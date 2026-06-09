package processor

import annotations.Extract
import annotations.Greeting
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 * Processador de anotações que procura por métodos anotados com @Extract
 * e gera uma classe final (Extractor) que implementa a lógica das Expressões Regulares.
 */
// regista automaticamente este processador no META-INF para que o compilador o consiga encontrar (via kapt)
@AutoService(Processor::class) // para o kapt o encontrar
// define a versão de Java suportada
@SupportedSourceVersion(SourceVersion.RELEASE_25)
// define que este processador acorda apenas quando encontra a anotação "annotations.Extract"
@SupportedAnnotationTypes("annotations.Extract") // procurar pela anotação Extract
class RegexProcessor : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        // dicionário (map) para agrupar as funções anotadas, dividindo-as pela classe a que pertencem
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()

        // procurar os elementos anotados com @Extract
        for (element in roundEnv.getElementsAnnotatedWith(Extract::class.java)) {
            // confirma que o elemento encontrado é um método
            if (element is ExecutableElement) {
                // descobre a classe e onde esse método está
                val enclosingClass = element.enclosingElement as TypeElement
                // adiciona este método à lista de métodos daquela classe no dicionário
                classMethodMap.computeIfAbsent(enclosingClass) { mutableListOf() }.add(element)
            }
        }

        // para cada classe encontrada com métodos @Extract, manda gerar a classe Extractor respetiva
        for ((classElement, methods) in classMethodMap) {
            generateKotlinExtractorClass(classElement, methods)
        }

        return true
    }

    private fun generateKotlinExtractorClass(classElement: TypeElement, methods: List<ExecutableElement>) {
        // procurar o nome do pacote onde a classe original está
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        // procurar o nome da classe original
        val ogClassName = classElement.simpleName.toString()
        // criar o nome para a classe gerada
        val extractorClassName = "${ogClassName}Extractor"

        // criar o construtor da classe DataProcessorExtractor
        val constructor = FunSpec.constructorBuilder() // criador do construtor
            .addParameter("input", String::class) // adicionar o parâmetro "input" cujo tipo é "String"
            .build() // criar

        // criador da classe
        val classBuilder = TypeSpec.classBuilder(extractorClassName) // atribuir o nome da classe, que será "DataProcessorExtractor"
            .primaryConstructor(constructor) // definir o construtor principal da classe
            .superclass(ClassName(packageName, ogClassName)) // define a classe a superclasse "DataProcessor"
            .addSuperclassConstructorParameter("input") // adicionar "input" como parâmetro da superclass
            .addModifiers(KModifier.PUBLIC) // adiciona a palavra chave "Public" antes do nome da classe

        // criar as funções que vão substituir as abstratas (getName e getAddress)
        for (method in methods) {
            val methodName = method.simpleName.toString()

            // vai ao método atual e pede ao compilador para procurar a anotação @Extract que está colada por cima dele
            val extractAnnotation = method.getAnnotation(Extract::class.java)
            val regexPattern = extractAnnotation.regex // aceder à propriedade 'regex'

            // constrói a função (ex: override fun getName(): String? { ... })
            val funcBuilder = FunSpec.builder(methodName)
                .addModifiers(KModifier.OVERRIDE) // adicionar override pois as funções vão sobrescrever os métodos abstratos
                .returns(String::class.asTypeName().copy(nullable = true)) // retorna "String?"
                // aqui injeta-se o Regex para procurar no texto
                .addStatement("val match = Regex(%S).find(input)", regexPattern) //"%S" placeholder onde é colocado o valor do regexPattern | input é o parâmetro que a classe recebe no construtor
                .addStatement("return match?.groupValues?.get(1)") // linha de return | usa "?." para evitar null pointer exceptions (retorna null se não houver match)
                // "get(1)" acede ao primeiro grupo de captura do regex (o que está entre parênteses) | ex.: regex "Name: (.+)" -> "Name: João" - "João" é o primeiro grupo

            classBuilder.addFunction(funcBuilder.build())
        }

        // preparar o ficheiro final
        val file = FileSpec.builder(packageName, extractorClassName)
            .addType(classBuilder.build())
            .build()

        // escrever o ficheiro na pasta de KAPT
        try {
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                file.writeTo(File(kaptKotlinGeneratedDir))
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(javax.tools.Diagnostic.Kind.ERROR, "Erro: ${e.message}")
        }
    }
}
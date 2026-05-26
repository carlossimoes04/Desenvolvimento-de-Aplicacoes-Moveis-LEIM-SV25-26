package processor
import annotations.Greeting
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Processador de anotações responsável por procurar por métodos anotados com @Greeting
 * e gerar classes Wrapper com a implementação estendida.
 */
// @AutoService regista o nosso processador no META-INF para ser reconhecido de imediato pelo compilador
@AutoService(Processor::class)
// @SupportedSourceVersion declara a compatibilidade da versão de Java a analisar
@SupportedSourceVersion(SourceVersion.RELEASE_25)
// @SupportedAnnotationTypes especifica exatamente a anotação que "acorda" este processador
@SupportedAnnotationTypes("annotations.Greeting")
// A classe herda de AbstractProcessor, tornando-se oficialmente num processador apto para o compilador
class GreetingProcessor : AbstractProcessor() {

    /**
     * Ponto de entrada do processamento
     *
     * É executado pelo compilador para varrer o código
     */
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Dicionário (Map) que guardará a relação: Classe onde foi encontrada a anotação -> Lista de métodos anotados
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()
        // roundEnv.getElementsAnnotatedWith procura por todos os elementos que possuem @Greeting no código todo
        for (element in roundEnv.getElementsAnnotatedWith(Greeting::class.java)) {
            // Garante que o elemento encontrado é realmente um método (ExecutableElement)
            if (element is ExecutableElement) {
                // Descobre a classe original (TypeElement) onde o método anotado está contido
                val enclosingClass = element.enclosingElement as TypeElement
                // Coloca o método na lista associada a essa classe dentro do dicionário
                classMethodMap.computeIfAbsent(enclosingClass) {
                    mutableListOf()
                }.add(element)
            }
        }

        // Itera sobre as classes encontradas e as respetivas listas de métodos para gerar os novos ficheiros
        for ((classElement, methods) in classMethodMap) {
            generateKotlinWrapperClass(classElement, methods)
        }

        // Retorna 'true' para sinalizar ao compilador que esta anotação já foi processada e tratada
        return true
    }

    /**
     * Método auxiliar que constrói o código da classe Wrapper e o guarda num novo ficheiro .kt
     */
    private fun generateKotlinWrapperClass(classElement: TypeElement, methods: List<ExecutableElement>) {
        // Extrai o nome do package da classe original (ex: "app")
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        // Extrai o nome da classe original (ex: "MyClass")
        val originalClassName = classElement.simpleName.toString()
        // Define o nome da classe a gerar juntando o sufixo Wrapper (ex: "MyClassWrapper")
        val wrapperClassName = "${originalClassName}Wrapper"

        // Criação e configuração da classe Wrapper
        val classBuilder = TypeSpec.classBuilder(wrapperClassName)
            // Adiciona o construtor primário para receber uma instância da classe original
            .primaryConstructor(
                FunSpec.constructorBuilder().addParameter("original",
                ClassName(packageName, originalClassName)).build()
            )
            // Declaração da propriedade global "original" dentro da classe Wrapper para a podermos invocar
            .addProperty(
                PropertySpec.builder("original", ClassName(packageName, originalClassName))
                    .initializer("original")
                    .build()
            )
            // Modificadores da classe: será pública (PUBLIC) e final (não pode ser herdada)
            .addModifiers(KModifier.PUBLIC, KModifier.FINAL)

        // Criação dos métodos replicados
        for (method in methods) {
            // Guarda o nome do método original (ex: "sayHello")
            val methodName = method.simpleName.toString()
            // Vai ler e reconstruir os mesmos parâmetros de input para manter a assinatura do método gerado igual
            val parameters = method.parameters.map { param ->
                ParameterSpec.builder(param.simpleName.toString(),
                    param.asType().asTypeName()).build()
            }
            // Agrupa os nomes dos argumentos originais numa só string, separados por vírgulas para a chamada final
            val arguments = method.parameters.joinToString(", ") {
                it.simpleName.toString() }
            // Lê explicitamente o conteúdo atribuído a @Greeting("aqui_dentro")
            // Se estiver nulo (erro), usa "Hello!"
            val greetingMessage =
                method.getAnnotation(Greeting::class.java)?.message ?:
                "Hello!"

            // Constrói a sintaxe final do método novo
            val methodBuilder = FunSpec.builder(methodName)
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL) // define o método novo como public
                .addParameters(parameters) // adiciona os argumentos espelho
                .addStatement("println(%S)", greetingMessage) // primeiro passo da lógica injetada: escreve no ecrã a saudação
                .addStatement("original.$methodName($arguments)") // segundo passo da lógica injetada: encaminha de volta para o original
            classBuilder.addFunction(methodBuilder.build()) // anexa o método acabado de construir à classe Wrapper
        }

        // Junta o nome do package e a classe construída num objeto final FileSpec
        val file = FileSpec.builder(packageName, wrapperClassName)
            .addType(classBuilder.build())
            .build()

        // Escrever e guardar o ficheiro
        try {
            val kaptKotlinGeneratedDir =
                processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                file.writeTo(File(kaptKotlinGeneratedDir)) // Correct way to write Kotlin files
            } else {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "kapt.kotlin.generated not found")
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Error generating Kotlin file: ${e.message}")
        }
    }
}
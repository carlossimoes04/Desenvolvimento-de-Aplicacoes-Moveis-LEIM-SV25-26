package processor

import annotations.Extract
import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class) // para o kapt o encontrar
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedAnnotationTypes("annotations.Extract") // procurar pela anotação Extract
class RegexProcessor : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Procurar os elementos anotados com @Extract
        // Agrupá-los por classe (como fizeste no GreetingProcessor)
        // Chamar a tua função que usa o KotlinPoet para gerar o DataProcessorExtractor



        return true
    }
}
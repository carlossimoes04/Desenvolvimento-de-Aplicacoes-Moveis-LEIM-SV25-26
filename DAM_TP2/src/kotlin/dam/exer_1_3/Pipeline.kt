package dam.exer_1_3

class Pipeline {

    // guarda-se um "pair" (par) onde o 1º elemento é a String (nome)
    // e o 2º elemento é a função de transformação
    private val listOfSteps = mutableListOf<Pair<String, (List<String>) -> List<String>>>()
    // a utilização do Pair permitiu a criação de um Stage sem ter a necessidade de criar a data class Stage

    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        // a palavra "to" em Kotlin cria automaticamente um Pair(A, B)
        listOfSteps.add(name to transform)
    }

    fun execute(input: List<String>): List<String> {
        var current = input
        listOfSteps.forEach { step ->
            // step.second acede ao 2º elemento do par (a função transform)
            current = step.second(current)
        }
        return current
    }

    fun describe() {
        listOfSteps.forEach { step ->
            // step.first acede ao 1º elemento do par (o nome)
            println(step.first)
        }
    }
}

// uma função top-level é uma função que está fora da própria classe
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    return Pipeline().apply(block)
}
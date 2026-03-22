package dam.exer_1_3

fun <A, B, C> ((A) -> B).andThen(other: (B) -> C): (A) -> C = { a: A -> other(this(a)) }
// baseado em: https://www.youtube.com/watch?v=47NYovu2VaQ
/*
O objetivo do andThen é combinar as ações de duas funções, f.andThen(g). Esta retorna uma função que aplica f and depois
aplica g ao resultado.
 */

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

    fun compose(nameA: String, nameB: String, newName: String) {
        val step1 = listOfSteps.find { it.first == nameA }
        val step2 = listOfSteps.find { it.first == nameB }

        val funcao1 = step1?.second ?: return
        val funcao2 = step2?.second ?: return

        val combinada = funcao1.andThen(funcao2)

        addStage(newName, combinada)
    }

    fun fork(input: List<String>, other: Pipeline): Pair<List<String>, List<String>> {
        val resultadoA = this.execute(input)
        val resultadoB = other.execute(input)
        return Pair(resultadoA, resultadoB)
    }
}

// uma função top-level é uma função que está fora da própria classe
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    return Pipeline().apply(block)
}

fun main() {

    val logs = listOf(
        "   system started   ",
        " error : disk full ",
        "   user logged in   ",
        " ERROR : OUT OF MEMORY ",
        " error : connection timeout "
    )

    val myPipeline = buildPipeline {
        addStage("Trim") { list ->
            list.map { it.trim() }
        }

        addStage("Filter errors") { list ->
            list.filter { it.contains("error", ignoreCase = true) }
        }

        addStage("Uppercase") { list ->
            list.map { it.uppercase() }
        }

        addStage("Add index") { list ->
            list.mapIndexed { index, line -> "${index + 1}. $line" }
        }
    }

    println("--- Pipeline Stages ---")
    myPipeline.describe()

    println("\n--- Execution Result ---")
    val processedLogs = myPipeline.execute(logs)

    processedLogs.forEach { println(it) }

    println("\n")
    myPipeline.compose("Trim", "Filter errors", "TrimAndFilter")
    myPipeline.describe()

    val pipeline2 = buildPipeline {
        addStage("Trim") { list ->
            list.map { it.trim() }
        }

        addStage("Filter errors") { list ->
            list.filter { it.contains("error", ignoreCase = true) }
        }
    }

    val resultado = myPipeline.fork(logs, pipeline2)
    println(resultado.first)  // resultado do myPipeline
    println(resultado.second)  // resultado do pipeline2
}
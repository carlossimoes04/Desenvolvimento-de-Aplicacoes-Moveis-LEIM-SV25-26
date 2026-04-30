package dam.exer_1_3

/**
 * Extensão da função (A) -> B que compõe duas funções em sequência.
 *
 * f.andThen(g) devolve uma nova função que primeiro aplica f ao argumento,
 * e depois aplica g ao resultado de f. Equivalente à composição matemática g(f(a)).
 *
 * @param A Tipo do argumento da primeira função.
 * @param B Tipo do resultado da primeira função e argumento da segunda.
 * @param C Tipo do resultado da segunda função.
 * @param other Função a aplicar após a função atual.
 * @return Nova função que representa a composição das duas.
 */
fun <A, B, C> ((A) -> B).andThen(other: (B) -> C): (A) -> C = { a: A -> other(this(a)) }
// baseado em: https://www.youtube.com/watch?v=47NYovu2VaQ


/**
 * Pipeline que processa uma lista de strings através de
 * uma sequência ordenada de estágios de transformação.
 *
 * Cada estágio recebe a lista resultante do estágio anterior e devolve
 * uma nova lista transformada, permitindo encadear operações de forma modular.
 */
class Pipeline {

    // cada entrada do Pair guarda o nome do estágio (String) e a sua função de transformação
    // a utilização do Pair evita a necessidade de criar uma data class Stage separada
    private val listOfSteps = mutableListOf<Pair<String, (List<String>) -> List<String>>>()

    /**
     * Acrescenta um novo estágio de transformação ao fim da pipeline.
     *
     * @param name Nome descritivo do estágio, usado em [describe].
     * @param transform Função que recebe a lista atual e devolve a lista transformada.
     */
    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        // "to" cria automaticamente um Pair(name, transform)
        listOfSteps.add(name to transform)
    }

    /**
     * Executa todos os estágios da pipeline pela ordem em que foram adicionados.
     *
     * A lista de entrada é passada ao primeiro estágio, cujo resultado é passado
     * ao segundo, e assim sucessivamente até ao último estágio.
     *
     * @param input Lista de strings a processar.
     * @return Lista resultante após todos os estágios terem sido aplicados.
     */
    fun execute(input: List<String>): List<String> {
        var current = input // começa com a lista original
        listOfSteps.forEach { step ->
            current = step.second(current) // step.second acede à função de transformação do Pair
        }
        return current // devolve a lista final após todos os estágios
    }

    /**
     * Imprime os nomes de todos os estágios da pipeline pela ordem de execução.
     */
    fun describe() {
        listOfSteps.forEach { step ->
            println(step.first) // step.first acede ao nome do estágio do Pair
        }
    }

    /**
     * Compõe dois estágios existentes num único novo estágio, usando [andThen].
     *
     * O novo estágio aplica nameA seguido de nameB, e é adicionado ao fim da pipeline.
     * Se algum dos nomes não existir na pipeline, a função não faz nada e termina.
     *
     * @param nameA Nome do primeiro estágio a compor.
     * @param nameB Nome do segundo estágio a compor.
     * @param newName Nome do novo estágio composto resultante.
     */
    fun compose(nameA: String, nameB: String, newName: String) {
        val step1 = listOfSteps.find { it.first == nameA } // procura o estágio com o nome nameA
        val step2 = listOfSteps.find { it.first == nameB } // procura o estágio com o nome nameB

        // o operador ?: devolve o valor da direita se o da esquerda for null
        // se algum dos estágios não existir, termina a função sem fazer nada
        val funcao1 = step1?.second ?: return
        val funcao2 = step2?.second ?: return

        val combinada = funcao1.andThen(funcao2) // compõe as duas funções em sequência com andThen

        addStage(newName, combinada) // adiciona o novo estágio composto à pipeline
    }

    /**
     * Executa o mesmo input em duas pipelines independentes e devolve ambos os resultados.
     *
     * Permite comparar ou processar o mesmo conjunto de dados por dois caminhos diferentes.
     *
     * @param input Lista de strings a processar em ambas as pipelines.
     * @param other Pipeline alternativa a executar com o mesmo input.
     * @return Par onde o primeiro elemento é o resultado desta pipeline
     *         e o segundo é o resultado da pipeline [other].
     */
    fun fork(input: List<String>, other: Pipeline): Pair<List<String>, List<String>> {
        val resultadoA = this.execute(input) // executa a pipeline atual
        val resultadoB = other.execute(input) // executa a pipeline alternativa com o mesmo input
        return Pair(resultadoA, resultadoB) // devolve os dois resultados num Pair
    }
}

/**
 * Função top-level (fora de qualquer classe) que constrói e devolve uma [Pipeline]
 * configurada através de uma lambda com receiver.
 *
 * Uma lambda com receiver (Pipeline.() -> Unit) permite chamar diretamente os métodos
 * da Pipeline dentro do bloco, como se estivéssemos dentro da própria classe.
 * O apply executa o bloco sobre a instância criada e devolve-a.
 *
 * @param block Lambda com receiver que configura a pipeline através de [Pipeline.addStage].
 * @return Pipeline configurada e pronta a executar.
 */
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    // Pipeline.() -> Unit significa: uma lambda com um receiver do tipo Pipeline, que não devolve nada (Unit)
    // dentro deste bloco (block), a instância de Pipeline passa a ser o "this" implícito
    // isto permite chamar métodos da Pipeline (como addStage) diretamente dentro das chavetas,
    // sem se precisar de referenciar o objeto explicitamente (ex: it.addStage())
    return Pipeline().apply(block)
}

fun main() {

    val logs = listOf(
        " system started ",
        " error : disk full ",
        " user logged in ",
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
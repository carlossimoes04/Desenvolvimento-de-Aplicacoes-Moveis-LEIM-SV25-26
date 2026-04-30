package dam.exer_1_2

/**
 * Cache genérica em memória que associa chaves a valores de qualquer tipo.
 *
 * O parâmetro K representa o tipo da chave e V o tipo do valor.
 * A restrição ': Any' garante que nem K nem V podem ser null,
 * evitando ambiguidade entre "chave não existe" e "chave com valor null".
 *
 * @param K Tipo da chave. Não pode ser null.
 * @param V Tipo do valor. Não pode ser null.
 */
class Cache <K: Any, V: Any> {

    // MutableMap é a estrutura interna que guarda os pares chave-valor
    // é private para que o exterior não possa modificar a cache diretamente
    private val entries: MutableMap<K, V> = mutableMapOf()

    /**
     * Insere ou substitui um valor associado à chave indicada.
     *
     * @param key Chave a inserir ou atualizar.
     * @param value Valor a associar à chave.
     */
    fun put(key: K, value: V) {
        entries[key] = value // se a key já existir, o valor é substituído
    }

    /**
     * Devolve o valor associado à chave, ou null se a chave não existir.
     *
     * @param key Chave a procurar.
     * @return Valor associado à chave, ou null se não existir.
     */
    fun get(key: K): V? {
        return entries[key] // devolve null automaticamente se a key não existir
    }

    /**
     * Remove a entrada associada à chave indicada.
     *
     * @param key Chave a remover.
     * @return Valor que estava associado à chave, ou null se não existia.
     */
    fun evict(key: K): V? {
        return entries.remove(key) // remove a entrada e devolve o valor que lá estava
    }

    /**
     * Devolve o número de entradas atualmente armazenadas na cache.
     *
     * @return Número de pares chave-valor existentes.
     */
    fun size(): Int {
        return entries.size // acede à propriedade size do MutableMap
    }

    /**
     * Devolve o valor associado à chave, ou calcula e armazena um novo valor
     * caso a chave não exista.
     *
     * @param key Chave a procurar ou inserir.
     * @param default Lambda invocada para calcular o valor caso a chave não exista.
     * @return Valor existente ou recém-calculado.
     *
     * Baseado em: https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/src/kotlin/collections/Maps.kt, linhas
     * 439 a 468
     */
    fun getOrPut(key: K, default: () -> V): V {
        val currentValue = get(key) // obtém o valor atual associado à chave utilizando o get(key) criado anteriormente
        if (currentValue != null) { // caso a chave exista
            return currentValue // retorna o valor associado
        } else { // se não existir
            put(key, default()) // atribui-se o valor chamando a função lambda default()
            return default() // retorna o novo valor
        }
    }

    /**
     * Aplica uma transformação ao valor associado à chave, caso esta exista.
     *
     * @param key Chave cujo valor se pretende transformar.
     * @param action Lambda que recebe o valor atual e devolve o novo valor.
     * @return true se a chave existia e a transformação foi aplicada, false caso contrário.
     */
    fun transform(key: K, action: (V) -> V): Boolean {
        val value = get(key) // obtém o valor atual associado à key
        if (value != null) { // se a key existir
            put(key, action(value)) // aplica a lambda action ao valor atual e guarda o resultado
            return true // transformação aplicada com sucesso
        } else { // se a key não existir
            return false // transformação não foi aplicada
        }
    }

    /**
     * Devolve uma cópia imutável do estado atual da cache.
     *
     * O caller não consegue modificar a cache através do mapa devolvido,
     * pois Map não expõe operações de escrita ao contrário de MutableMap.
     *
     * @return Cópia imutável de todas as entradas da cache.
     */
    fun snapshot(): Map<K, V> {
        return entries.toMap() // Map é immuttable ao contrário do MuttableMap, então usa-se toMap para converter
    }

    /**
     * Devolve uma cópia imutável contendo apenas as entradas cujos valores
     * satisfazem o predicado indicado.
     *
     * @param predicate Lambda que recebe um valor e devolve true se deve ser incluído.
     * @return Mapa imutável com as entradas filtradas.
     */
    fun filterValues(predicate: (V) -> Boolean): Map<K, V> {
        // filter itera todas as entradas e mantém apenas as que satisfazem o predicado
        val resultado = entries.filter {
            entry -> predicate(entry.value)
        }
        return resultado.toMap() // converte para Map imutável antes de devolver
    }
}

fun main() {
    // --- Word frequency cache ---
    println("--- Word frequency cache ---")

    val wordCache = Cache<String, Int>()
    wordCache.put("kotlin", 1)
    wordCache.put("scala", 1)
    wordCache.put("haskell", 1)

    println("Size: ${wordCache.size()}")
    println("Frequency of \"kotlin\": ${wordCache.get("kotlin")}")

    println("getOrPut \"kotlin\": ${wordCache.getOrPut("kotlin") { 0 }}")
    println("getOrPut \"java\": ${wordCache.getOrPut("java") { 0 }}")
    println("Size after getOrPut: ${wordCache.size()}")

    println("Transform \"kotlin\" (+1): ${wordCache.transform("kotlin") { it + 1 }}")
    println("Transform \"cobol\" (+1): ${wordCache.transform("cobol") { it + 1 }}")

    println("Snapshot: ${wordCache.snapshot()}")
    println("")

    // --- Id registry cache ---
    println("--- Id registry cache ---")

    val idCache = Cache<Int, String>()
    idCache.put(1, "Alice")
    idCache.put(2, "Bob")

    println("Id 1 -> ${idCache.get(1)}")
    println("Id 2 -> ${idCache.get(2)}")

    idCache.evict(1)
    println("After evict id 1, size: ${idCache.size()}")
    println("Id 1 after evict -> ${idCache.get(1)}")

    println("")

    // --- filterValues test ---
    println("Count greater than zero: ${wordCache.filterValues { count -> count > 0 }}")
}
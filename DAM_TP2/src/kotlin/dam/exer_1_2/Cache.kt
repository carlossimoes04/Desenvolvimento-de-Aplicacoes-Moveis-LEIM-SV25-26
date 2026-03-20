package dam.exer_1_2

class Cache <K: Any, V: Any> (){
    private val entries: MutableMap<K, V> = mutableMapOf()

    fun put(key: K, value: V) {
        entries[key] = value
    }

    fun get(key: K): V? {
        return entries[key]
    }

    fun evict(key: K): V? {
        return entries.remove(key)
    }

    fun size(): Int {
        return entries.size
    }

    fun getOrPut(key: K, default: () -> V): V {
        val currentValue = get(key) // valor atual
        if (currentValue != null) { // se a key existir
            return currentValue // retorna o seu valor
        } else { // se não existir
            put(key, default()) // atribui-se o valor chamando a função lambda default()
            return default() // retorna o novo valor
        }
    }

    fun transform(key: K, action: (V) -> V): Boolean {
        val value = get(key) // valor atual
        if (value != null) { // se a key existir
            put(key, action(value)) // aplicar a action
            return true
        } else {
            return false
        }
    }

    fun snapshot(): Map<K, V> {
        return entries.toMap() // Map é immuttable ao contrário do MuttableMap, então usa-se toMap para converter
    }

    fun filterValues(predicate: (V) -> Boolean): Map<K, V> {
        val resultado = entries.filter {
            entry -> predicate(entry.value)
        }
        // filtra-se todas as entradas que satisfaçam o predicate
        return resultado.toMap() // retorna o immutable map
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
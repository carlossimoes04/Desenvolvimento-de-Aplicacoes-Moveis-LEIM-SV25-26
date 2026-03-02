package dam.exer_vl

class PhysicalBook (title: String, author: String, year: Int, initialCopies: Int,
                    val weight: Double, val hasHardcover: Boolean) : Book(title, author, year, initialCopies){

    fun doesItHaveHardcover(): String = if (hasHardcover) "It has hardcover" else "It doesn't have a hardcover"

    override fun toString(): String {
        return """
            ${super.toString()}
            ${getStorageInfo()}
        """.trimIndent()
    }

    override fun getStorageInfo(): String {
        return """
        -> Weight: ${weight}Kg
        -> Cover: ${doesItHaveHardcover()}
        """.trimIndent()
    }
}
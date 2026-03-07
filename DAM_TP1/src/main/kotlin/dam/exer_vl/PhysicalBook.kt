package dam.exer_vl

/**
 * Represents a physical book with weight and cover information.
 *
 * @property weight Book weight in kilograms.
 * @property hasHardcover Whether the book has a hardcover.
 */
class PhysicalBook (title: String, author: String, year: Int, initialCopies: Int,
                    val weight: Double, val hasHardcover: Boolean) : Book(title, author, year, initialCopies){

    /**
     * Returns a short, human-readable message indicating cover type.
     */
    fun doesItHaveHardcover(): String = if (hasHardcover) "It has hardcover" else "It doesn't have a hardcover"

    /**
     * Returns base book details plus physical storage details.
     */
    override fun toString(): String {
        return """
            ${super.toString()}
            ${getStorageInfo()}
        """.trimIndent()
    }

    /**
     * Provides storage-related information for this physical book.
     */
    override fun getStorageInfo(): String {
        return """Weight: ${weight}Kg; Cover: ${doesItHaveHardcover()}"""
    }
}
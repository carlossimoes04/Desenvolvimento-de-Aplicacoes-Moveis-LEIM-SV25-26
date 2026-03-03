package dam_a51696.virtuallibrary

/**
 * Represents a physical, printed book in the virtual library.
 * This class extends the base [Book] class by incorporating physical attributes
 * such as weight and the type of cover (hardcover or paperback).
 *
 * @param title The title of the physical book.
 * @param author The author of the physical book.
 * @param year The original year the book was published.
 * @param initialCopies The total number of physical copies initially available.
 * @property weight The physical weight of the book in kilograms (Kg).
 * @property hasHardcover A boolean indicating whether the book has a hardcover (`true`)
 * or is a paperback (`false`).
 */
class PhysicalBook (title: String, author: String, year: Int, initialCopies: Int,
                    val weight: Double, val hasHardcover: Boolean) : Book(title, author, year, initialCopies){

    /**
     * Helper function to translate the boolean [hasHardcover] property into a
     * human-readable descriptive string.
     *
     * @return "It has hardcover" if true, or "It doesn't have a hardcover" if false.
     */
    fun doesItHaveHardcover(): String = if (hasHardcover) "It has hardcover" else "It doesn't have a hardcover"

    /**
     * Returns a formatted string representation of the physical book.
     * It combines the common book details from the base class with its
     * specific physical storage information.
     *
     * @return A multi-line string containing all the physical book's details.
     */
    override fun toString(): String {
        return """
            ${super.toString()}
            ${getStorageInfo()}
        """.trimIndent()
    }

    /**
     * Retrieves the storage information specific to a physical book.
     * * @return A string detailing the weight in Kg and the cover type.
     */
    override fun getStorageInfo(): String {
        return """Weight: ${weight}Kg; Cover: ${doesItHaveHardcover()}"""
    }
}
package dam_a51696.virtuallibrary

/**
 * Represents an abstract base class for a Book in the virtual library.
 * This class holds the common properties and behaviors shared by all types of books
 * (e.g., physical or digital) and cannot be instantiated directly.
 *
 * @property title The title of the book.
 * @property author The author of the book.
 * @property year The original year the book was published.
 * @property initialCopies The total number of copies initially added to the library.
 */
abstract class Book constructor(val title: String, val author: String, val year: Int, val initialCopies: Int) {

    /**
     * Internal property to hold the year for categorization purposes.
     */
    private val _publicationYear: Int = year

    /**
     * Categorizes the book's era based on its publication year.
     * * @return "Classic" if published before 1980,
     * "Modern" if published between 1980 and 2010,
     * "Contemporary" if published after 2010.
     */
    val publicationYear: String
        get() = when {
            _publicationYear < 1980 -> "Classic"
            _publicationYear in 1980..2010 -> "Modern"
            else -> "Contemporary"
        }

    /**
     * Tracks the current number of available copies in the library.
     * * The custom setter includes a safety check to prevent the number of
     * available copies from dropping below zero. If a negative value is passed,
     * the update is simply ignored.
     */
    var availableCopies: Int = initialCopies
        set(value) {
            when {
                value < 0 -> {
                    return
                }
                value == 0 -> {
                    field = value
                }
                else -> field = value
            }
        }

    /**
     * Returns a formatted string representation of the book's details.
     * * @return A multi-line string containing the book's title, author,
     * publication year (both exact and categorized), and current stock.
     */
    override fun toString(): String {
        return """
            Book:
            -> Title: $title
            -> Author: $author
            -> Publication Year: $year; $publicationYear
            -> Available Copies: $availableCopies/$initialCopies
            """.trimIndent()
    }

    /**
     * Abstract method to retrieve specific storage information about the book.
     * Subclasses must provide their own implementation (e.g., file size for
     * digital books, or physical weight for printed books).
     * * @return A string containing the storage details.
     */
    abstract fun getStorageInfo(): String
}
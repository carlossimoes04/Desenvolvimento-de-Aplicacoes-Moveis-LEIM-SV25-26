package dam.exer_vl

/**
 * Base abstraction for books managed by the virtual library.
 *
 * @property title Book title.
 * @property author Book author.
 * @property year Numeric publication year.
 * @property initialCopies Number of copies initially available in stock.
 */
abstract class Book constructor(val title: String, val author: String, val year: Int, val initialCopies: Int) {
    
    private val _publicationYear: Int = year

    /**
     * Publication period label derived from [year].
     *
     * Returns `Classic` for years before 1980, `Modern` for 1980..2010,
     * and `Contemporary` for later years.
     */
    val publicationYear: String
        get() = when {
            _publicationYear < 1980 -> "Classic"
            _publicationYear in 1980..2010 -> "Modern"
            else -> "Contemporary"
        }
    
    /**
     * Current number of copies available for borrowing.
     *
     * Negative values are rejected, and setting the value to `0` emits an
     * out-of-stock warning.
     */
    var availableCopies: Int = initialCopies
        set(value) {
            when {
                value < 0 -> {
                    println("Error: Cannot set negative copies. Keeping current value.")
                    return
                }
                value == 0 -> {
                    field = value
                    println("Warning: Book is now out of stock!")
                }
                else -> field = value
            }
        }

    /**
     * Returns a multi-line, human-readable summary of the book metadata and stock.
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
     * Provides implementation-specific storage details.
     *
     * @return Description of where or how this book is stored.
     */
    abstract fun getStorageInfo(): String

    init {
        println("Book created: '$title' by $author")
    }
}
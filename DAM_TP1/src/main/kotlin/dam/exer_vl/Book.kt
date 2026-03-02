package dam.exer_vl

abstract class Book constructor(val title: String, val author: String, val year: Int, val initialCopies: Int) {
    
    private val _publicationYear: Int = year
    val publicationYear: String
        get() = when {
            _publicationYear < 1980 -> "Classic"
            _publicationYear in 1980..2010 -> "Modern"
            else -> "Contemporary"
        }
    
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

    override fun toString(): String {
        return """
            Book:
            -> Title: $title
            -> Author: $author
            -> Publication Year: $year; $publicationYear
            -> Available Copies: $availableCopies/$initialCopies
            """.trimIndent()
    }

    abstract fun getStorageInfo(): String

    init {
        println("Book created: '$title' by $author")
    }
}
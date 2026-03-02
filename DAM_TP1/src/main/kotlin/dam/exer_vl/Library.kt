package dam.exer_vl

class Library {
    // used mutable list instead of list because lists can't be modified after their creation
    private val books: MutableList<Book> = mutableListOf()

    /**
     * Adds a new book to the library collection.
     */
    fun addBook(book: Book) {
        books.add(book)
        println("Book '${book.title}' has been added to the library.")
    }

    /**
     * Borrows a book from the library by title.
     * Decreases availableCopies by 1 if the book is found and copies are available.
     */
    fun borrowBook(title: String) {
        val book = books.find { it.title.equals(title, ignoreCase = true) }
        
        when {
            book == null -> println("Error: Book '$title' not found in the library.")
            book.availableCopies <= 0 -> println("Error: Book '$title' is not available. No copies left.")
            else -> {
                book.availableCopies--
                println("Success: Book '$title' has been borrowed. Remaining copies: ${book.availableCopies}")
            }
        }
    }

    /**
     * Returns a book to the library by title.
     * Increases availableCopies by 1 if the book is found.
     */
    fun returnBook(title: String) {
        val book = books.find { it.title.equals(title, ignoreCase = true) }
        
        when {
            book == null -> println("Error: Book '$title' not found in the library.")
            else -> {
                book.availableCopies++
                println("Confirmation: Book '$title' has been returned. Total copies: ${book.availableCopies}")
            }
        }
    }

    /**
     * Displays details of all books in the library.
     */
    fun showBooks() {
        if (books.isEmpty()) {
            println("The library is empty. No books available.")
            return
        }
        
        println("\nLibrary Books: ")
        books.forEachIndexed { index, book ->
            println("${index + 1}. Title: '${book.title}'")
            println("   Author: ${book.author}")
            println("   Year: ${book.year} (${book.publicationYear})")
            println("   Available Copies: ${book.availableCopies}")
            println()
        }
    }

    /**
     * Searches and displays all books by a specific author.
     */
    fun searchByAuthor(author: String) {
        val authorBooks = books.filter { it.author.equals(author, ignoreCase = true) }
        
        when {
            authorBooks.isEmpty() -> println("No books found by author '$author'.")
            else -> {
                println("\nBooks by $author:")
                authorBooks.forEach { book ->
                    println("-> '${book.title}' (${book.year}) - Available: ${book.availableCopies} copies")
                }
                println()
            }
        }
    }
}
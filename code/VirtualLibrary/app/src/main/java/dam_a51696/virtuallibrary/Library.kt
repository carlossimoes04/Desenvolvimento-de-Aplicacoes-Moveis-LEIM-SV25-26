package dam_a51696.virtuallibrary

/**
 * Represents a virtual library that manages a collection of [Book] objects.
 * This class provides functionalities to add, borrow, return, and search for books.
 * It also tracks the global number of books created across all library instances.
 *
 * @property name The official name of the library instance.
 */
class Library(val name: String) {

    /**
     * Internal mutable list storing all the books currently registered in this library.
     */
    private val books: MutableList<Book> = mutableListOf()

    /**
     * Adds a new book to the library's collection and increments the global book counter.
     *
     * @param book The [Book] object (e.g., DigitalBook or PhysicalBook) to be added.
     * @return A string confirming that the book has been successfully added.
     */
    fun addBook(book: Book): String {
        books.add(book)
        globalTotalBooksCreated++
        return "Book '${book.title}' has been added to the library."
    }

    /**
     * Attempts to borrow a book from the library using its title.
     * The search is case-insensitive. If successful, it decreases the available copies by 1.
     *
     * @param title The exact title of the book the user wants to borrow.
     * @return A status message indicating success, an error (if not found or out of stock),
     * or a warning if it was the last available copy.
     */
    fun borrowBook(title: String): String {
        val book = books.find { it.title.equals(title, ignoreCase = true) }

        return when {
            book == null -> "Error: Book '$title' not found in the library."
            book.availableCopies <= 0 -> "Error: Book '$title' is not available. No copies left."
            else -> {
                book.availableCopies--
                var mensagem = "Success: Book '$title' has been borrowed. Remaining copies: ${book.availableCopies}"
                if (book.availableCopies == 0) {
                    mensagem += "\nWarning: Book is now out of stock!"
                }
                mensagem
            }
        }
    }

    /**
     * Processes the return of a borrowed book to the library.
     * The search is case-insensitive. If successful, it increases the available copies by 1.
     *
     * @param title The title of the book being returned.
     * @return A status message confirming the return or an error if the book doesn't belong here.
     */
    fun returnBook(title: String): String {
        val book = books.find { it.title.equals(title, ignoreCase = true) }

        return if (book != null) {
            book.availableCopies++
            "Success: Book '${book.title}' returned. Total copies: ${book.availableCopies}"
        } else {
            "Error: Book '$title' does not belong to this library."
        }
    }

    /**
     * Retrieves a formatted list of all the books currently stored in the library.
     *
     * @return A multi-line string containing the details of every book, or a message
     * stating that the library is empty.
     */
    fun showBooks(): String {
        if (books.isEmpty()) {
            return "The library is empty. No books available."
        }

        return buildString {
            append("\nLibrary Books:\n")
            books.forEachIndexed { index, book ->
                append("${index + 1}. Title: '${book.title}'\n")
                append("   Author: ${book.author}\n")
                append("   Year: ${book.year} (${book.publicationYear})\n")
                append("   Available Copies: ${book.availableCopies}\n")
                append("   Storage: ${book.getStorageInfo()}\n\n")
            }
        }
    }

    /**
     * Searches for all books written by a specific author.
     * The search is case-insensitive.
     *
     * @param author The name of the author to search for.
     * @return A formatted string listing all books by that author, or a message
     * indicating that no books were found.
     */
    fun searchByAuthor(author: String): String {
        val authorBooks = books.filter { it.author.equals(author, ignoreCase = true) }

        return if (authorBooks.isEmpty()) {
            "No books found by author '$author'."
        } else {
            buildString {
                append("\nBooks by $author:\n")
                authorBooks.forEach { book ->
                    append("-> '${book.title}' (${book.year}) - Available: ${book.availableCopies} copies\n")
                }
            }
        }
    }

    /**
     * Companion object acting as a global state manager for the library system.
     */
    companion object {
        /**
         * Tracks the total number of books added across all [Library] instances globally.
         */
        private var globalTotalBooksCreated: Int = 0

        /**
         * Retrieves the total count of books created in the entire system.
         *
         * @return The integer value of total books registered.
         */
        fun getTotalBooksCreated(): Int {
            return globalTotalBooksCreated
        }
    }
}
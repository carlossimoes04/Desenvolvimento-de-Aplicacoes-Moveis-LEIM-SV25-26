package dam_a51696.virtuallibrary

class Library(val name: String) {

    private val books: MutableList<Book> = mutableListOf()

    fun addBook(book: Book): String {
        books.add(book)
        globalTotalBooksCreated++
        return "Book '${book.title}' has been added to the library."
    }

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

    fun returnBook(title: String): String {
        val book = books.find { it.title.equals(title, ignoreCase = true) }

        return if (book != null) {
            book.availableCopies++
            "Success: Book '${book.title}' returned. Total copies: ${book.availableCopies}"
        } else {
            "Error: Book '$title' does not belong to this library."
        }
    }

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

    companion object {
        private var globalTotalBooksCreated: Int = 0
        fun getTotalBooksCreated(): Int {
            return globalTotalBooksCreated
        }
    }
}
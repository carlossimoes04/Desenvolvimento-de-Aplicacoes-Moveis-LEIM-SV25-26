package dam.exer_vl

/**
 * Demonstrates virtual library features by creating books, borrowing/returning
 * items, searching by author, and printing summary information.
 */
fun main() {
    val library = Library("Central Library")

    val digitalBook = DigitalBook(
        "Kotlin in Action",
        "Dmitry Jemerov",
        2017,
        5,
        4.5,
        "PDF"
    )

    val physicalBook = PhysicalBook(
        "Clean Code",
        "Robert C. Martin",
        2008,
        3,
        650.0,
        true
    )

    val classicBook = PhysicalBook(
        "1984",
        "George Orwell",
        1949,
        2,
        400.0,
        false
    )

    library.addBook(digitalBook)
    library.addBook(physicalBook)
    library.addBook(classicBook)

    library.showBooks()

    println("\n--- Borrowing Books ---")
    library.borrowBook("Clean Code")
    library.borrowBook("1984")
    library.borrowBook("1984")
    library.borrowBook("1984") // Should fail - no copies left

    println("\n--- Returning Books ---")
    library.returnBook("1984")

    println("\n--- Search by Author ---")
    library.searchByAuthor("George Orwell")

    println("\n--- Testing LibraryMember (Data Class) ---")
    val member = LibraryMember("Ana Duarte", 1001, mutableListOf("Clean Code", "1984", "Kotlin in Action"))
    println(member)

    println("\n--- Testing Companion Object ---")
    println("Total books added across all libraries: ${Library.getTotalBooksCreated()}")
}
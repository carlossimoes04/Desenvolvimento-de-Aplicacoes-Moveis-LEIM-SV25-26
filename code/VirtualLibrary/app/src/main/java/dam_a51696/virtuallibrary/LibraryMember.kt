package dam_a51696.virtuallibrary

/**
 * Represents a registered member of the virtual library.
 * Implemented as a Kotlin `data class` since its primary purpose is to hold data.
 * This automatically generates standard utility functions for the class, such as
 * a readable `toString()`, `equals()`, `hashCode()`, and `copy()`.
 *
 * @property name The full name of the library member.
 * @property membershipId A unique numerical identifier assigned to the member.
 * @property borrowedBooks A mutable list containing the exact titles of the books
 * currently borrowed by this member. It is mutable (`var`) so it can be updated
 * whenever the member borrows or returns a book.
 */
data class LibraryMember(val name: String, val membershipId: Int, var borrowedBooks: MutableList<String>) {
}
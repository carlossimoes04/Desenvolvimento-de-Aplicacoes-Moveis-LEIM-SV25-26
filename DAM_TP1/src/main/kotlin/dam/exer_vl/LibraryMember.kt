package dam.exer_vl

/**
 * Represents a library member and their currently borrowed books.
 *
 * @property name Member name.
 * @property membershipId Unique membership identifier.
 * @property borrowedBooks Titles currently borrowed by the member.
 */
data class LibraryMember(val name: String, val membershipId: Int, var borrowedBooks: MutableList<String>) {
}
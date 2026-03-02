package dam.exer_vl

data class LibraryMember(val name: String, val membershipId: Int, var borrowedBooks: MutableList<String>) {
}
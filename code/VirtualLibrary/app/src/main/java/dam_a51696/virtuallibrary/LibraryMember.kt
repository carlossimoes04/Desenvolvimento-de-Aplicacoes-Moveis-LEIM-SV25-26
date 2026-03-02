package dam_a51696.virtuallibrary

data class LibraryMember(val name: String, val membershipId: Int, var borrowedBooks: MutableList<String>) {
}
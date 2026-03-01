package dam.exer_vl

class PhysicalBook (title: String, author: String, year: Int, initialCopies: Int,
                    val weight: Double, val hasHardcover: Boolean) : Book(title, author, year, initialCopies){

}
package dam.exer_vl

class DigitalBook (title: String, author: String, year: Int, initialCopies: Int,
                   val fileSize: Double, val format: String) : Book(title, author, year, initialCopies){

    override fun toString(): String {
        return """
    ${super.toString()}
    ${getStorageInfo()}
        """.trimIndent()
    }

    override fun getStorageInfo(): String {
        return """-> File Size: ${fileSize}MB; Format: $format"""
    }


}
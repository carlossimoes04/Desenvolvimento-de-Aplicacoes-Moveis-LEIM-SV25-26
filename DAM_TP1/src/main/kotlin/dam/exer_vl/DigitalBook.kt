package dam.exer_vl

/**
 * Represents a digital book with file metadata.
 *
 * @property fileSize File size in megabytes.
 * @property format File format (for example, PDF or EPUB).
 */
class DigitalBook (title: String, author: String, year: Int, initialCopies: Int,
                   val fileSize: Double, val format: String) : Book(title, author, year, initialCopies){

    /**
     * Returns base book details plus digital storage details.
     */
    override fun toString(): String {
        return """
    ${super.toString()}
    ${getStorageInfo()}
        """.trimIndent()
    }

    /**
     * Provides storage-related information for this digital book.
     */
    override fun getStorageInfo(): String {
        return """-> File Size: ${fileSize}MB; Format: $format"""
    }


}
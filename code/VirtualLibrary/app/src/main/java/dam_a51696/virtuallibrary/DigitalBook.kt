package dam_a51696.virtuallibrary

/**
 * Represents a digital book in the virtual library.
 * This class extends the base [Book] class by adding properties specific
 * to digital media, such as file size and digital format.
 *
 * @param title The title of the digital book.
 * @param author The author of the digital book.
 * @param year The original year the book was published.
 * @param initialCopies The total number of digital licenses/copies initially available.
 * @property fileSize The size of the digital file in megabytes (MB).
 * @property format The file format of the digital book (e.g., "PDF", "EPUB").
 */
class DigitalBook (title: String, author: String, year: Int, initialCopies: Int,
                   val fileSize: Double, val format: String) : Book(title, author, year, initialCopies){

    /**
     * Returns a formatted string representation of the digital book.
     * It combines the common book details from the base class with the
     * specific digital storage information.
     *
     * @return A multi-line string containing all the digital book's details.
     */
    override fun toString(): String {
        return """
    ${super.toString()}
    ${getStorageInfo()}
        """.trimIndent()
    }

    /**
     * Retrieves the storage information specific to a digital book.
     * * @return A string detailing the file size (in MB) and the file format.
     */
    override fun getStorageInfo(): String {
        return """-> File Size: ${fileSize}MB; Format: $format"""
    }

}
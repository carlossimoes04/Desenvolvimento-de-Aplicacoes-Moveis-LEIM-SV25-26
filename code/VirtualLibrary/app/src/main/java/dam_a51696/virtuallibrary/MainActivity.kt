package dam_a51696.virtuallibrary

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val library = Library("Central Library")
    private lateinit var tvConsola: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvConsola = findViewById(R.id.tvConsola)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val btnMostrar = findViewById<Button>(R.id.btnMostrar)
        val btnRequisitar = findViewById<Button>(R.id.btnRequisitar)
        val btnDevolver = findViewById<Button>(R.id.btnDevolver)

        // botão de adicionar
        btnIniciar.setOnClickListener {
            val digitalBook = DigitalBook("Kotlin in Action", "Dmitry Jemerov", 2017, 5, 4.5, "PDF")
            val physicalBook = PhysicalBook("Clean Code", "Robert C. Martin", 2008, 3, 650.0, true)
            val classicBook = PhysicalBook("1984", "George Orwell", 1949, 2, 400.0, false)

            logToScreen(library.addBook(digitalBook))
            logToScreen(library.addBook(physicalBook))
            logToScreen(library.addBook(classicBook))

            logToScreen("\n> Total global de livros criados: ${Library.getTotalBooksCreated()}")
            btnIniciar.isEnabled = false // Desativa para não adicionar duplicados
        }

        // botão de mostrar e pesquisar
        btnMostrar.setOnClickListener {
            logToScreen("\n--- LISTA DE LIVROS ---")
            logToScreen(library.showBooks())

            logToScreen("\n--- PESQUISA (George Orwell) ---")
            logToScreen(library.searchByAuthor("George Orwell"))
        }

        // botão de requisitar
        btnRequisitar.setOnClickListener {
            logToScreen("\n--- REQUISITAR ---")
            logToScreen(library.borrowBook("Clean Code"))
            logToScreen(library.borrowBook("1984"))
            logToScreen(library.borrowBook("1984"))
            logToScreen(library.borrowBook("1984")) // Este vai falhar pois o stock acabou!
        }

        // botão de devolver
        btnDevolver.setOnClickListener {
            logToScreen("\n--- DEVOLVER ---")
            logToScreen(library.returnBook("1984"))
        }
    }

    private fun logToScreen(mensagem: String) {
        tvConsola.append("\n$mensagem")
    }
}
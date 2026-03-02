package dam_a51696.virtuallibrary

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val minhaBiblioteca = Library("Biblioteca do ISEL")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvTotalLivros = findViewById<TextView>(R.id.tvTotalLivros)
        val btnAdicionar = findViewById<Button>(R.id.btnAdicionar)
        val tvMensagem = findViewById<TextView>(R.id.tvMensagem)

        tvTotalLivros.text = getString(R.string.total_criados, 0)

        btnAdicionar.setOnClickListener {

            val novoLivro = DigitalBook(
                title = "Kotlin para Android",
                author = "Prof. ISEL",
                year = 2026,
                initialCopies = 5,
                fileSize = 10.5,
                format = "PDF"
            )

            minhaBiblioteca.addBook(novoLivro)

            tvTotalLivros.text = getString(R.string.total_criados, Library.getTotalBooksCreated())

            tvMensagem.text = getString(R.string.msg_novo_livro, novoLivro.toString())
        }
    }
}
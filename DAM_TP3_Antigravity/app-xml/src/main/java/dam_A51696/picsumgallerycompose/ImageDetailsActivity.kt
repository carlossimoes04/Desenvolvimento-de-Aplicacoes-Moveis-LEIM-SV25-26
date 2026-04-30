package dam.a51696.picsumgallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dam_A51696.picsumgallerycompose.databinding.ActivityImageDetailsBinding

class ImageDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageDetailsBinding

    companion object {
        private const val EXTRA_ID = "extra_id"
        private const val EXTRA_AUTHOR = "extra_author"
        private const val EXTRA_URL = "extra_url"

        fun newIntent(context: Context, id: String, author: String, url: String): Intent {
            return Intent(context, ImageDetailsActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_AUTHOR, author)
                putExtra(EXTRA_URL, url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageId = intent.getStringExtra(EXTRA_ID) ?: ""
        val author = intent.getStringExtra(EXTRA_AUTHOR) ?: "Desconhecido"
        val downloadUrl = intent.getStringExtra(EXTRA_URL) ?: ""

        setSupportActionBar(binding.toolbarDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarDetails.setNavigationOnClickListener { finish() }

        binding.textAuthorDetail.text = author
        binding.textIdDetail.text = "#$imageId"

        // Load da imagem em alta resolução num ImageView dinâmico
        Glide.with(this)
            .load(downloadUrl)
            .into(binding.imageFull)
    }
}

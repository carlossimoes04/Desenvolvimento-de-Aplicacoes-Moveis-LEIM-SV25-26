package dam.a51696.picsumgallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dam_A51696.picsumgallerycompose.R
import dam_A51696.picsumgallerycompose.core.model.ImageItem

class ImageAdapter(
    private val onFavoriteClick: (ImageItem) -> Unit
) : ListAdapter<ImageItem, ImageAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(
        itemView: View, 
        private val onFavoriteClick: (ImageItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        private val buttonFavorite: android.widget.ImageButton = itemView.findViewById(R.id.buttonFavorite)

        fun bind(imageItem: ImageItem) {
            textViewAuthor.text = imageItem.author
            
            Glide.with(itemView.context)
                .load(imageItem.downloadUrl)
                .centerCrop()
                .into(imageViewPhoto)

            // Emitir evento Heart
            buttonFavorite.setOnClickListener {
                onFavoriteClick(imageItem)
            }

            // Abrir ecrã de Detalhes
            itemView.setOnClickListener {
                val intent = dam.a51696.picsumgallery.ImageDetailsActivity.newIntent(
                    context = itemView.context,
                    id = imageItem.id,
                    author = imageItem.author,
                    url = imageItem.downloadUrl
                )
                itemView.context.startActivity(intent)
            }
        }
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }
    }
}

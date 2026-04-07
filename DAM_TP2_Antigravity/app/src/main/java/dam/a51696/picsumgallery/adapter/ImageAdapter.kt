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
import dam.a51696.picsumgallery.R
import dam.a51696.picsumgallery.model.ImageItem

class ImageAdapter : ListAdapter<ImageItem, ImageAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)

        fun bind(imageItem: ImageItem) {
            textViewAuthor.text = imageItem.author
            
            // Aqui usamos o Glide, tal como no Step 2, para carregar o URL para a ImageView
            Glide.with(itemView.context)
                .load(imageItem.downloadUrl)
                .centerCrop()
                .into(imageViewPhoto)
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

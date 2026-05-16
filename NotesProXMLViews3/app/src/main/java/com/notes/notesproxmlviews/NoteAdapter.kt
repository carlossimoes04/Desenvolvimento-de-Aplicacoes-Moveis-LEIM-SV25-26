package com.notes.notesproxmlviews

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val context: Context
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)

        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.timestampTextView.text = note.timestamp?.let { Utility.timestampToString(it) } ?: ""

        holder.favoriteTextView.visibility = if (note.favorite) View.VISIBLE else View.GONE
        holder.favoriteTextView.text = if (note.favorite) "★" else ""

        val tags = note.tagsCsv
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()
        if (tags.isNotEmpty()) {
            holder.tagsTextView.visibility = View.VISIBLE
            holder.tagsTextView.text = tags.joinToString("   ") { "#$it" }
        } else {
            holder.tagsTextView.visibility = View.GONE
        }

        holder.colorStrip.setBackgroundColor(resolveColor(note.colorLabel))

        if (!note.imageBase64.isNullOrEmpty()) {
            val bmp = Utility.base64ToBitmap(note.imageBase64)
            holder.imageView.setImageBitmap(bmp)
            holder.imageView.visibility = View.VISIBLE
        } else {
            holder.imageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, NoteDetailsActivity::class.java)
            intent.putExtra("title", note.title)
            intent.putExtra("content", note.content)
            intent.putExtra("docId", note.docId)
            intent.putExtra("imageBase64", note.imageBase64)
            intent.putExtra("isFavorite", note.favorite)
            intent.putExtra("tagsCsv", note.tagsCsv)
            intent.putExtra("colorLabel", note.colorLabel)
            context.startActivity(intent)
        }
    }

    private fun resolveColor(colorLabel: String?): Int {
        return when (colorLabel) {
            "blue" -> Color.parseColor("#3F51B5")
            "yellow" -> Color.parseColor("#FFC107")
            "green" -> Color.parseColor("#4CAF50")
            else -> Color.parseColor("#FFFFFF")
        }
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorStrip: View = itemView.findViewById(R.id.note_color_strip)
        val favoriteTextView: TextView = itemView.findViewById(R.id.note_favorite_text_view)
        val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)
        val contentTextView: TextView = itemView.findViewById(R.id.note_content_text_view)
        val tagsTextView: TextView = itemView.findViewById(R.id.note_tags_text_view)
        val timestampTextView: TextView = itemView.findViewById(R.id.note_timestamp_text_view)
        val imageView: ImageView = itemView.findViewById(R.id.note_image_view)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.docId == newItem.docId
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.title == newItem.title &&
                oldItem.content == newItem.content &&
                oldItem.imageBase64 == newItem.imageBase64 &&
                oldItem.favorite == newItem.favorite &&
                oldItem.tagsCsv == newItem.tagsCsv &&
                oldItem.colorLabel == newItem.colorLabel &&
                oldItem.timestamp == newItem.timestamp
        }
    }
}

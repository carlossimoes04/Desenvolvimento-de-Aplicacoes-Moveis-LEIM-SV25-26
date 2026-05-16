package com.notes.notesproxmlviews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp.Companion.now
import com.google.firebase.firestore.DocumentReference

class NoteDetailsActivity : AppCompatActivity() {
    private var titleEditText: EditText? = null
    private var contentEditText: EditText? = null
    private var tagsEditText: EditText? = null
    private var saveNoteBtn: ImageButton? = null
    private var pageTitleTextView: TextView? = null
    private var deleteNoteTextViewBtn: TextView? = null
    private var selectImageBtn: TextView? = null
    private var favoriteNoteBtn: TextView? = null
    private var noteImageView: ImageView? = null
    private var colorLabelGroup: RadioGroup? = null

    private var title: String? = null
    private var content: String? = null
    private var docId: String? = null
    private var existingImageBase64: String? = null
    private var existingTagsCsv: String? = null
    private var existingColorLabel: String? = null
    private var isFavorite: Boolean = false
    private var isEditMode: Boolean = false
    private var selectedBitmap: Bitmap? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = inputStream?.use { BitmapFactory.decodeStream(it) }
            selectedBitmap = bitmap
            noteImageView?.setImageBitmap(bitmap)
            noteImageView?.visibility = if (bitmap != null) View.VISIBLE else View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.notes_content_text)
        tagsEditText = findViewById(R.id.notes_tags_text)
        saveNoteBtn = findViewById(R.id.save_note_btn)
        pageTitleTextView = findViewById(R.id.page_title)
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn)
        selectImageBtn = findViewById(R.id.select_image_btn)
        favoriteNoteBtn = findViewById(R.id.favorite_note_btn)
        noteImageView = findViewById(R.id.note_image_view)
        colorLabelGroup = findViewById(R.id.color_label_group)

        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        docId = intent.getStringExtra("docId")
        existingImageBase64 = intent.getStringExtra("imageBase64")
        existingTagsCsv = intent.getStringExtra("tagsCsv")
        existingColorLabel = intent.getStringExtra("colorLabel")
        isFavorite = intent.getBooleanExtra("isFavorite", false)
        isEditMode = !docId.isNullOrEmpty()

        titleEditText?.setText(title)
        contentEditText?.setText(content)
        tagsEditText?.setText(existingTagsCsv)

        if (isEditMode) {
            pageTitleTextView?.text = getString(R.string.edit_your_note)
            deleteNoteTextViewBtn?.visibility = View.VISIBLE
        }

        favoriteNoteBtn?.text = if (isFavorite) "Unfavorite note" else "Mark as favorite"

        if (!existingImageBase64.isNullOrEmpty()) {
            val bitmap = Utility.base64ToBitmap(existingImageBase64)
            selectedBitmap = bitmap
            noteImageView?.setImageBitmap(bitmap)
            noteImageView?.visibility = if (bitmap != null) View.VISIBLE else View.GONE
        }

        when (existingColorLabel) {
            "blue" -> colorLabelGroup?.check(R.id.color_label_blue)
            "yellow" -> colorLabelGroup?.check(R.id.color_label_yellow)
            "green" -> colorLabelGroup?.check(R.id.color_label_green)
            else -> colorLabelGroup?.check(R.id.color_label_none)
        }

        selectImageBtn?.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        favoriteNoteBtn?.setOnClickListener {
            isFavorite = !isFavorite
            favoriteNoteBtn?.text = if (isFavorite) "Unfavorite note" else "Mark as favorite"
        }

        saveNoteBtn?.setOnClickListener { saveNote() }
        deleteNoteTextViewBtn?.setOnClickListener { deleteNoteFromFirebase() }
    }

    private fun saveNote() {
        val noteTitle = titleEditText?.text?.toString()?.trim().orEmpty()
        val noteContent = contentEditText?.text?.toString()?.trim().orEmpty()
        val noteTagsCsv = tagsEditText?.text?.toString()?.trim().orEmpty()
        val colorLabel = when (colorLabelGroup?.checkedRadioButtonId) {
            R.id.color_label_blue -> "blue"
            R.id.color_label_yellow -> "yellow"
            R.id.color_label_green -> "green"
            else -> "none"
        }

        if (noteTitle.isEmpty()) {
            titleEditText?.error = "Title is required"
            return
        }

        val note = Note()
        note.setTitle(noteTitle)
        note.setContent(noteContent)
        note.setTimestamp(now())
        note.setFavorite(isFavorite)
        note.setTagsCsv(noteTagsCsv)
        note.setColorLabel(colorLabel)
        note.setImageBase64(
            selectedBitmap?.let {
                Utility.bitmapToBase64(scaleBitmapForFirestore(it), 70)
            }
        )

        saveNoteToFirebase(note)
    }

    private fun scaleBitmapForFirestore(bitmap: Bitmap): Bitmap {
        val maxSize = 1024
        val width = bitmap.width
        val height = bitmap.height
        val ratio = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        if (ratio >= 1f) return bitmap

        val scaledWidth = (width * ratio).toInt()
        val scaledHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }

    private fun saveNoteToFirebase(note: Note) {
        val documentReference: DocumentReference = if (isEditMode) {
            Utility.getCollectionReferenceForNotes().document(docId.toString())
        } else {
            Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note saved successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while saving note")
                }
            }
        })
    }

    private fun deleteNoteFromFirebase() {
        val documentReference: DocumentReference = Utility.getCollectionReferenceForNotes().document(
            docId.toString()
        )
        documentReference.delete().addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note deleted successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while deleting note")
                }
            }
        })
    }
}

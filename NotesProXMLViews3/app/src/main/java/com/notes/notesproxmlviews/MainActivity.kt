package com.notes.notesproxmlviews

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {
    private var addNoteBtn: FloatingActionButton? = null
    private var recyclerView: RecyclerView? = null
    private var menuBtn: ImageButton? = null
    private var searchNotesText: EditText? = null
    private var filterAllBtn: TextView? = null
    private var filterStudyBtn: TextView? = null
    private var filterPersonalBtn: TextView? = null
    private var filterUrgentBtn: TextView? = null

    private lateinit var noteAdapter: NoteAdapter
    private val allNotes = mutableListOf<Note>()
    private var searchQuery: String = ""
    private var activeTagFilter: String? = null
    private var notesListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNoteBtn = findViewById(R.id.add_note_btn)
        recyclerView = findViewById(R.id.recyler_view)
        menuBtn = findViewById(R.id.menu_btn)
        searchNotesText = findViewById(R.id.search_notes_text)
        filterAllBtn = findViewById(R.id.filter_all_btn)
        filterStudyBtn = findViewById(R.id.filter_study_btn)
        filterPersonalBtn = findViewById(R.id.filter_personal_btn)
        filterUrgentBtn = findViewById(R.id.filter_urgent_btn)

        noteAdapter = NoteAdapter(this)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = noteAdapter

        addNoteBtn?.setOnClickListener {
            startActivity(Intent(this@MainActivity, NoteDetailsActivity::class.java))
        }
        menuBtn?.setOnClickListener { showMenu() }

        searchNotesText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        filterAllBtn?.setOnClickListener {
            activeTagFilter = null
            applyFilters()
        }
        filterStudyBtn?.setOnClickListener {
            activeTagFilter = "study"
            applyFilters()
        }
        filterPersonalBtn?.setOnClickListener {
            activeTagFilter = "personal"
            applyFilters()
        }
        filterUrgentBtn?.setOnClickListener {
            activeTagFilter = "urgent"
            applyFilters()
        }

    }

    override fun onStart() {
        super.onStart()
        if (notesListener == null) {
            listenForNotes()
        }
    }

    override fun onStop() {
        super.onStop()
        notesListener?.remove()
        notesListener = null
    }

    private fun listenForNotes() {
        notesListener = Utility.getCollectionReferenceForNotes().addSnapshotListener { snapshots, error ->
            if (error != null) {
                Utility.showToast(this, "Failed to load notes")
                return@addSnapshotListener
            }

            allNotes.clear()
            snapshots?.documents?.forEach { document ->
                val note = document.toObject(Note::class.java)
                if (note != null) {
                    note.docId = document.id
                    allNotes.add(note)
                }
            }
            applyFilters()
        }
    }

    private fun applyFilters() {
        val filteredNotes = allNotes.asSequence()
            .filter { matchesSearch(it, searchQuery) }
            .filter { matchesTagFilter(it, activeTagFilter) }
            .sortedWith(compareByDescending<Note> { it.favorite }.thenByDescending { it.timestamp })
            .toList()

        noteAdapter.submitList(filteredNotes)
    }

    private fun matchesSearch(note: Note, query: String): Boolean {
        if (query.isBlank()) return true
        val normalizedQuery = query.trim().lowercase()
        val tags = note.tagsCsv.orEmpty().lowercase()
        return note.title.orEmpty().lowercase().contains(normalizedQuery) ||
            note.content.orEmpty().lowercase().contains(normalizedQuery) ||
            tags.contains(normalizedQuery)
    }

    private fun matchesTagFilter(note: Note, tagFilter: String?): Boolean {
        if (tagFilter.isNullOrBlank()) return true
        val tags = note.tagsCsv
            ?.split(",")
            ?.map { it.trim().lowercase() }
            .orEmpty()
        return tags.contains(tagFilter.lowercase())
    }

    private fun showMenu() {
        val popupMenu = PopupMenu(this@MainActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            if (item.title == "Logout") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else {
                false
            }
        }
    }
}

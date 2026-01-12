package com.example.notessqlite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notessqlite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        notesAdapter = NotesAdapter(db.getAllNotes(), this)

        binding.notesRecyclerView.layoutManager =  LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                refreshNotes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                refreshNotes(newText)
                return true
            }
        })

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        refreshNotes(binding.searchView.query?.toString())
    }

    private fun refreshNotes(query: String?) {
        val notes = db.getAllNotes(query)
        notesAdapter.refreshData(notes)
        binding.emptyTextView.visibility = if (notes.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }
}
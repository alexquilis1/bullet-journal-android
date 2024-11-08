package com.example.bulletjournalapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var journalEntries: MutableList<JournalEntry>
    private lateinit var journalViewModel: JournalViewModel
    private lateinit var adapter: JournalEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the ViewModel
        journalViewModel = ViewModelProvider(this).get(JournalViewModel::class.java)

        // Set up RecyclerView
        setupRecyclerView()

        // Observe LiveData to update the RecyclerView when entries change
        journalViewModel.allEntries.observe(this, Observer { entries ->
            // Update the RecyclerView with the new list of entries
            journalEntries.clear()
            journalEntries.addAll(entries)
            adapter.notifyDataSetChanged()
        })

        // Setting up FloatingActionButton (FAB) click listener
        val fab = findViewById<FloatingActionButton>(R.id.addEntryButton)
        fab.setOnClickListener {
            // When FAB is clicked, open AddEntryActivity
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_ENTRY)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        journalEntries = mutableListOf()  // Initialize the journal entries list
        adapter = JournalEntryAdapter(journalEntries) // Set up the adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Handle result from AddEntryActivity (optional if using ViewModel and LiveData)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_ENTRY && resultCode == RESULT_OK) {
            val newEntry = data?.getParcelableExtra<JournalEntry>("newEntry")
            newEntry?.let {
                // Save the new entry using ViewModel
                journalViewModel.insertEntry(it)
            }
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_ENTRY = 1
    }
}

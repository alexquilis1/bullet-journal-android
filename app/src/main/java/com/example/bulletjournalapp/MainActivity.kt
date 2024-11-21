package com.example.bulletjournalapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit
import com.google.android.material.snackbar.Snackbar


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

            // Show "No entries yet" if the list is empty
            val noEntriesTextView = findViewById<TextView>(R.id.noEntriesTextView)
            if (entries.isEmpty()) {
                noEntriesTextView.visibility = View.VISIBLE
            } else {
                noEntriesTextView.visibility = View.GONE
            }
        })

        // Setting up FloatingActionButton (FAB) click listener
        val fab = findViewById<FloatingActionButton>(R.id.addEntryButton)
        fab.setOnClickListener {
            // When FAB is clicked, open AddEntryActivity
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_ENTRY)
        }

        // Show Snackbar with instructions about editing and deleting entries
        val rootView = findViewById<View>(android.R.id.content) // Root view of the activity
        Snackbar.make(rootView, "Tap to edit, long press to delete an entry", Snackbar.LENGTH_LONG)
            .setAction("Got it") {
                // You can do something after the user presses the action
            }
            .show()

        // Schedule the ReminderWorker to notify the user daily
        scheduleReminderWorker()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        journalEntries = mutableListOf()  // Initialize the journal entries list
        adapter = JournalEntryAdapter(journalEntries, ::onDeleteEntry, ::onEditEntry)  // Pass the onDeleteEntry function to the adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun onEditEntry(entry: JournalEntry) {
        // Mostrar un Snackbar indicando que se editará la entrada
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, "Tap to edit", Snackbar.LENGTH_SHORT).show()

        // Crear el AlertDialog para confirmar la edición
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Entry")
            .setMessage("Are you sure you want to edit this entry?")
            .setPositiveButton("Edit") { _, _ ->
                // Si el usuario confirma, navegar a la pantalla de edición
                val intent = Intent(this, AddEntryActivity::class.java).apply {
                    putExtra("ENTRY_TO_EDIT", entry) // Pasar el objeto JournalEntry
                    putExtra("PHOTO_URI", entry.imageUri) // Pasar la URI de la imagen (si la hay)
                }
                startActivityForResult(intent, REQUEST_CODE_EDIT_ENTRY)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun onDeleteEntry(entry: JournalEntry) {
        // Mostrar un Snackbar indicando que se borrará la entrada
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, "Long press to delete", Snackbar.LENGTH_SHORT).show()

        // Crear el AlertDialog para confirmar la eliminación
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ ->
                // Si el usuario confirma, eliminar la entrada
                journalViewModel.deleteEntry(entry)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Handle result from AddEntryActivity or EditEntryActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD_ENTRY -> {
                    val newEntry = data?.getParcelableExtra<JournalEntry>("newEntry")
                    newEntry?.let {
                        // Save the new entry using ViewModel
                        journalViewModel.insertEntry(it)
                    }
                }
                REQUEST_CODE_EDIT_ENTRY -> {
                    val updatedEntry = data?.getParcelableExtra<JournalEntry>("updatedEntry")
                    updatedEntry?.let {
                        // Update the entry using ViewModel
                        journalViewModel.updateEntry(it)
                    }
                }
            }
        }
    }


    private fun scheduleReminderWorker() {
        // Create a OneTimeWorkRequest to show a reminder notification after a delay (e.g., 24 hours)
        val reminderRequest = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
            .setInitialDelay(24, TimeUnit.HOURS) // Adjust the delay time as needed
            .build()

        // Enqueue the work request using WorkManager
        WorkManager.getInstance(this).enqueue(reminderRequest)
    }

    companion object {
        const val REQUEST_CODE_ADD_ENTRY = 1
        const val REQUEST_CODE_EDIT_ENTRY = 2
    }
}

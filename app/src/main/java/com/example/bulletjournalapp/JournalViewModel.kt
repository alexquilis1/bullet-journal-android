package com.example.bulletjournalapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val journalDao: JournalEntryDao = JournalDatabase.getDatabase(application).journalEntryDao()
    val allEntries: LiveData<List<JournalEntry>> = journalDao.getAllEntries()

    // Method to insert an entry
    fun insertEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalDao.insertEntry(entry)
        }
    }

    fun updateEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalDao.updateEntry(entry)
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalDao.deleteEntry(entry)
        }
    }
}

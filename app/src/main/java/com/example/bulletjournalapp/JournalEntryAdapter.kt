package com.example.bulletjournalapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Ensure you have Glide imported

class JournalEntryAdapter(
    private val journalEntries: MutableList<JournalEntry>,
    private val onDelete: (JournalEntry) -> Unit,
    private val onEdit: (JournalEntry) -> Unit // Add onEdit to handle the edit action
) : RecyclerView.Adapter<JournalEntryAdapter.JournalEntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal_entry, parent, false)
        return JournalEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalEntryViewHolder, position: Int) {
        val entry = journalEntries[position]
        holder.bind(entry)
    }

    override fun getItemCount(): Int = journalEntries.size

    inner class JournalEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            // Configure long press event to delete
            itemView.setOnLongClickListener {
                val entry = journalEntries[adapterPosition]
                // Call delete confirmation function
                onDelete(entry)
                true  // Return true to indicate event was handled
            }

            // Set up single click for edit
            itemView.setOnClickListener {
                val entry = journalEntries[adapterPosition]
                onEdit(entry)  // Trigger edit
            }
        }

        fun bind(entry: JournalEntry) {
            titleTextView.text = entry.title
            dateTextView.text = entry.date
            descriptionTextView.text = entry.description

            // Load the image if Base64 string exists
            if (!entry.imageUri.isNullOrEmpty()) {
                // Decode the Base64 string to a Bitmap
                val decodedByteArray = Base64.decode(entry.imageUri, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

                // Set the Bitmap to the ImageView
                imageView.setImageBitmap(bitmap)
                imageView.visibility = View.VISIBLE // Make the image visible
            } else {
                imageView.visibility = View.GONE // Hide the ImageView if no image exists
            }
        }
    }
}

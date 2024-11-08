package com.example.bulletjournalapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Asegúrate de importar Glide
import com.example.bulletjournalapp.R // Importa R para recursos

class JournalEntryAdapter(private val journalEntries: MutableList<JournalEntry>) :
    RecyclerView.Adapter<JournalEntryAdapter.JournalEntryViewHolder>() {

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

    // Método para actualizar las entradas en el adaptador
    fun updateEntries(newEntries: List<JournalEntry>) {
        journalEntries.clear()
        journalEntries.addAll(newEntries)
        notifyDataSetChanged() // Notifica que los datos han cambiado
    }

    inner class JournalEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(entry: JournalEntry) {
            titleTextView.text = entry.title
            dateTextView.text = entry.date
            descriptionTextView.text = entry.description

            // Cargar la imagen si existe la URI
            if (entry.imageUri != null) {
                val uri = Uri.parse(entry.imageUri)

                // Usamos Glide para cargar la imagen de manera eficiente
                Glide.with(itemView.context)
                    .load(uri)  // Cargar la URI de la imagen
                    .into(imageView)  // Mostrar la imagen en el ImageView

                imageView.visibility = View.VISIBLE // Hacemos visible la imagen
            } else {
                imageView.visibility = View.GONE // Ocultamos el ImageView si no hay imagen
            }
        }
    }
}

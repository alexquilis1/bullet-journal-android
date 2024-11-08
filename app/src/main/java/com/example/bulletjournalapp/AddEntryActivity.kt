package com.example.bulletjournalapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEntryActivity : AppCompatActivity() {

    private lateinit var journalViewModel: JournalViewModel
    private val REQUEST_GALLERY = 1
    private val REQUEST_CAMERA = 2
    private val CAMERA_PERMISSION_CODE = 101
    private var selectedImageUri: String? = null // Variable para almacenar la URI de la imagen seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        journalViewModel = ViewModelProvider(this).get(JournalViewModel::class.java)

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val content = findViewById<EditText>(R.id.descriptionEditText).text.toString()
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Crear la nueva entrada con la URI de la imagen seleccionada
            val newEntry = JournalEntry(
                title = title,
                description = content,
                date = date,
                imageUri = selectedImageUri // Aquí agregamos la URI de la imagen
            )

            // Insertar la nueva entrada
            lifecycleScope.launch {
                journalViewModel.insertEntry(newEntry)
                finish() // Cerrar AddEntryActivity
            }
        }

        // Configurar el botón de selección de imagen desde la galería
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            openGallery()
        }

        // Configurar el botón para tomar una foto con la cámara
        val takePhotoButton = findViewById<Button>(R.id.takePhotoButton)
        takePhotoButton.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
    }

    // Comprobar si se tiene permiso para usar la cámara
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    // Solicitar permiso para usar la cámara
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    // Abrir la galería para seleccionar una imagen
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    // Abrir la cámara para tomar una foto
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    // Manejar los resultados de la selección de imagen o de la cámara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri = data?.data
                    this.selectedImageUri = selectedImageUri?.toString() // Guardamos la URI como String

                    // Mostrar la imagen seleccionada en el ImageView
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    imageView.visibility = android.view.View.VISIBLE
                    Glide.with(this)
                        .load(selectedImageUri)  // Cargar la URI de la imagen
                        .into(imageView)  // Mostrarla en el ImageView
                }
                REQUEST_CAMERA -> {
                    val photo = data?.extras?.get("data") as? android.graphics.Bitmap
                    // Mostrar la foto tomada en el ImageView
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    imageView.visibility = android.view.View.VISIBLE
                    imageView.setImageBitmap(photo)  // Mostrar la imagen capturada
                }
            }
        }
    }

    // Manejar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera() // Si se concede el permiso, abrir la cámara
            } else {
                // Si el permiso es denegado, mostrar un mensaje al usuario
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

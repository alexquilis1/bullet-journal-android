package com.example.bulletjournalapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddEntryActivity : AppCompatActivity() {

    private lateinit var journalViewModel: JournalViewModel
    private val REQUEST_GALLERY = 1
    private val REQUEST_CAMERA = 2
    private val CAMERA_PERMISSION_CODE = 101
    private var selectedImageBitmap: Bitmap? = null
    private var isEditing = false
    private var currentEntry: JournalEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        currentEntry = intent.getParcelableExtra("ENTRY_TO_EDIT")
        if (currentEntry != null) {
            isEditing = true
            supportActionBar?.title = "Edit Entry"
            findViewById<EditText>(R.id.titleEditText).setText(currentEntry?.title)
            findViewById<EditText>(R.id.descriptionEditText).setText(currentEntry?.description)
            currentEntry?.imageUri?.let { loadImageIfExists(it) }
        } else {
            supportActionBar?.title = "Add Entry"
        }

        journalViewModel = ViewModelProvider(this).get(JournalViewModel::class.java)

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val content = findViewById<EditText>(R.id.descriptionEditText).text.toString()
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val imageBase64 = selectedImageBitmap?.let { bitmapToBase64(it) } ?: currentEntry?.imageUri

            if (isEditing && currentEntry != null) {
                val updatedEntry = currentEntry!!.copy(
                    title = title,
                    description = content,
                    date = date,
                    imageUri = imageBase64
                )
                lifecycleScope.launch {
                    journalViewModel.updateEntry(updatedEntry)
                    finish()
                }
            } else {
                val newEntry = JournalEntry(
                    title = title,
                    description = content,
                    date = date,
                    imageUri = imageBase64
                )
                lifecycleScope.launch {
                    journalViewModel.insertEntry(newEntry)
                    finish()
                }
            }
        }

        findViewById<Button>(R.id.selectImageButton).setOnClickListener {
            openGallery()
        }

        findViewById<Button>(R.id.takePhotoButton).setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun loadImageIfExists(imageBase64: String?) {
        if (!imageBase64.isNullOrEmpty()) {
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.visibility = android.view.View.VISIBLE
            val bitmap = base64ToBitmap(imageBase64)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        val compressedBitmap = compressImage(bitmap)
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun compressImage(bitmap: Bitmap): Bitmap {
        val maxWidth = 1024
        val maxHeight = 1024
        val width = bitmap.width
        val height = bitmap.height

        if (width > maxWidth || height > maxHeight) {
            val ratio = Math.min(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
            val newWidth = (width * ratio).toInt()
            val newHeight = (height * ratio).toInt()
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }
        return bitmap
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                            selectedImageBitmap = bitmap
                            updateImageView(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                REQUEST_CAMERA -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    photo?.let {
                        selectedImageBitmap = it
                        updateImageView(it)
                    }
                }
            }
        }
    }

    private fun updateImageView(bitmap: Bitmap) {
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.visibility = android.view.View.VISIBLE
        imageView.setImageBitmap(bitmap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

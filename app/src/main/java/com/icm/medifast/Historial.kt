package com.icm.medifast

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ListView
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Historial : AppCompatActivity() {

    private val storage = FirebaseStorage.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val gridView: GridView = findViewById(R.id.gridView)

        val imageList = listOf(
            R.drawable.doctor2,
            R.drawable.doctor3,
            R.drawable.doctor4,
            // Agrega más imágenes según sea necesario
        )

        val adapter = ImageAdapter(this, mutableListOf())
        gridView.adapter = adapter
        downloadImages(adapter)

    }

    private fun downloadImages(adapter: ImageAdapter) {
        val storageReference = storage.reference
        val imagesReference = storageReference.child("images/2/recetas/")

        imagesReference.listAll()
            .addOnSuccessListener { result ->
                for (item in result.items) {
                    item.downloadUrl.addOnSuccessListener { uri ->
                        val localFile = File.createTempFile("tempImage", "jpg")
                        item.getFile(localFile)
                            .addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                adapter.addBitmap(bitmap)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("FirebaseStorage", "Error downloading file", exception)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Error listing files", exception)
            }
    }
}
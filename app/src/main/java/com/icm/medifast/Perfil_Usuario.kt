package com.icm.medifast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Perfil_Usuario : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        // Verifica si tienes permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no, solicitar al usuario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_REQUEST_CODE
            )
        }

        val openGalleryButton = findViewById<Button>(R.id.editImagen)
        openGalleryButton.setOnClickListener {
            openGallery()
        }

        // Verifica si tienes permiso para acceder a la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no, solicitar al usuario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
        val openCameraButton = findViewById<Button>(R.id.camara)
        openCameraButton.setOnClickListener {
            openCamera()
        }


    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Tienes permiso para abrir la cámara, así que puedes abrir la cámara aquí
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            // No tienes permiso para la cámara, debes solicitarlo al usuario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    // Maneja la respuesta del usuario a la solicitud de permiso
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Mostrar un mensaje de explicación al usuario
            AlertDialog.Builder(this)
                .setTitle("Permiso necesario")
                .setMessage("Se requiere el permiso para acceder a la galería de imágenes.")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        GALLERY_REQUEST_CODE
                    )
                }
                .setNegativeButton("Cancelar") { _, _ ->
                    // Manejar la cancelación por parte del usuario
                }
                .show()
        } else {
            // El usuario marcó "No volver a preguntar", proporciona instrucciones para otorgar el permiso manualmente en la configuración de la aplicación.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario otorgó permiso para la cámara, abre la cámara
                openCamera()
            } else {
                // El usuario negó el permiso, puedes mostrar un mensaje o tomar acciones adecuadas
            }
        }


    }


}
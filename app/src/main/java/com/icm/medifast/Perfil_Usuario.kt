package com.icm.medifast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.icm.medifast.databinding.ActivityPerfilUsuarioBinding
import com.icm.medifast.databinding.ActivityUserDashBoardBinding
import com.icm.medifast.entities.Cliente
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class Perfil_Usuario : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityPerfilUsuarioBinding
    private lateinit var camerapath: Uri
    val PATH_USERS="clientes/"
    private val cameraRequest = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        succes:Boolean-> if (succes){
        loadImage(camerapath)
        }

    }

    private val GalleryRequest = registerForActivityResult(ActivityResultContracts.GetContent()
    ) {uri:Uri? -> if (uri!= null){
        loadImage(uri)
    }
    }
    //private lateinit var myUser: Cliente

    private val database = FirebaseDatabase.getInstance()

    private lateinit var myRef: DatabaseReference

    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        binding = ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val usernameExtra = intent.getStringExtra("username")
        val EPSextra = intent.getStringExtra("EPS")

        binding.editTextText3.setText(usernameExtra)

        val userEmail = auth.currentUser?.email ?: ""
        binding.editTextTextPostalAddress.setText(userEmail)
        setEditableIfNotEmpty(binding.editTextTextPostalAddress, userEmail)

        // Set the user's address and phone if available
        val userDisplayName =  ""
        binding.editTextTextEmailAddress.setText(userDisplayName)
        setEditableIfNotEmpty(binding.editTextTextEmailAddress, userDisplayName)

        val userPhoneNumber = auth.currentUser?.phoneNumber ?: ""
        binding.editTextPhone.setText(userPhoneNumber)
        setEditableIfNotEmpty(binding.editTextPhone, userPhoneNumber)


        //fetchClientInfo()
        val celular = UserDashBoardActivity.myUser.celular
        val direccion = UserDashBoardActivity.myUser.direccion
        binding.editTextTextEmailAddress.setText(direccion)
        binding.editTextPhone.setText(celular)


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
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                GalleryRequest.launch("image/*") // Launch GalleryRequest
            } else {
                // Request permission if not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_REQUEST_CODE
                )
            }
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                initializeFile()
                cameraRequest.launch(camerapath)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PermissionRequestCodes.CAMERA)
            }

        }


        binding.guardar.setOnClickListener{
            val direccionText = binding.editTextTextEmailAddress.text.toString()
            val celular = binding.editTextPhone.text.toString()
            if(direccionText != "" || celular!= ""){

                updateCliente(celular,direccionText)

            }
        }


    }

    private fun updateCliente(celular:String,direccion:String) {
        myRef = database.getReference(PATH_USERS+auth.currentUser!!.uid)
        UserDashBoardActivity.myUser.celular = celular
        UserDashBoardActivity.myUser.direccion = direccion
        myRef.setValue(UserDashBoardActivity.myUser)
    }



    private fun downloadFile() {
        val localFile = File.createTempFile("profile", "jpg")
        val imageRef = storage.reference.child("images/${auth.currentUser!!.uid}/profile.jpg")
        imageRef.getFile(localFile)
            .addOnSuccessListener { taskSnapshot ->
                Log.i("FBApp", "succesfully downloaded")
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.imageView5.setImageBitmap(bitmap)

            }.addOnFailureListener { exception ->

            }
    }


    fun loadImage(imagepath:Uri?){

        val imagestream = contentResolver.openInputStream(imagepath!!)
        val image = BitmapFactory.decodeStream(imagestream)
        binding.imageView5.setImageBitmap(image)


        uploadImageToFirebaseStorage(imagepath)
    }



    private fun uploadImageToFirebaseStorage(imageUri: Uri) {

        var storageReference = storage.reference
        val storageRef = storageReference.child("images/${auth.currentUser!!.uid}/profile.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully, you can get the download URL
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Now you can use 'uri' to store the download URL in your database or perform any other action
                    Log.d("Upload", "Image uploaded successfully. Download URL: $uri")
                    updateDatabaseWithImageUrl(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful uploads
                Log.e("Upload", "Error uploading image", exception)
            }
    }

    private fun updateDatabaseWithImageUrl(imageUrl: String) {
        myRef = database.getReference(PATH_USERS + auth.currentUser!!.uid)

        // Assuming you have a field named 'profileImageUrl' in your Cliente class
        UserDashBoardActivity.myUser.photo = imageUrl

        myRef.setValue(UserDashBoardActivity.myUser)
            .addOnSuccessListener {
                Log.d("Database Update", "Profile image URL updated successfully")
                // Optionally, you can perform additional actions after updating the database
            }
            .addOnFailureListener { exception ->
                Log.e("Database Update", "Error updating profile image URL", exception)
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

    fun initializeFile() {
        val imageFileName: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        try {
            val imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            )

            // Save the file path for use with ACTION_VIEW intents
            camerapath = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".fileprovider",
                imageFile
            )
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the error, show a toast, or log the exception
        }
    }


    private fun setEditableIfNotEmpty(editText: EditText, text: String) {
        editText.isFocusable = text.isEmpty()
        editText.isFocusableInTouchMode = text.isEmpty()
    }


}
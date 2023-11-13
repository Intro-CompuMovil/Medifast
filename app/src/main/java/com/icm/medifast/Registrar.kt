package com.icm.medifast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityRegistrarBinding
import com.icm.medifast.databinding.ActivityUserDashBoardBinding
import com.icm.medifast.entities.Cliente

class Registrar : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityRegistrarBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef:DatabaseReference
    val PATH_USERS="clientes/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_registrar)

        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.button.setOnClickListener {
            if (validateForm()) {
                val email = binding.editTextText.text.toString()
                val password = binding.editTextTextPassword.text.toString()

                registerUser(email, password)
            }
        }



    }



    private fun registerUser(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        // Update user info
                        val upcrb = UserProfileChangeRequest.Builder()
                        upcrb.displayName = binding.editTextText2.text.toString()+ " " + "user"

                        user.updateProfile(upcrb.build())

                        var cliente =  Cliente()
                        cliente.nombre = binding.editTextText2.text.toString()
                        cliente.contrasena = password
                        cliente.correo = email

                        //this part
                        myRef = database.getReference(PATH_USERS+auth.currentUser!!.uid)
                        myRef.setValue(cliente)



                }
                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this, UserDashBoardActivity::class.java)
            intent.putExtra("user", currentUser.email)
            intent.putExtra("username", binding.editTextText2.text.toString())
            startActivity(intent)
            finish() // Close the current activity
        } else {
            binding.editTextText.setText("")
            binding.editTextTextPassword.setText("")
        }
    }
    companion object{
        private const val  TAG = "EmailPassword"
    }
    private fun validateForm(): Boolean {
        var valid = true

        val nombre = binding.editTextText2.text.toString()
        if (TextUtils.isEmpty(nombre)) {
            binding.editTextText2.error = "Required."
            valid = false
        } else {
            binding.editTextText2.error = null
        }

        val email = binding.editTextText.text.toString()
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextText.error = "Enter a valid email address."
            valid = false
        } else {
            binding.editTextText.error = null
        }

        val password = binding.editTextTextPassword.text.toString()
        if (TextUtils.isEmpty(password) || password.length < 6) {
            binding.editTextTextPassword.error = "Password must be at least 6 characters."
            valid = false
        } else {
            binding.editTextTextPassword.error = null
        }

        return valid
    }
}
package com.icm.medifast


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val usuario = findViewById<Button>(R.id.button)

        val Registrarse = findViewById<Button>(R.id.button2)

        val pasarDoctor = findViewById<ImageButton>(R.id.imageButton6)
        usuario.setOnClickListener{
            signInUser(binding.editTextText.text.toString(),binding.editTextTextPassword.text.toString())

        }
        Registrarse.setOnClickListener{
            val Registrar = Intent(this,Registrar::class.java)
            startActivity(Registrar)
        }

        pasarDoctor.setOnClickListener{
            val Doctor = Intent(this,Perfil_Doc::class.java)
            startActivity(Doctor)
        }


    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val userName = currentUser.displayName
            if (userName != null && userName.contains("user", ignoreCase = true)) {
                // User is a regular user, navigate to User Dashboard
                val intent = Intent(this, UserDashBoardActivity::class.java)
                intent.putExtra("user", currentUser.email)
                intent.putExtra("username", currentUser.displayName)
                startActivity(intent)
            } else {
                // User is a doctor or doesn't have a valid display name, navigate to Doctor Dashboard
                val intent = Intent(this, Perfil_Doc::class.java)
                intent.putExtra("user", currentUser.email)
                startActivity(intent)
            }
        } else {
            binding.editTextText.setText("")
            binding.editTextTextPassword.setText("")
        }
    }


    private fun singInCorreoContraseña(){

        auth.signInWithEmailAndPassword(binding.editTextText.text.toString(),
            binding.editTextTextPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                // Sign in task
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful)
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful) {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.editTextText.setText("")
                    binding.editTextTextPassword.setText("")
                }
            }
    }
    private fun validateForm(): Boolean {
        var valid = true
        val email = binding.editTextText.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.editTextText.error = "Required."
            valid = false
        } else {
            binding.editTextText.error = null
        }
        val password = binding.editTextTextPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.editTextTextPassword.error = "Required."
            valid = false
        } else {
            binding.editTextTextPassword.error = null
        }
        return valid
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@") ||
            !email.contains(".") ||
            email.length < 5)
            return false
        return true
    }


    private fun signInUser(email: String, password: String){
        if(validateForm() && isEmailValid(email)){
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
// Sign in success, update UI
                        Log.d(TAG, "signInWithEmail:success:")
                        val user = auth.currentUser
                        updateUI(auth.currentUser)
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }

    companion object{
        private const val  TAG = "EmailPassword"
    }



}
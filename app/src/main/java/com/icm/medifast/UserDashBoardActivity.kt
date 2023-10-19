package com.icm.medifast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityMainBinding
import com.icm.medifast.databinding.ActivityUserDashBoardBinding

class UserDashBoardActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityUserDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val usernameExtra = intent.getStringExtra("username")
        val cleanedUsername = usernameExtra?.replace(" user", "")

        binding.textView4.text = "Bienvenido \n $cleanedUsername"
        binding.imageView5.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        val peditcita = findViewById<LinearLayout>(R.id.linearLayout)
        peditcita.setOnClickListener(){
            val cambiarCitas = Intent(this,DoctoresDisponiblesActivity::class.java)
            startActivity(cambiarCitas)
        }
        val verEps = findViewById<LinearLayout>(R.id.linearLayout1)
        verEps.setOnClickListener(){
            val cambiarwebview = Intent(this,EpsClienteActivity::class.java)
            startActivity(cambiarwebview)
        }
        val RutaDoctor = findViewById<LinearLayout>(R.id.linearLayout2)

        RutaDoctor.setOnClickListener(){
            val atencion = Intent(this,AtencionEnRuta::class.java)
            startActivity(atencion)
        }

        val perfilUsuario = findViewById<Button>(R.id.perfilUsuario)

        perfilUsuario.setOnClickListener(){
            val perfil = Intent(this, Perfil_Usuario::class.java)
            startActivity(perfil)
        }
    }


}
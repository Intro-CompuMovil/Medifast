package com.icm.medifast


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityPerfilDocBinding
import com.icm.medifast.databinding.ActivityRegistrarBinding
import com.icm.medifast.databinding.ActivityUserDashBoardBinding

class Perfil_Doc : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityPerfilDocBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_perfil_doc)

        binding = ActivityPerfilDocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.imageView5.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        val pasarDoctor = findViewById<Button>(R.id.botonPacientes)

        pasarDoctor.setOnClickListener(){
            val Pacientes = Intent(this,Pacientes::class.java)
            startActivity(Pacientes)
        }
        val pasarProxima = findViewById<Button>(R.id.proximaCita)

        pasarProxima.setOnClickListener(){
            val Pacientes = Intent(this,ProximaCita::class.java)
            startActivity(Pacientes)
        }


    }
}
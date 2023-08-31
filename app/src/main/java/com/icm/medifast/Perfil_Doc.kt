package com.icm.medifast


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class Perfil_Doc : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_doc)

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
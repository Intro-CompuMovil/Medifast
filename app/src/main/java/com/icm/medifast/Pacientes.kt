package com.icm.medifast

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Pacientes : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pacientes)
        var paciente = findViewById<TextView>(R.id.nombre1)

        val verHistorial = findViewById<Button>(R.id.historial1)

        verHistorial.setOnClickListener{
            val Historial =  Intent(this, Historial::class.java)
            startActivity(Historial)
        }
    }
}
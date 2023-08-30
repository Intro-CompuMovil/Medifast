package com.icm.medifast

import DoctoresAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView

class DoctoresDisponiblesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctores_disponibles)


        val doctorsList = listOf(
            Doctores("Adriana Juanita", "cardióloga",R.drawable.doctora1),
            Doctores("Jeronima Rosa", "pediatra",R.drawable.doctor2),
            Doctores("Noe Criado", "cardiólogo",R.drawable.doctor3),
            Doctores("Eulogio Narvaez", "pediatra",R.drawable.doctor4),
            Doctores("Xavier Sola", "pediatra",R.drawable.doctor5),
        )

        val adapter = DoctoresAdapter(this, R.layout.doctores_item, doctorsList)
        val listView: ListView = findViewById(R.id.listaDoctores) // Replace with the actual ListView ID
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            // Get the clicked doctor's information
            Log.d("errorenposicion", "Item at position $position clicked.")
            val selectedDoctor = doctorsList[position]

            // Create an intent to open SolicitarCitaActivity
            val intent = Intent(this, SolicitarCitaActivity::class.java)

            // Pass any relevant data to the new activity
            intent.putExtra("doctorName", selectedDoctor.Nombre)
            intent.putExtra("doctorSpecialty", selectedDoctor.Especilidad)

            startActivity(intent)
        }

    }
}
package com.icm.medifast

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityCitasBinding
import com.icm.medifast.databinding.ActivityPacientesBinding
import com.icm.medifast.entities.Cita
import com.icm.medifast.entities.Cliente

class Pacientes : AppCompatActivity() {

    private lateinit var binding: ActivityPacientesBinding
    private lateinit var adapter: ClienteAdapter
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference

    companion object{
        lateinit var citaEscogida: Cita
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_pacientes)

        binding = ActivityPacientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val PacienteList = mutableListOf<Cliente>()
        adapter = ClienteAdapter(this, PacienteList)

        binding.listCitas.adapter = adapter

        getCitasForDoctor(auth.currentUser!!.uid)

        binding.listCitas.setOnItemClickListener { parent, view, position, id ->
            // Get the selected Cita
            val selectedpaciente= adapter.getItem(position)

            // Assuming you have an Intent to open the AtencionEnRutaActivity
            val intent = Intent(this, Historial::class.java)

            if (selectedpaciente != null) {
                UserDashBoardActivity.myUser = selectedpaciente
            }

            // Start the AtencionEnRutaActivity
            startActivity(intent)
        }

    }

    private fun getCitasForDoctor(uid: String) {

        val citasReference = database.reference.child("citas")

        citasReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val citasList = mutableListOf<Cliente>()

                for (citaSnapshot in dataSnapshot.children) {
                    val cita = citaSnapshot.getValue(Cita::class.java)

                    // Check if the cita is for the specified paciente ID
                    if (cita?.idDoctor == uid) {
                        // Fetch doctor and patient names based on their IDs
                        fetchPatient(cita.idPaciente) { paciente ->

                            // Update the Cita object with names
                            cita.paciente = paciente
                            cita.doctor = Perfil_Doc.myDoctor

                            // Add the modified Cita to the list
                            if (paciente != null) {
                                citasList.add(paciente)
                            }

                            // Update the adapter with the new data
                            adapter.clear()
                            adapter.addAll(citasList)
                            adapter.notifyDataSetChanged()

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })



    }


    private fun fetchPatient(patientId: String, callback: (Cliente?) -> Unit) {
        val clientsReference = database.reference.child("clientes").child(patientId)

        clientsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val patient = dataSnapshot.getValue(Cliente::class.java)
                callback(patient)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}
package com.icm.medifast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityCitasBinding
import com.icm.medifast.entities.Cita
import com.icm.medifast.entities.Cliente

class citasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitasBinding
    private lateinit var adapter: CitasAdapter
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference

    companion object{
        lateinit var citaEscogida: Cita
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_citas)

        binding = ActivityCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val CitasList = mutableListOf<Cita>()
        adapter = CitasAdapter(this, CitasList)

        binding.listCitas.adapter = adapter

        getCitasForPaciente(auth.currentUser!!.uid)


        binding.listCitas.setOnItemClickListener { parent, view, position, id ->
            // Get the selected Cita
            val selectedCita = adapter.getItem(position)

            // Assuming you have an Intent to open the AtencionEnRutaActivity
            val intent = Intent(this, AtencionEnRuta::class.java)

            if (selectedCita != null) {
                citaEscogida = selectedCita
            }

            // Start the AtencionEnRutaActivity
            startActivity(intent)
        }

    }

    private fun getCitasForPaciente(pacienteId: String) {
        val citasReference = database.reference.child("citas")

        citasReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val citasList = mutableListOf<Cita>()

                for (citaSnapshot in dataSnapshot.children) {
                    val cita = citaSnapshot.getValue(Cita::class.java)

                    // Check if the cita is for the specified paciente ID
                    if (cita?.idPaciente == pacienteId) {
                        // Fetch doctor and patient names based on their IDs
                        fetchDoctor(cita.idDoctor) { doctor ->

                                // Update the Cita object with names
                                cita.doctor = doctor
                                cita.paciente = UserDashBoardActivity.myUser

                                // Add the modified Cita to the list
                                citasList.add(cita)

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

    private fun fetchDoctor(doctorId: String, callback: (Doctores?) -> Unit) {
        val doctorsReference = database.reference.child("doctores").child(doctorId)

        doctorsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctor = dataSnapshot.getValue(Doctores::class.java)
                callback(doctor)
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

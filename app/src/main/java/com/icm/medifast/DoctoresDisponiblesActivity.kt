package com.icm.medifast

import DoctoresAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class DoctoresDisponiblesActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var adapter: DoctoresAdapter
    companion object{
        lateinit var doctorTodo:Doctores
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctores_disponibles)



//        val doctorsList = listOf(
//            Doctores("Adriana Juanita", "cardióloga",R.drawable.doctora1),
//            Doctores("Jeronima Rosa", "pediatra",R.drawable.doctor2),
//            Doctores("Noe Criado", "cardiólogo",R.drawable.doctor3),
//            Doctores("Eulogio Narvaez", "pediatra",R.drawable.doctor4),
//            Doctores("Xavier Sola", "pediatra",R.drawable.doctor5),
//        )

        val doctorsList = mutableListOf<Doctores>()
        adapter = DoctoresAdapter(this, R.layout.doctores_item, doctorsList)

        val listView: ListView = findViewById(R.id.listaDoctores)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            // Get the clicked doctor's information
            Log.d("errorenposicion", "Item at position $position clicked.")
            val selectedDoctor = doctorsList[position]
            doctorTodo = selectedDoctor
            // Create an intent to open SolicitarCitaActivity
            val intent = Intent(this, SolicitarCitaActivity::class.java)

            // Pass any relevant data to the new activity
            intent.putExtra("doctorName", selectedDoctor.nombre)
            intent.putExtra("doctorSpecialty", selectedDoctor.especialidad)
            intent.putExtra("photo", selectedDoctor.FotoResource)

            startActivity(intent)
        }
        obtenerDoctores()
    }

    private fun obtenerDoctores() {
        myRef = database.getReference("doctores")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val doctorsList = mutableListOf<Doctores>()

                if (snapshot.exists()) {
                    for (empSnap in snapshot.children) {
                        Log.i("Doctoranadido" , empSnap.toString())
                        val empData = empSnap.getValue(Doctores::class.java)
                        Log.i("Doctoranadido" , empData.toString())
                        if (empData != null) {
                            if(empData.Eps == UserDashBoardActivity.myUser.eps)
                                doctorsList.add(empData)
                        }
                    }

                    // Actualizar el adaptador con la nueva lista de doctores
                    adapter.clear()
                    adapter.addAll(doctorsList)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("conseguirDoctores", "Error al obtener doctores", error.toException())
            }
        })
    }

}
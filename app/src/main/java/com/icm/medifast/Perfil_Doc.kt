package com.icm.medifast


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityPerfilDocBinding
import com.icm.medifast.databinding.ActivityRegistrarBinding
import com.icm.medifast.databinding.ActivityUserDashBoardBinding
import com.icm.medifast.entities.Cita
import com.icm.medifast.entities.Cliente

class Perfil_Doc : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityPerfilDocBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_DOCTOR="doctores/"
    companion object{
        lateinit var myDoctor: Doctores
    }
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
            val Pacientes = Intent(this,CitasDoctor::class.java)
            startActivity(Pacientes)
        }


        fetchDoctorInfo()
    }

    private fun fetchDoctorInfo() {
        myRef = database.getReference("$PATH_DOCTOR${auth.currentUser!!.uid}")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                myDoctor = dataSnapshot.getValue(Doctores::class.java)!!
                if (myDoctor!= null) {
                    Log.i("nombre usuario", "Encontró usuario: ${myDoctor.nombre}")
                    binding.nomDoc.text = "Dr. ${myDoctor.nombre}"

                } else {
                    Log.w("error en la consulta", "Cliente is null")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("error en la consulta", databaseError.toException())
                Toast.makeText(baseContext, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
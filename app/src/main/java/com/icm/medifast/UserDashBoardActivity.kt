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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.icm.medifast.databinding.ActivityMainBinding
import com.icm.medifast.databinding.ActivityUserDashBoardBinding
import com.icm.medifast.entities.Cliente

class UserDashBoardActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityUserDashBoardBinding
    companion object{
        lateinit var myUser: Cliente
    }

    private val database = FirebaseDatabase.getInstance()
    val PATH_USERS="clientes/"
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val usernameExtra = intent.getStringExtra("username")
        val cleanedUsername = usernameExtra?.replace(" user", "")
        val EPSCliente = intent.getStringExtra("EPS")

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
            val atencion = Intent(this,citasActivity::class.java)
            startActivity(atencion)
        }

        val perfilUsuario = findViewById<Button>(R.id.perfilUsuario)

        perfilUsuario.setOnClickListener(){
            val perfil = Intent(this, Perfil_Usuario::class.java)
            perfil.putExtra("username", cleanedUsername)
            perfil.putExtra("EPS", EPSCliente)
            startActivity(perfil)
        }
        val historial = findViewById<ImageView>(R.id.imageView2)
        historial.setOnClickListener(){
            val historial:Intent = Intent(this,Historial::class.java)
            startActivity(historial)
        }
        fetchClientInfo()
    }

    private fun fetchClientInfo() {
        myRef = database.getReference("$PATH_USERS${auth.currentUser!!.uid}")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                myUser = dataSnapshot.getValue(Cliente::class.java)!!
                if (myUser != null) {
                    Log.i("nombre usuario", "Encontró usuario: ${myUser.nombre}")


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
package com.icm.medifast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout

class UserDashBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dash_board)

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
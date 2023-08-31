package com.icm.medifast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usuario = findViewById<Button>(R.id.button)

        val Registrarse = findViewById<Button>(R.id.button2)

        usuario.setOnClickListener{

            val pasaracliente = Intent(this,UserDashBoardActivity::class.java)
            startActivity(pasaracliente)
        }
        Registrarse.setOnClickListener{
            val Registrar = Intent(this,Registrar::class.java)
            startActivity(Registrar)
        }


    }




}
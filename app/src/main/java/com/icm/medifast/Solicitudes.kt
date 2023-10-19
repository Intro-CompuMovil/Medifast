package com.icm.medifast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast

class Solicitudes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitudes)


        val aceptar = findViewById<ImageButton>(R.id.aceptar)
        val negar = findViewById<ImageButton>(R.id.negar)

        aceptar.setOnClickListener{
            val intent = Intent(this, AtencionEnRuta::class.java )
            startActivity(intent)
        }

        negar.setOnClickListener{
            Toast.makeText(this, "Has negado la solicitud", Toast.LENGTH_SHORT).show()
        }
    }
}
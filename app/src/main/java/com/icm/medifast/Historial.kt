package com.icm.medifast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ListView

class Historial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val gridView: GridView = findViewById(R.id.gridView)

        val imageList = listOf(
            R.drawable.doctor2,
            R.drawable.doctor3,
            R.drawable.doctor4,
            // Agrega más imágenes según sea necesario
        )

        val adapter = ImageAdapter(this, imageList)
        gridView.adapter = adapter

    }


    fun downloadImages(){

    }
}
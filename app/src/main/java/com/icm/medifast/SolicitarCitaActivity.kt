package com.icm.medifast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker

class SolicitarCitaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitar_cita)

        val timePicker: TimePicker = findViewById(R.id.timePicker)

        // Set the time picker to display the AM/PM format
        timePicker.setIs24HourView(true)

        val doctorName = intent.getStringExtra("doctorName")
        val doctorSpecialty = intent.getStringExtra("doctorSpecialty")
        val photoResource = intent.getIntExtra("photo", R.drawable.doctor3)

        val doctorNameTextView: TextView = findViewById(R.id.textView14)
        val doctorSpecialtyTextView: TextView = findViewById(R.id.textView15)
        val doctorPhotoImageView: ImageView = findViewById(R.id.circleImageView)

        doctorNameTextView.text = doctorName
        doctorSpecialtyTextView.text = doctorSpecialty
        doctorPhotoImageView.setImageResource(photoResource)
    }

}
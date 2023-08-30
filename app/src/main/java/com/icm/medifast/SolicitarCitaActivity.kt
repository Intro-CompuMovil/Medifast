package com.icm.medifast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker

class SolicitarCitaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitar_cita)

        val timePicker: TimePicker = findViewById(R.id.timePicker)

        // Set the time picker to display the AM/PM format
        timePicker.setIs24HourView(false)
    }
}
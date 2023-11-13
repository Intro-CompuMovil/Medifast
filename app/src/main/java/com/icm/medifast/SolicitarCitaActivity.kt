package com.icm.medifast

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SolicitarCitaActivity : AppCompatActivity() {

    private val CHANNEL_ID = "my_channel_01"

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
        createNotificationChannel()

        val botonCita = findViewById<Button>(R.id.confirmButton)

        botonCita.setOnClickListener(){
            showNotification(doctorName)
        }
    }

    private fun createNotificationChannel() {
        // Crea el NotificationChannel, pero solo en API 26+ porque
        // la clase NotificationChannel es nueva y no está en la biblioteca de soporte
        val name = "channel"
        val descriptionText = "channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        // IMPORTANCE_MAX muestra la notificación animada
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Registra el canal con el sistema; no puedes cambiar la importancia
        // u otros comportamientos de notificación después de esto
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification( Nombre:String?) {
        // Crea un intent para la actividad que deseas abrir al hacer clic en la notificación
        val intent = Intent(this, UserDashBoardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val wearableExtender = NotificationCompat.WearableExtender()
            .setHintHideIcon(true)
            .addAction(NotificationCompat.Action(R.drawable.phuser, "Notificacion", pendingIntent))
        // Construye la notificación
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.phuser)
            .setContentTitle("Cita Confirmada")
            .setContentText("Tu cita con el Dr. ${Nombre} ha sido confirmada.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        mBuilder.extend(wearableExtender)

        // Muestra la notificación
        val notificationId = 1
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ){

        }
        with(NotificationManagerCompat.from(this)) {
        // notificationId is a unique int for each notification that you must define.
            notify(notificationId, mBuilder.build())
        }

    }
}
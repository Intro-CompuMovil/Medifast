package com.icm.medifast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.icm.medifast.entities.Cita

class CitasAdapter(context: Context, citas: List<Cita>) : ArrayAdapter<Cita>(context, 0, citas) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.cita_item, parent, false)
        }

        // Get the data item for this position
        val cita = getItem(position)

        // Find your TextViews in the layout
        val doctorNameTextView: TextView = itemView!!.findViewById(R.id.doctorName)
        val patientNameTextView: TextView = itemView.findViewById(R.id.patientName)
        val hourValueTextView: TextView = itemView.findViewById(R.id.hourValue)

        // Populate the data into the template view using the data object
        cita?.let {
            doctorNameTextView.text = "Doctor: ${it.doctor?.nombre}"
            patientNameTextView.text = "Paciente: ${it.paciente?.nombre}"
            hourValueTextView.text = "${it.fecha}"
        }

        // Return the completed view to render on screen
        return itemView
    }
}

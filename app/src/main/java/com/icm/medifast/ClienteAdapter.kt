package com.icm.medifast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.icm.medifast.entities.Cita
import com.icm.medifast.entities.Cliente

class ClienteAdapter(context: Context, Cliente: List<Cliente>) : ArrayAdapter<Cliente>(context, 0, Cliente) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.paciente_item, parent, false)
        }

        // Get the data item for this position
        val cita = getItem(position)

        // Find your TextViews in the layout
        val nombreTextView: TextView = itemView!!.findViewById(R.id.nombre1)
        val correoTextView: TextView = itemView.findViewById(R.id.correo1)
        val celularTextView: TextView = itemView.findViewById(R.id.celular1)
        val direccionTextView: TextView = itemView.findViewById(R.id.direccion1)

        // Populate the data into the template view using the data object
        cita?.let {
            nombreTextView.text = " ${it.nombre}" // Assuming there is a property named 'nombre' in your Cita class
            correoTextView.text = " ${it.correo}" // Assuming there is a property named 'correo' in your Cita class
            celularTextView.text = "${it.celular}" // Assuming there is a property named 'celular' in your Cita class
            direccionTextView.text = "${it.direccion}" // Assuming there is a property named 'direccion' in your Cita class
        }

        // You can set a click listener for the historialButton here if needed

        // Return the completed view to render on screen
        return itemView
    }
}

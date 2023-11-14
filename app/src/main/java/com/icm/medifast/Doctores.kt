package com.icm.medifast

data class Doctores(
    val nombre: String,
    val especialidad: String,
    val FotoResource: Int,
    val Eps:String,
    val id: String,
    var latitud:Double,
    var longitud:Double
) {

    // Constructor sin argumentos necesario para Firebase



    constructor() : this("", "", getRandomImageResource(), "","",0.0,0.0)

    companion object {
        private val randomImageResources = arrayOf(
            R.drawable.doctora1,
            R.drawable.doctor2,
            R.drawable.doctor3,
            R.drawable.doctor4,
            R.drawable.doctor5,
            // Add more image resources as needed
        )

        fun getRandomImageResource(): Int {
            // Select a random image resource from the array
            return randomImageResources.random()
        }
    }

    // Constructor que selecciona una imagen aleatoria


}


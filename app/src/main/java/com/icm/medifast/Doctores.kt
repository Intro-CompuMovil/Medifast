package com.icm.medifast

data class Doctores(
    val nombre: String,
    val especialidad: String,
    val FotoResource: Int,
    val Eps:String
) {

    // Constructor sin argumentos necesario para Firebase
    constructor() : this("", "", getRandomImageResource(), "")

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
    constructor(nombre: String, especialidad: String, eps: String) : this(
        nombre,
        especialidad,
        getRandomImageResource(),
        eps
    )
}


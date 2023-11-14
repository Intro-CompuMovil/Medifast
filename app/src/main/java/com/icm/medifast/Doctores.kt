package com.icm.medifast

data class Doctores(
    val nombre: String,
    val especialidad: String,
    val FotoResource: Int,
    val Eps:String,
    val id: String
) {
    // Constructor sin argumentos necesario para Firebase
    constructor() : this("", "", R.drawable.doctor2,"","")
}


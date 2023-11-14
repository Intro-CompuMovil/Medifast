package com.icm.medifast.entities

import com.icm.medifast.Doctores

class Cita {
    var idPaciente:String = ""
    var idDoctor:String=""
    var fecha:String=""
    var doctor: Doctores? = null
    var paciente: Cliente? = null


}
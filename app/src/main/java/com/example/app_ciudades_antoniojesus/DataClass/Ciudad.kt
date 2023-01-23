package com.example.app_ciudades_antoniojesus.DataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Ciudades")
data class Ciudad(
    var Nombre : String,
    var Provincia : String,
    var Pais : String,
    var Latitud : Double,
    var Longitud : Double,
    var Imagen : String,
    var Visitado : Boolean,
    var habitantes : Int
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}

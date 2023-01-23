package com.example.app_ciudades_antoniojesus.BaseDatos

import android.app.Application

class MyApp : Application() {
    val database by lazy { MyDataBase.getDatabase(this) }
    val repository by lazy { Repository(database.ciudadesDao()) }
}
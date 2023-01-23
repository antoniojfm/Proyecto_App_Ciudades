package com.example.app_ciudades_antoniojesus.BaseDatos

import androidx.lifecycle.LiveData
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad

class Repository(private val ciudadesDAO: CiudadesDAO) {

    suspend fun insertarCiudad(ciudad: Ciudad){
        ciudadesDAO.insertarCiudad(ciudad)
    }

    val listadoCiudades = ciudadesDAO.dameCiudades()

    suspend fun eliminaCiudad(ciudad: Ciudad){
        ciudadesDAO.eliminaCiudad(ciudad)
    }

    suspend fun eliminaTodasCiudades(){
        ciudadesDAO.eliminaTodasCiudades()
    }

    suspend fun modificarCiudad(ciudad: Ciudad){
        ciudadesDAO.modificarCiudad(ciudad)
    }

    val ListaFavoritos = ciudadesDAO.ListaFavoritos()

    fun busquedaPais(pais: String){
        ciudadesDAO.BusquedaCiudadPais(pais)
    }

}
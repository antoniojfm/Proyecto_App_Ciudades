package com.example.app_ciudades_antoniojesus.BaseDatos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad

@Dao
interface CiudadesDAO {

    @Insert
    suspend fun insertarCiudad(ciudad: Ciudad)

    @Query("Select * from Ciudades")
    fun dameCiudades() : LiveData<List<Ciudad>>

    @Delete
    suspend fun eliminaCiudad(ciudad: Ciudad)

    @Query("Delete from Ciudades")
    suspend fun eliminaTodasCiudades()

    @Update
    suspend fun modificarCiudad(ciudad: Ciudad)

    @Query("Select * from Ciudades where Visitado = 1")
    fun ListaFavoritos() : LiveData<List<Ciudad>>

    @Query("Select * from Ciudades where Pais LIKE :pais")
    fun BusquedaCiudadPais(pais : String) : LiveData<List<Ciudad>>

}
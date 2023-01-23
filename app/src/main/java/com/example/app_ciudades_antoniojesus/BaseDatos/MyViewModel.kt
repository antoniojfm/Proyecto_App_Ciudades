package com.example.app_ciudades_antoniojesus.BaseDatos

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(val repository: Repository) : ViewModel() {

    val ciudad_seleccionada = MutableLiveData<Ciudad>()
    val imageUri = MutableLiveData<Uri>()
    val textQuery = MutableLiveData<String>()
    val texto: LiveData<String> = textQuery

    fun insertarCiudad(ciudad: Ciudad){
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertarCiudad(ciudad)
        }
    }
    fun eliminaCiudad(ciudad: Ciudad){
        CoroutineScope(Dispatchers.IO).launch {
            repository.eliminaCiudad(ciudad)
        }
    }
    fun eliminaTodasCiudades(){
        CoroutineScope(Dispatchers.IO).launch {
            repository.eliminaTodasCiudades()
        }
    }
    fun modificarCiudad(ciudad: Ciudad){
        CoroutineScope(Dispatchers.IO).launch {
            repository.modificarCiudad(ciudad)
        }
    }

    fun buscarpaisCiudad(pais: String){
        CoroutineScope(Dispatchers.IO).launch {
            repository.busquedaPais(pais)
        }
    }

    class MyViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Repository::class.java)
                .newInstance(repository)
        }
    }
}
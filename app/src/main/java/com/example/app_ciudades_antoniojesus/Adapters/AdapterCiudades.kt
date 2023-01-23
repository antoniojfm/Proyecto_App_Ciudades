package com.example.app_ciudades_antoniojesus.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_ciudades_antoniojesus.BaseDatos.MyViewModel
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad
import com.example.app_ciudades_antoniojesus.R
import com.example.app_ciudades_antoniojesus.databinding.VistaCiudadBinding
import java.util.logging.LogRecord


class AdapterCiudades(var listaCiudades : List<Ciudad>, var mv: MyViewModel) : RecyclerView.Adapter<AdapterCiudades.MyViewHolder>(){

    inner class MyViewHolder(val binding: VistaCiudadBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): MyViewHolder {
        val layoutManager = LayoutInflater.from(parent.context)
        val binding = VistaCiudadBinding.inflate(layoutManager,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder , position: Int) {
        val ciudad = listaCiudades[position]
        holder.binding.nombreCiudad.text = ciudad.Nombre
        holder.binding.provinciaCiudad.text = ciudad.Provincia
        holder.binding.paisCiudad.text = ciudad.Pais

        if(ciudad.Visitado){
            Glide.with(holder.itemView).load(R.drawable.ic_baseline_star_24).into(holder.binding.btFavorito)
        }
        else{
            Glide.with(holder.itemView).load(R.drawable.ic_baseline_star_black_24).into(holder.binding.btFavorito)
        }

        holder.binding.btEliminaCiudad.setOnClickListener {
            mv.eliminaCiudad(ciudad)
        }

        holder.binding.Item.setOnClickListener {
            mv.ciudad_seleccionada.value = ciudad
            it.findNavController().navigate(R.id.action_listaCiudades_to_ciudadSeleccionada)
        }

        holder.binding.btEditarciudad.setOnClickListener {
            mv.ciudad_seleccionada.value = ciudad
            it.findNavController().navigate(R.id.action_listaCiudades_to_editarCiudad)
        }
    }

    override fun getItemCount(): Int {
        return listaCiudades.size
    }

}

package com.example.app_ciudades_antoniojesus

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.app_ciudades_antoniojesus.BaseDatos.MyApp
import com.example.app_ciudades_antoniojesus.BaseDatos.MyViewModel
import com.example.app_ciudades_antoniojesus.databinding.FragmentCiudadSeleccionadaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CiudadSeleccionada: Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentCiudadSeleccionadaBinding
    private lateinit var mapa : GoogleMap
    private lateinit var coordenada: LatLng
    val mv: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCiudadSeleccionadaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aplicacion = requireActivity().application as MyApp

        val fragment = childFragmentManager.findFragmentById(R.id.map_ciudad_seleccionada) as SupportMapFragment
        fragment.getMapAsync(this)


    }

    override fun onMapReady(p0: GoogleMap) {
        mapa = p0
        mapa.mapType = GoogleMap.MAP_TYPE_HYBRID
        mapa.isTrafficEnabled = true

        val uiSettings = mapa.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomGesturesEnabled = true
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isTiltGesturesEnabled = true
        uiSettings.isRotateGesturesEnabled = true

        mv.ciudad_seleccionada.observe(viewLifecycleOwner){ ciudad ->
            binding.ciudadSeleccionada.text = ciudad.Nombre
            (ciudad.Provincia + " - " + ciudad.Pais).also { binding.provinciaPaisSeleccionao.text = it }

            if(ciudad.Imagen != null){
                Glide.with(requireContext()).load(ciudad.Imagen).into(binding.imagenCiudad)
            }

            if(ciudad.habitantes >= 10000){
                binding.habitantesCiudad.setTextColor(Color.GREEN)
                binding.habitantesCiudad.text = ciudad.habitantes.toString()
            }
            else{
                binding.habitantesCiudad.setTextColor(Color.RED)
                binding.habitantesCiudad.text = ciudad.habitantes.toString()
            }

            coordenada = LatLng(ciudad.Latitud,ciudad.Longitud)

            val posicion = MarkerOptions()
                .position(coordenada)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            val marker = mapa.addMarker(posicion)
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada,7f))
        }
    }

    private fun activarUbicacion(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mapa.isMyLocationEnabled = true
        }
    }
}
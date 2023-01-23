package com.example.app_ciudades_antoniojesus

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.app_ciudades_antoniojesus.Adapters.AdapterCiudades
import com.example.app_ciudades_antoniojesus.BaseDatos.MyApp
import com.example.app_ciudades_antoniojesus.BaseDatos.MyViewModel
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad
import com.example.app_ciudades_antoniojesus.databinding.FragmentCiudadSeleccionadaBinding
import com.example.app_ciudades_antoniojesus.databinding.FragmentMostrarCiudadesBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MostrarCiudades : Fragment(), OnMapReadyCallback {
    private lateinit var mapa: GoogleMap
    private lateinit var binding: FragmentMostrarCiudadesBinding
    private lateinit var ciudades: MutableList<Ciudad>
    private lateinit var ciudad: Ciudad
    private lateinit var coordenadas: LatLng
    private lateinit var marker: Marker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMostrarCiudadesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = childFragmentManager.findFragmentById(R.id.mapa_mostrarciudades) as SupportMapFragment
        fragment.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap) {

        val aplicacion = requireActivity().application as MyApp
        val mv : MyViewModel by activityViewModels{
            MyViewModel.MyViewModelFactory(aplicacion.repository)
        }

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

        val madrid = LatLng(40.47883646461693, -3.7692950517063832)
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(madrid,4f))
        val zona: LatLngBounds.Builder = LatLngBounds.builder()

        ciudades = mv.repository.listadoCiudades.value as MutableList<Ciudad>

        for(ciudad in ciudades){
            coordenadas = LatLng(ciudad.Latitud,ciudad.Longitud)
            val posicion = MarkerOptions()
                .position(coordenadas)
                .title(ciudad.Nombre)
                .snippet("pincha para más información")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            marker = mapa.addMarker(posicion)!!
            marker.tag = ciudad.id

            zona.include((LatLng(ciudad.Latitud,ciudad.Longitud)))
           // mv.ciudad_seleccionada.value = ciudad

        }

        mapa.setOnMarkerClickListener { marcador ->
            Toast.makeText(requireContext(),marcador.title,Toast.LENGTH_SHORT).show()
            false
        }

        mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(zona.build(),400))

        mapa.setOnInfoWindowClickListener { marcador ->
            val nombre = marcador.title
            val filtrada = ciudades.filter { it.Nombre == nombre }
            if (filtrada.size > 0) {
                ciudad = filtrada[0]
                mv.ciudad_seleccionada.value = ciudad
                findNavController().navigate(R.id.action_mostrarCiudades_to_ciudadSeleccionada)
            }
        }
    }
}
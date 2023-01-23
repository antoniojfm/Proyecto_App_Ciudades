package com.example.app_ciudades_antoniojesus

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.app_ciudades_antoniojesus.BaseDatos.MyApp
import com.example.app_ciudades_antoniojesus.BaseDatos.MyViewModel
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad
import com.example.app_ciudades_antoniojesus.databinding.FragmentCiudadSeleccionadaBinding
import com.example.app_ciudades_antoniojesus.databinding.FragmentEditarCiudadBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditarCiudad: Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentEditarCiudadBinding
    private lateinit var mapa: GoogleMap
    private lateinit var coordenada: LatLng
    private var marker: Marker? = null
    private lateinit var ciud: Ciudad
    private lateinit var imagenUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarCiudadBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val aplicacion = requireActivity().application as MyApp
        val mv: MyViewModel by activityViewModels {
            MyViewModel.MyViewModelFactory(aplicacion.repository)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa_editarciudades) as MyMapView
        mapFragment.getMapAsync(this)

        mapFragment.setListener(object: MyMapView.OnTouchListener {
            override fun onTouch() {
                binding.scrollview.requestDisallowInterceptTouchEvent(true)
            }
        })

        ciud = Ciudad("","","",0.0,0.0,"",false,0)

        binding.btSeleccionImagenCiudad.setOnClickListener{
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Escoge la opcion que quiera")
            dialog.setPositiveButton("Galeria") { dialog, i ->
                tomarImagenGaleria()
            }
            dialog.setNegativeButton("Camara") { dialog, i ->
                tomarImagenCamara()
            }
            dialog.create()
            dialog.show()
        }

        mv.ciudad_seleccionada.observe(viewLifecycleOwner) { ciudad ->
            binding.datoNombreCiudad.text.append(ciudad.Nombre)
            binding.datoProvinciaCiudad.text.append(ciudad.Provincia)
            binding.datoPaisCiudad.text.append(ciudad.Pais)

            Glide.with(requireContext()).load(ciudad.Imagen).into(binding.imagenSeleccionada)

            if (ciudad.habitantes >= 10000) {
                binding.datoHabitantesCiudad.setTextColor(Color.GREEN)
                binding.datoHabitantesCiudad.text.append(ciudad.habitantes.toString())
            } else {
                binding.datoHabitantesCiudad.setTextColor(Color.RED)
                binding.datoHabitantesCiudad.text.append(ciudad.habitantes.toString())
            }

            if (ciudad.Visitado) {
                binding.visitado.isChecked = ciudad.Visitado
            }

            coordenada = LatLng(ciudad.Latitud , ciudad.Longitud)


            binding.btEditarCiudad.setOnClickListener {
                if (binding.datoNombreCiudad.text.isNullOrEmpty() ||
                    binding.datoProvinciaCiudad.text.isNullOrEmpty() ||
                    binding.datoPaisCiudad.text.isNullOrEmpty() ||
                    binding.datoHabitantesCiudad.text.isNullOrEmpty() ||
                    marker == null || imagenUri == null) {

                    binding.datoNombreCiudad.error = "Mete el Nombre de la ciudad"
                    binding.datoProvinciaCiudad.error = "Mete la Provincia"
                    binding.datoPaisCiudad.error = "Mete el Pais"
                    binding.datoHabitantesCiudad.error = "Mete los Habitantes de la ciudad"
                    Toast.makeText(requireContext() , "Marca la posicion en el mapa" , Toast.LENGTH_LONG).show()
                }
                else {
                    ciudad.Nombre = binding.datoNombreCiudad.text.toString()
                    ciudad.Provincia = binding.datoProvinciaCiudad.text.toString()
                    ciudad.Pais = binding.datoPaisCiudad.text.toString()
                    ciudad.habitantes = binding.datoHabitantesCiudad.text.toString().toInt()
                    ciudad.Visitado = binding.visitado.isChecked
                    ciudad.Latitud = coordenada.latitude
                    ciudad.Longitud = coordenada.longitude
                    ciudad.Imagen = imagenUri.toString()
                    mv.modificarCiudad(ciudad)
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mapa = p0
        mapa.mapType = GoogleMap.MAP_TYPE_HYBRID
        mapa.isTrafficEnabled = true
        val espanita = LatLng(40.4165000 , -3.7025600)
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(espanita , 4f) , 2000 , null)

        val uiSettings = mapa.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomGesturesEnabled = true
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isTiltGesturesEnabled = true
        uiSettings.isRotateGesturesEnabled = true

        val posicion_seleccionada = MarkerOptions()
            .position(coordenada)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            marker = mapa.addMarker(posicion_seleccionada)
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada,7f))

        mapa.setOnMapClickListener {
            if (marker != null) {
                marker?.remove()
            }

            coordenada = LatLng(it.latitude , it.longitude)
            ciud.Latitud = coordenada.latitude
            ciud.Longitud = coordenada.longitude
            val posicion = MarkerOptions().position(coordenada).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada , 6f))
            marker = mapa.addMarker(posicion)
        }
    }

    private fun tomarImagenGaleria() = galeria.launch(arrayOf("image/*"))

    private val galeria = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

        uri?.let {
            imagenUri = it
            binding.imagenSeleccionada.setImageURI(it)
        }
    }

    private fun tomarImagenCamara() {
        val archivoFoto = crearArchivoParaFoto()
        imagenUri = FileProvider.getUriForFile(requireContext(),"${requireActivity().packageName}.provider",archivoFoto)
        resultadoTomarCamara.launch(imagenUri)
    }

    private val resultadoTomarCamara = registerForActivityResult(
        ActivityResultContracts.TakePicture()){ guardado->
        if(guardado){
            binding.imagenSeleccionada.setImageURI(imagenUri)
        }
        else{
            val rutaarchivo = requireActivity().getExternalFilesDir("/imagenes/" + imagenUri.path)
            rutaarchivo?.let {
                val resultado = rutaarchivo.delete()
                Toast.makeText(requireContext(), "Imagen borrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun crearArchivoParaFoto(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }


}
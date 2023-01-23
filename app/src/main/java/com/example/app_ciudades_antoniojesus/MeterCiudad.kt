package com.example.app_ciudades_antoniojesus

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_ciudades_antoniojesus.BaseDatos.MyApp
import com.example.app_ciudades_antoniojesus.BaseDatos.MyViewModel
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad
import com.example.app_ciudades_antoniojesus.databinding.FragmentMeterCiudadBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MeterCiudad: Fragment(), OnMapReadyCallback{

    private lateinit var binding: FragmentMeterCiudadBinding
    private lateinit var mapa : GoogleMap
    private lateinit var ciudad : Ciudad
    private lateinit var coordenadas : LatLng
    private var marker: Marker? = null
    private val model by activityViewModels<MyViewModel>()
    private lateinit var imagenUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeterCiudadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aplicacion = requireActivity().application as MyApp
        val mv: MyViewModel by activityViewModels {
            MyViewModel.MyViewModelFactory(aplicacion.repository)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa_insertarCiudad) as MyMapView
        mapFragment.getMapAsync(this)

        mapFragment.setListener(object: MyMapView.OnTouchListener {
            override fun onTouch() {
                binding.scrollview.requestDisallowInterceptTouchEvent(true)
            }
        })

        ciudad = Ciudad("","","",0.0,0.0,"",false,0)

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

        binding.btMeterCiudad.setOnClickListener {
            if(binding.datoNombreCiudad.text.isNullOrEmpty() ||
                binding.datoProvinciaCiudad.text.isNullOrEmpty() ||
                    binding.datoPaisCiudad.text.isNullOrEmpty() ||
                        binding.datoHabitantesCiudad.text.isNullOrEmpty() ||
                            marker == null || imagenUri == null){

                binding.datoNombreCiudad.error = "Mete el Nombre de la ciudad"
                binding.datoProvinciaCiudad.error = "Mete la Provincia"
                binding.datoPaisCiudad.error = "Mete el Pais"
                binding.datoHabitantesCiudad.error = "Mete los Habitantes de la ciudad"
                Toast.makeText(requireContext(), "Marca la posicion en el mapa", Toast.LENGTH_LONG).show()

            }
            else{
                ciudad.Nombre = binding.datoNombreCiudad.text.toString()
                ciudad.Provincia = binding.datoProvinciaCiudad.text.toString()
                ciudad.Pais = binding.datoPaisCiudad.text.toString()
                ciudad.habitantes = binding.datoHabitantesCiudad.text.toString().toInt()
                ciudad.Visitado = binding.visitado.isChecked
                ciudad.Latitud = coordenadas.latitude
                ciudad.Longitud = coordenadas.longitude
                ciudad.Imagen = imagenUri.toString()
                mv.insertarCiudad(ciudad)
                findNavController().popBackStack()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mapa = p0
        mapa.mapType = GoogleMap.MAP_TYPE_HYBRID
        mapa.isTrafficEnabled = true
        val espanita = LatLng(40.4165000, -3.7025600)
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(espanita,4f),2000,null)

        val uiSettings = mapa.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomGesturesEnabled = true
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isTiltGesturesEnabled = true
        uiSettings.isRotateGesturesEnabled = true

        mapa.setOnMapClickListener {
            if(marker != null){
                marker?.remove()
            }

            coordenadas = LatLng(it.latitude,it.longitude)
            ciudad.Latitud = coordenadas.latitude
            ciudad.Longitud = coordenadas.longitude
            val posicion = MarkerOptions().position(coordenadas).icon(BitmapDescriptorFactory.defaultMarker(HUE_VIOLET))
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas,6f))
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

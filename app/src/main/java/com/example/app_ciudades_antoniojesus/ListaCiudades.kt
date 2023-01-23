package com.example.app_ciudades_antoniojesus

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.app_ciudades_antoniojesus.Adapters.AdapterCiudades
import com.example.app_ciudades_antoniojesus.BaseDatos.MyApp
import com.example.app_ciudades_antoniojesus.BaseDatos.MyViewModel
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad
import com.example.app_ciudades_antoniojesus.databinding.ActivityMainBinding.inflate
import com.example.app_ciudades_antoniojesus.databinding.FragmentListaCiudadesBinding

class ListaCiudades: Fragment() {

    private lateinit var binding: FragmentListaCiudadesBinding
    private lateinit var Lista : ArrayList<Ciudad>
    private lateinit var adapter : AdapterCiudades

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListaCiudadesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val aplicacion = requireActivity().application as MyApp

        val mv : MyViewModel by activityViewModels{
            MyViewModel.MyViewModelFactory(aplicacion.repository)
        }

        Lista = ArrayList()
        adapter = AdapterCiudades(Lista,mv)
        binding.Listado.adapter = adapter
        configRecycler(Lista,mv)

        mv.repository.listadoCiudades.observe(viewLifecycleOwner){
            configRecycler(it.sortedBy { ordenacion -> ordenacion.Nombre },mv)
            binding.contadorCiudades.text = adapter.listaCiudades.size.toString()
        }

        binding.mostrar.setOnClickListener {
            if(binding.mostrar.isChecked){
                Glide.with(this).load(R.drawable.ic_baseline_star_24).into(binding.visitadoCiudad)
                mv.repository.ListaFavoritos.observe(viewLifecycleOwner){
                    configRecycler(it.sortedBy { ordenacion -> ordenacion.Nombre },mv)
                    binding.contadorCiudades.text = adapter.mv.repository.ListaFavoritos.value?.size.toString()
                }
            }
            else{
                mv.repository.listadoCiudades.observe(viewLifecycleOwner){
                    Glide.with(this).load(R.drawable.ic_baseline_star_black_24).into(binding.visitadoCiudad)
                    configRecycler(it.sortedBy { ordenacion -> ordenacion.Nombre },mv)
                    binding.contadorCiudades.text = adapter.listaCiudades.size.toString()
                }
            }
        }


        binding.btNvCiudad.setOnClickListener {
            it.findNavController().navigate(R.id.action_listaCiudades_to_meterCiudad)
        }

        binding.btEliminaCiudades.setOnClickListener{
            mv.eliminaTodasCiudades()
        }

        binding.btMostrarCiudades.setOnClickListener {
            it.findNavController().navigate(R.id.action_listaCiudades_to_mostrarCiudades)
        }

    }

    private fun configRecycler(Lista : List<Ciudad>, mv: MyViewModel) {
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = AdapterCiudades(Lista,mv)
        binding.Listado.layoutManager = layoutManager
        binding.Listado.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val aplicacion = requireActivity().application as MyApp
        val mv : MyViewModel by activityViewModels{
            MyViewModel.MyViewModelFactory(aplicacion.repository)
        }

        inflater.inflate(R.menu.menu, menu)

        val item = menu.findItem(R.id.busqueda)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(pais: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(pais: String): Boolean {
                mv.textQuery.value = pais
                mv.buscarpaisCiudad(pais)
                return true
            }

        })
    }
}
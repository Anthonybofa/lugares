package com.example.lugares_j.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lugares_j.databinding.FragmentGalleryBinding
import com.example.lugares_j.model.Lugar
import com.example.lugares_j.viewmodel.GalleryViewModel
import com.example.lugares_j.viewmodel.LugarViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GalleryFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //un objeto para interactuar con la vista -mapa-
    private lateinit var googleMap: GoogleMap
    private var mapReady = false;

    //se utiliza lugarviewModel donde esta el arraylist de lugares
    private lateinit var lugarViewModel: LugarViewModel

    //programar la funcion para solicitar la descarga del objeto mapa
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        binding.map.onResume()
        binding.map.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        map.let {
            googleMap = it
            mapReady = true

            //se recorre el arreglo para dibujar los lugares
            lugarViewModel.getLugares.observe(viewLifecycleOwner){lugares ->
                updateMap(lugares)
            }
        }
    }

    private fun updateMap(lugares: List<Lugar>) {
        if(mapReady){
            lugares.forEach{ lugar ->
                if(lugar.latitud?.isFinite()==true && lugar.longitud?.isFinite()==true){
                    val marca = LatLng(lugar.latitud,lugar.latitud)
                    googleMap.addMarker(MarkerOptions().position(marca).title(lugar.nombre))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this)[LugarViewModel::class.java]

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
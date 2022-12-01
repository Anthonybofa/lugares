package com.example.lugares_j.ui.lugar

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lugares_j.R
import com.example.lugares_j.databinding.FragmentAddLugarBinding
import com.example.lugares_j.databinding.FragmentLugarBinding
import com.example.lugares_j.model.Lugar
import com.example.lugares_j.viewmodel.LugarViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class AddLugarFragment : Fragment() {

    //objeto que interactua con la tabla
    private  lateinit var lugarViewModel: LugarViewModel

    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        binding.btAdd.setOnClickListener{ addLugar() }

        ubicaGPS()

        return binding.root
    }

    private fun ubicaGPS(){
       if(requireActivity().
               checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=
               PackageManager.PERMISSION_GRANTED
           && requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=
               PackageManager.PERMISSION_GRANTED){
           //si estamos aca hay que pedir auytorizacion para hacer la ubicacion gps
           requireActivity().requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
           Manifest.permission.ACCESS_FINE_LOCATION),105)
       }else{
           //si se tienen los permisoso se busca la ubicacion GPS
           val ubicacion:FusedLocationProviderClient =
               LocationServices.getFusedLocationProviderClient(requireContext())
           ubicacion.lastLocation.addOnSuccessListener {
                location: Location? ->
               if(location != null){
                   binding.tvLatitud.text = "${location.latitude}"
                   binding.tvLongitud.text = "${location.longitude}"
                   binding.tvAltura.text = "${location.altitude}"
               }else{
                    binding.tvLatitud.text = "0.0"
                    binding.tvLongitud.text = "0.0"
                    binding.tvAltura.text = "0.0"
               }
           }
       }
    }

    private fun addLugar() {
        val nombre=binding.etNombre.text.toString() //obtienen el texto de lo que el usuario escribio

        if (nombre.isNotEmpty()){
            val correo=binding.etCorreo.text.toString() //obtienen el texto de lo que el usuario escribio
            val telefono=binding.etTelefono.text.toString() //obtienen el texto de lo que el usuario escribio
            val web=binding.etWeb.text.toString() //obtienen el texto de lo que el usuario escribio
            val latitud = binding.tvLatitud.text.toString().toDouble()
            val longitud = binding.tvLongitud.text.toString().toDouble()
            val altura = binding.tvAltura.text.toString().toDouble()
            val lugar = Lugar("",nombre,correo,telefono,web,latitud,longitud,altura,
                "","")

            //se procede a registrar el nuevo lugar
            lugarViewModel.saveLugar(lugar)
            Toast.makeText(requireContext(),getString(R.string.msg_lugar_added),Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_nav_lugar_to_addLugarFragment)
        }else{
            Toast.makeText(requireContext(),getString(R.string.msg_data),Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
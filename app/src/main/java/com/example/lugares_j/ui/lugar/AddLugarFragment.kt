package com.example.lugares_j.ui.lugar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lugares_j.R
import com.example.lugares_j.databinding.FragmentAddLugarBinding
import com.example.lugares_j.model.Lugar
import com.example.lugares_j.viewmodel.LugarViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles


class AddLugarFragment : Fragment() {

    //objeto que interactua con la tabla
    private  lateinit var lugarViewModel: LugarViewModel

    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var audioUtiles: AudioUtiles
    private lateinit var imagenUtiles: ImagenUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        binding.btAdd.setOnClickListener{
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.VISIBLE
            subeNota() }

        ubicaGPS()

        audioUtiles = AudioUtiles(
            requireActivity(),requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio))

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK){
                imagenUtiles.actualizaFoto()
            }
        }

        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity)

        return binding.root
    }

    //esta funcion sube la nota de autio al storage y pasa la ruta publica a la siguiente funcion
    private fun subeNota() {
        val archivoLocal = audioUtiles.audioFile
        if(archivoLocal.exists() && archivoLocal.isFile && archivoLocal.canRead()){
            //se fija la ruta(uri) del archivoLocal de audio
            val rutaLocal = Uri.fromFile(archivoLocal)

            //se establece la rutqa en la nube de la nota de audio
            val rutaNube
            = "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${archivoLocal.name}"

            //se hace la referencia real
            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            //se sube el archivo y se establecen los "listen" para saber que hacer
            referencia.putFile(rutaLocal).addOnSuccessListener {
                referencia.downloadUrl.addOnSuccessListener {
                    //se obtiene la ruta publica del archivo
                    val rutaAudio = it.toString()
                    subeImagen(rutaAudio)
                }
            }.addOnFailureListener{
                subeImagen("")
            }
        }else{//no hay foto o hay un error en el archivo o algo que no puede leer
            subeImagen("")
        }
    }

    private fun subeImagen(rutaAudio: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen)

        val archivoLocal = imagenUtiles.imagenFile
        if(archivoLocal.exists() && archivoLocal.isFile && archivoLocal.canRead()){
            //se fija la ruta(uri) del archivoLocal de imagen
            val rutaLocal = Uri.fromFile(archivoLocal)

            //se establece la ruta en la nube de la imagen
            val rutaNube
                    = "lugaresApp/${Firebase.auth.currentUser?.email}/imagenes/${archivoLocal.name}"

            //se hace la referencia real
            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            //se sube el archivo y se establecen los "listen" para saber que hacer
            referencia.putFile(rutaLocal).addOnSuccessListener {
                referencia.downloadUrl.addOnSuccessListener {
                    //se obtiene la ruta publica del archivo
                    val rutaImagen = it.toString()
                    addLugar(rutaAudio,rutaImagen)
                }
            }.addOnFailureListener{
                addLugar(rutaAudio,"")
            }
        }else{//no hay foto o hay un error en el archivo o algo que no puede leer
            addLugar(rutaAudio,"")
        }
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

    private fun addLugar(rutaAudio: String, rutaImagen: String) {

        binding.msgMensaje.text = getString(R.string.msg_subiendo_lugar)
        val nombre=binding.etNombre.text.toString() //obtienen el texto de lo que el usuario escribio

        if (nombre.isNotEmpty()){
            val correo=binding.etCorreo.text.toString() //obtienen el texto de lo que el usuario escribio
            val telefono=binding.etTelefono.text.toString() //obtienen el texto de lo que el usuario escribio
            val web=binding.etWeb.text.toString() //obtienen el texto de lo que el usuario escribio
            val latitud = binding.tvLatitud.text.toString().toDouble()
            val longitud = binding.tvLongitud.text.toString().toDouble()
            val altura = binding.tvAltura.text.toString().toDouble()
            val lugar = Lugar("",nombre,correo,telefono,web,latitud,longitud,altura,
                rutaAudio,rutaImagen)

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
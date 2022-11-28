package com.example.lugares_j.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lugares_j.model.Lugar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase


class LugarDao {

    //Variabvles usadas para poder generar la estructura en la nube
    private val coleccion1 = "lugaresApp"
    private val usuario = Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misLugares"

    //Contiene la conexion a la base de datos
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }


    //las funciones de bajo nivel para hacer un CRUD
    //create Read Update Delete

    fun saveLugar(lugar: Lugar){
        //para definir un docunmento en la nube
        val documento : DocumentReference

        if(lugar.id.isEmpty()){ //si esta vacio es un nuevo documento
            documento = firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document()

            lugar.id = documento.id

        }else{//si el id tiene algo se modifica el documento (lugar)
            documento = firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document(lugar.id)
        }

        //ahora se modifica o crea el documento
        documento.set(lugar)
            .addOnSuccessListener {
                Log.d("saveLugar","Lugar creado/actualizado")
            }
            .addOnCanceledListener {
                Log.d("saveLugar","Lugar NO creado/actualizado")
            }
    }

    fun deleteLugar(lugar: Lugar){
        //se valida si el lugar tiene ID para poder borrarlo
        if(lugar.id.isNotEmpty()){ //si NO esta vacio se puede eliminar
            firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document(lugar.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("deleteLugar","Lugar eliminado")
                }
                .addOnCanceledListener {
                    Log.d("deleteLugar","Lugar NO eliiminado")
                }
        }
    }

    fun getLugares() : MutableLiveData<List<Lugar>> {
        val listaLugares = MutableLiveData<List<Lugar>>()
        firestore
            .collection(coleccion1)
            .document(usuario)
            .collection(coleccion2)
            .addSnapshotListener { instantanea, e ->
                if ( e != null) { //se dio un error capturando la imagen de info
                    return@addSnapshotListener
                }
                //si estamos aca no hubo error
                if(instantanea != null){ //si se pudo recuperar la info
                    val lista = ArrayList<Lugar>()

                    //se recorre la instantanea documento por documento convirtiendolo en lugar y agregandolo
                    instantanea.documents.forEach{
                        val lugar = it.toObject(Lugar::class.java)
                        if (lugar != null){ // si se pudo convertir el documento en un lugar
                            lista.add(lugar) //se agrega el lugar a la lista
                        }
                    }

                    listaLugares.value = lista
                }
            }

        return listaLugares
    }
}
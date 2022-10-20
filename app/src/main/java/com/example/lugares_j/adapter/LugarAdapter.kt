package com.example.lugares_j.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.lugares_j.databinding.LugarFilaBinding
import com.example.lugares_j.model.Lugar
import com.example.lugares_j.ui.lugar.LugarFragmentDirections

class LugarAdapter : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>(){
    inner class LugarViewHolder(private val itemBinding: LugarFilaBinding)
        :RecyclerView.ViewHolder(itemBinding.root){
        fun dibuja(lugar: Lugar){
            itemBinding.tvNombre.text = lugar.nombre
            itemBinding.tvCorreo.text = lugar.correo
            itemBinding.tvTelefono.text = lugar.telefono
            itemBinding.vistaFila.setOnClickListener{
                //creo una action para navegar al updqate lugar pasando un argumento lugar
                val action = LugarFragmentDirections.actionNavLugarToUpdateLugarFragment(lugar)

                //efectivamente se para el fragmento....
                itemView.findNavController().navigate(action)
            }

        }

    }

    //lista donde estan los objetos lugar a dibujarse
    private var listaLugares = emptyList<Lugar>()


    //Esta funcion crea "cajitas" para cada lugar.. en memoria
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val itemBinding = LugarFilaBinding.inflate(LayoutInflater.from(parent.context),
        parent,false)

        return LugarViewHolder(itemBinding)
    }

    //Eswta funcion toma un lugar y lo envia a dibujar
    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = listaLugares[position]
        holder.dibuja(lugar)
    }

    //Esta funcion devuelve la cantidad de elementos a dibujar
    override fun getItemCount(): Int {
        return listaLugares.size
    }

    fun setListaLugares(lugares: List<Lugar>){
        this.listaLugares = lugares
        notifyDataSetChanged()
    }
}
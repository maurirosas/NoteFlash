package com.example.noteflash.Fragmentos_Admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.noteflash.R
import com.example.noteflash.Registro_Proyectos
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class Fragment_admin_proyectos : Fragment() {
    private lateinit var ProyectosRegistrados: ListView
    private lateinit var proyectoList: MutableList<String>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nuevoPButton: Button
    private var proyectosListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_admin_proyectos, container, false)

        // Inicializa los componentes de la interfaz
        ProyectosRegistrados = view.findViewById(R.id.ProyectosRegistrados)
        nuevoPButton = view.findViewById(R.id.nuevoPButton) // Inicializa el botón aquí
        proyectoList = mutableListOf()

        // Inicializa Firestore
        firestore = FirebaseFirestore.getInstance()

        // Cargar los proyectos desde Firestore
        cargarProyectos()

        // Configurar el OnClickListener para el botón de registro
        nuevoPButton.setOnClickListener {
            val intent = Intent(requireActivity(), Registro_Proyectos::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun cargarProyectos() {
        // Agregar un listener para obtener los proyectos en tiempo real desde Firestore
        proyectosListener = firestore.collection("proyectos")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar proyectos: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }

                proyectoList.clear() // Limpiar la lista antes de agregar nuevos datos

                if (snapshots != null) {
                    for (document in snapshots) {
                        val nombreProyecto = document.getString("nombreProyecto")
                        if (nombreProyecto != null) {
                            proyectoList.add(nombreProyecto) // Agrega el nombre del proyecto a la lista
                        }
                    }
                }

                // Crear un ArrayAdapter y asociarlo con el ListView
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    proyectoList
                )
                ProyectosRegistrados.adapter = adapter
            }
    }

    override fun onResume() {
        super.onResume()
        cargarProyectos() // Cargar los proyectos cada vez que se vuelve al fragmento
    }

    override fun onPause() {
        super.onPause()
        // Eliminar el listener para evitar fugas de memoria
        proyectosListener?.remove()
    }
}

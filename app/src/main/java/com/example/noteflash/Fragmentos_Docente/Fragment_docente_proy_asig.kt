package com.example.noteflash.Fragmentos_Docente

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.noteflash.Proyectos_Asignados
import com.example.noteflash.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_docente_proy_asig : Fragment() {
    private lateinit var ProyectosAsignados: ListView
    private lateinit var proyectoList: MutableList<String> // Almacena los nombres de los proyectos
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_docente_proy_asig, container, false)

        // Inicializar los componentes de la interfaz
        ProyectosAsignados = view.findViewById(R.id.ProyectosAsignados)
        proyectoList = mutableListOf()

        // Inicializar FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Cargar los proyectos asignados al usuario
        cargarProyectosAsignados()

        return view
    }

    private fun cargarProyectosAsignados() {
        val uid = auth.currentUser?.uid ?: return // Obtener el UID del usuario en sesión

        // Consultar todos los proyectos en la colección "proyectos"
        firestore.collection("proyectos").get()
            .addOnSuccessListener { querySnapshot ->
                proyectoList.clear() // Limpiar la lista antes de cargar nuevos proyectos

                for (document in querySnapshot) {
                    val jurados = document.get("jurados") as? List<Map<String, Any>> ?: continue

                    // Verificar si el UID del usuario en sesión aparece en el array "jurados"
                    val estaEnJurados = jurados.any { it["uid"] == uid }
                    if (estaEnJurados) {
                        // Si el jurado coincide, obtener el nombre del proyecto
                        val nombreProyecto = document.getString("nombreProyecto")
                        if (nombreProyecto != null) {
                            proyectoList.add(nombreProyecto) // Agregar a la lista
                        }
                    }
                }

                if (proyectoList.isEmpty()) {
                    mostrarMensaje("No tienes proyectos asignados.")
                } else {
                    actualizarListView() // Actualizar la vista con los proyectos encontrados
                }
            }
            .addOnFailureListener {
                mostrarMensaje("Error al obtener los proyectos.")
            }
    }

    private fun actualizarListView() {
        // Crear y asignar el adaptador al ListView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, proyectoList)
        ProyectosAsignados.adapter = adapter

        // Manejar los clics en los proyectos listados
        ProyectosAsignados.setOnItemClickListener { _, _, position, _ ->
            val proyectoSeleccionado = proyectoList[position]
            val intent = Intent(requireContext(), Proyectos_Asignados::class.java)
            intent.putExtra("nombreProyecto", proyectoSeleccionado) // Enviar el nombre del proyecto seleccionado
            startActivity(intent)
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        // Mostrar un Toast con el mensaje proporcionado
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }
}

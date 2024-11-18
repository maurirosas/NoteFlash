package com.example.noteflash.Fragmentos_Docente

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.example.noteflash.Proyectos_Calificados
import com.example.noteflash.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_docente_lista_proy : Fragment() {
    private lateinit var ProyectosCalificados: ListView
    private lateinit var proyectoList: MutableList<String>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var spinnerCarreras: Spinner
    private lateinit var carreraSeleccionada: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_docente_lista_proy, container, false)

        // Inicializa los componentes de la interfaz
        ProyectosCalificados = view.findViewById(R.id.ProyectosAsignados)
        spinnerCarreras = view.findViewById(R.id.spinnerCarreras)
        proyectoList = mutableListOf()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        carreraSeleccionada = "Todos" // Valor predeterminado

        configurarSpinner()
        cargarTodosLosProyectos()

        return view
    }

    private fun configurarSpinner() {
        val carreras = listOf(
            "Todos", "Contaduría General",
            "Sistemas Informaticos", "Turismo", "Secretariado Ejecutivo"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, carreras)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCarreras.adapter = adapter

        spinnerCarreras.setSelection(0)

        spinnerCarreras.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                carreraSeleccionada = carreras[position]
                cargarTodosLosProyectos()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarTodosLosProyectos() {
        firestore.collection("proyectos").get()
            .addOnSuccessListener { documentos ->
                if (!documentos.isEmpty) {
                    val proyectosConPromedio = mutableListOf<Pair<String, Double>>()
                    val proyectosSinPromedio = mutableListOf<String>()

                    for (documento in documentos) {
                        val nombreProyecto = documento.getString("nombreProyecto")
                        val carrera = documento.getString("carrera")
                        val promedio = documento.get("promedio") as? Number

                        if (nombreProyecto != null &&
                            (carreraSeleccionada == "Todos" || carreraSeleccionada == carrera)
                        ) {
                            if (promedio != null) {
                                proyectosConPromedio.add(nombreProyecto to promedio.toDouble())
                            } else {
                                proyectosSinPromedio.add(nombreProyecto)
                            }
                        }
                    }

                    // Ordenar los proyectos con promedio de mayor a menor
                    val proyectosOrdenados = proyectosConPromedio.sortedByDescending { it.second }
                    proyectoList.clear()
                    proyectoList.addAll(proyectosOrdenados.map { it.first }) // Agregar primero los ordenados
                    proyectoList.addAll(proyectosSinPromedio) // Luego los sin promedio

                    actualizarListView()
                } else {
                    mostrarMensaje("No hay proyectos disponibles.")
                }
            }
            .addOnFailureListener {
                mostrarMensaje("Error al cargar los proyectos.")
            }
    }

    private fun actualizarListView() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, proyectoList)
        ProyectosCalificados.adapter = adapter

        ProyectosCalificados.setOnItemClickListener { _, _, position, _ ->
            val proyectoSeleccionado = proyectoList[position]
            val intent = Intent(requireContext(), Proyectos_Calificados::class.java)
            intent.putExtra("nombreProyecto", proyectoSeleccionado)
            startActivity(intent)
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }
}

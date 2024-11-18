package com.example.noteflash.Fragmentos_Admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.noteflash.R
import com.example.noteflash.Registro_Estudiantes
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_admin_estudiantes : Fragment() {
    private lateinit var listViewDocentes: ListView
    private lateinit var estudianteList: MutableList<String>
    private lateinit var nuevoEButton: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_admin_estudiantes, container, false)

        // Inicializa los componentes de la interfaz
        listViewDocentes = view.findViewById(R.id.listViewDocentes)
        nuevoEButton = view.findViewById(R.id.nuevoEButton) // Inicializa el botón aquí
        estudianteList = mutableListOf()

        // Inicializa Firestore
        firestore = FirebaseFirestore.getInstance()

        // Cargar los datos de Firestore
        cargarEstudiantes()

        // Configurar el OnClickListener para el botón de registro
        nuevoEButton.setOnClickListener {
            val intent = Intent(requireActivity(), Registro_Estudiantes::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun cargarEstudiantes() {
        // Cargar documentos de la colección "usuarios" donde el rol sea "Estudiante"
        firestore.collection("usuarios")
            .whereEqualTo("rol", "Estudiante")
            .get()
            .addOnSuccessListener { documents ->
                estudianteList.clear() // Limpiar la lista antes de agregar nuevos datos

                for (document in documents) {
                    val nombre = document.getString("nombres")
                    val apellidos = document.getString("apellidos")

                    // Verificar que ambos valores no sean nulos antes de agregarlos a la lista
                    if (nombre != null && apellidos != null) {
                        estudianteList.add("$nombre $apellidos") // Solo nombre y apellidos
                    }
                }

                // Crear un ArrayAdapter y asociarlo con el ListView
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, estudianteList)
                listViewDocentes.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al cargar Estudiantes: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        cargarEstudiantes() // Cargar docentes cada vez que se vuelve al fragmento
    }
}

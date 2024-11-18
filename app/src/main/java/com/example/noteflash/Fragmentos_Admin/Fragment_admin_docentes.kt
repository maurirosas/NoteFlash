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
import com.example.noteflash.Registro_Docentes
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_admin_docentes : Fragment() {
    private lateinit var listViewDocentes: ListView
    private lateinit var docenteList: MutableList<String>
    private lateinit var nuevoDButton: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_admin_docentes, container, false)

        // Inicializa los componentes de la interfaz
        listViewDocentes = view.findViewById(R.id.listViewDocentes)
        nuevoDButton = view.findViewById(R.id.nuevoDButton) // Inicializa el botón aquí
        docenteList = mutableListOf()

        // Inicializa Firestore
        firestore = FirebaseFirestore.getInstance()

        // Cargar los datos de Firestore
        cargarDocentes()

        // Configurar el OnClickListener para el botón de registro
        nuevoDButton.setOnClickListener {
            val intent = Intent(requireActivity(), Registro_Docentes::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun cargarDocentes() {
        // Cargar documentos de la colección "usuarios" donde el rol sea "Docente"
        firestore.collection("usuarios")
            .whereEqualTo("rol", "Docente")
            .get()
            .addOnSuccessListener { documents ->
                docenteList.clear() // Limpiar la lista antes de agregar nuevos datos

                for (document in documents) {
                    val nombre = document.getString("nombres")
                    val apellidos = document.getString("apellidos")

                    // Verificar que ambos valores no sean nulos antes de agregarlos a la lista
                    if (nombre != null && apellidos != null) {
                        docenteList.add("$nombre $apellidos") // Solo nombre y apellidos
                    }
                }

                // Crear un ArrayAdapter y asociarlo con el ListView
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, docenteList)
                listViewDocentes.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al cargar Docentes: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        cargarDocentes() // Cargar docentes cada vez que se vuelve al fragmento
    }
}

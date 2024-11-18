package com.example.noteflash.Fragmentos_Docente

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.noteflash.Inicio_sesion
import com.example.noteflash.R
import com.google.firebase.auth.FirebaseAuth

class Fragment_docente_logout : Fragment() {

    private lateinit var btnCerrarSesion: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar la vista del fragmento
        val view = inflater.inflate(R.layout.fragment_docente_logout, container, false)

        // Inicializar FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Referenciar el botón de cerrar sesión
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        // Configurar el click listener del botón
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        return view
    }

    private fun mostrarDialogoCerrarSesion() {
        // Crear cuadro de diálogo
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Está seguro de querer cerrar sesión?")
            .setPositiveButton("Aceptar") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun cerrarSesion() {
        // Cerrar sesión en Firebase
        firebaseAuth.signOut()

        // Navegar a la página de Inicio de Sesión
        val intent = Intent(requireContext(), Inicio_sesion::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        // Finalizar el fragmento actual
        activity?.finish()
    }
}

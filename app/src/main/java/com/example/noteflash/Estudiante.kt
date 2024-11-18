package com.example.noteflash

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteflash.Fragmentos_Docente.Fragment_docente_logout
import com.example.noteflash.Fragmentos_estudiante.Fragment_estudiante_lista_proy
import com.example.noteflash.databinding.ActivityEstudianteBinding

class Estudiante : AppCompatActivity() {
    private lateinit var binding: ActivityEstudianteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEstudianteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        verListaProyectos() // Muestra el fragmento al iniciar la actividad

        binding.BottomNvEstudiante.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.Menu_lista_proyectos_est -> {
                    verListaProyectos()
                    true
                }
                R.id.Menu_logout_est -> {
                    verCerrarSesion()
                    true
                }
                else -> false
            }
        }
    }
    private fun verListaProyectos() {
        val nombre_titulo = "Todos los proyectos"
        binding.TituloRLEstudiante.text = nombre_titulo // Asegúrate de que este ID sea correcto

        val fragment = Fragment_estudiante_lista_proy()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsEstudiante.id, fragment, "Lista Proyectos")
        fragmentTransaction.commit()
    }
    private fun verCerrarSesion() {
        val nombreTitulo = "Cerrar Sesión"
        binding.TituloRLEstudiante.text = nombreTitulo

        val fragment = Fragment_docente_logout()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsEstudiante.id, fragment, "Lista Proyectos")
        fragmentTransaction.commit()
    }
}
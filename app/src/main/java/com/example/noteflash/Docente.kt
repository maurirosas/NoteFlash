package com.example.noteflash

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteflash.Fragmentos_Docente.Fragment_docente_proy_asig
import com.example.noteflash.Fragmentos_Docente.Fragment_docente_lista_proy
import com.example.noteflash.Fragmentos_Docente.Fragment_docente_logout
import com.example.noteflash.databinding.ActivityDocenteBinding
import com.example.noteflash.Inicio_sesion

class Docente : AppCompatActivity() {
    private lateinit var binding: ActivityDocenteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocenteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Manejar insets para evitar superposición con barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mostrar "Proyectos Asignados" por defecto al iniciar la actividad
        verFragmentoProyectosAsig()

        // Configurar el menú inferior con un solo botón
        binding.BottomNvDocente.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_proyectos_asignados -> {
                    verFragmentoProyectosAsig()
                    true
                }
                R.id.Menu_lista_proyectos -> {
                    verListaProyectos()
                    true
                }
                R.id.Menu_logout_doc -> {
                    verCerrarSesion()
                    true
                }
                else -> false
            }
        }
    }

    // Método para mostrar el fragmento de "Proyectos Asignados"
    private fun verFragmentoProyectosAsig() {
        val nombreTitulo = "Proyectos Asignados"
        binding.TituloRLDocente.text = nombreTitulo

        val fragment = Fragment_docente_proy_asig()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsDocente.id, fragment, "Fragment Proyectos Asignados")
        fragmentTransaction.commit()
    }
    // Método para mostrar todos los proyectos
    private fun verListaProyectos() {
        val nombreTitulo = "Todos los Proyectos"
        binding.TituloRLDocente.text = nombreTitulo

        val fragment = Fragment_docente_lista_proy()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsDocente.id, fragment, "Lista Proyectos")
        fragmentTransaction.commit()
    }

    private fun verCerrarSesion() {
        val nombreTitulo = "Cerrar Sesión"
        binding.TituloRLDocente.text = nombreTitulo

        val fragment = Fragment_docente_logout()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsDocente.id, fragment, "Lista Proyectos")
        fragmentTransaction.commit()
    }
}

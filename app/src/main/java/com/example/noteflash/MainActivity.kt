package com.example.noteflash

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteflash.Fragmentos_Admin.Fragment_admin_docentes
import com.example.noteflash.Fragmentos_Admin.Fragment_admin_proyectos
import com.example.noteflash.Fragmentos_Docente.Fragment_admin_lista_proy
import com.example.noteflash.Fragmentos_Docente.Fragment_docente_logout
import com.example.noteflash.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Configuración de la vista
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar el fragmento de docentes al inicio
        verFragmentoDocentes()

        // Configurar el listener del menú de navegación
        binding.BottomNvAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_docente -> {
                    verFragmentoDocentes()
                    true
                }
                R.id.Menu_proyecto -> {
                    verFragmentoProyectos()
                    true
                }
                R.id.Menu_lista_proyectos_adm -> {
                    verListaProyectos()
                    true
                }
                R.id.Menu_logout_admin -> {
                    verCerrarSesion()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun verFragmentoDocentes() {
        val nombre_titulo = "Docentes Registrados"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_admin_docentes()
        val fragmentTrasaction = supportFragmentManager.beginTransaction()
        fragmentTrasaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment Docentes")
        fragmentTrasaction.commit()
    }

    private fun verFragmentoProyectos() {
        val nombre_titulo = "Proyectos Registrados"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_admin_proyectos()
        val fragmentTrasaction = supportFragmentManager.beginTransaction()
        fragmentTrasaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment Proyectos")
        fragmentTrasaction.commit()
    }

    private fun verListaProyectos() {
        val nombreTitulo = "Todos los Proyectos"
        binding.TituloRLAdmin.text = nombreTitulo

        val fragment = Fragment_admin_lista_proy()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Lista Proyectos")
        fragmentTransaction.commit()
    }

    private fun verCerrarSesion() {
        val nombreTitulo = "Cerrar Sesión"
        binding.TituloRLAdmin.text = nombreTitulo

        val fragment = Fragment_docente_logout()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Lista Proyectos")
        fragmentTransaction.commit()
    }

}
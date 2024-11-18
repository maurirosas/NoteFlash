package com.example.noteflash

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Proyectos : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var etProjectName: TextView
    private lateinit var etMember: TextView
    private lateinit var etCareer: TextView
    private lateinit var etProjectSummary: TextView

    // Recibir el nombre del proyecto a través del Intent
    private lateinit var nombreProyecto: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectos)

        // Inicialización de las vistas
        etProjectName = findViewById(R.id.etProject_Name)
        etMember = findViewById(R.id.etMember)
        etCareer = findViewById(R.id.etCareer)
        etProjectSummary = findViewById(R.id.etProject_Summary)

        // Inicializa la referencia a la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("Proyectos")

        // Obtener el nombre del proyecto del Intent
        nombreProyecto = intent.getStringExtra("nombre_proyecto") ?: ""

        // Cargar los datos del proyecto
        cargarDatosProyecto(nombreProyecto)
    }

    private fun cargarDatosProyecto(nombreProyecto: String) {
        // Cargar datos del proyecto basado en el nombre
        databaseReference.orderByChild("nombre_proyecto").equalTo(nombreProyecto)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Obtener el proyecto si existe
                    snapshot.children.firstOrNull()?.let {
                        etProjectName.text = it.child("nombre_proyecto").getValue(String::class.java) ?: ""
                        etMember.text = it.child("integrante").getValue(String::class.java) ?: ""
                        etCareer.text = it.child("carrera").getValue(String::class.java) ?: ""
                        etProjectSummary.text = it.child("resumen_proyecto").getValue(String::class.java) ?: ""
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de errores de la base de datos
                    Toast.makeText(this@Proyectos, "Error al Cargar los Datos", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
package com.example.noteflash
import java.math.BigDecimal
import java.math.RoundingMode

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class Proyectos_Asignados : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var etProjectName: TextView
    private lateinit var etMember: TextView
    private lateinit var etCareer: TextView
    private lateinit var etProjectSummary: TextView
    private lateinit var CalificacionButton: Button

    private lateinit var nombreProyecto: String
    private lateinit var juradoUID: String // UID del jurado que inició sesión

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectos_asignados)

        // Inicialización de las vistas
        etProjectName = findViewById(R.id.etProject_Name)
        etMember = findViewById(R.id.etMember)
        etCareer = findViewById(R.id.etCareer)
        etProjectSummary = findViewById(R.id.etProject_Summary)
        CalificacionButton = findViewById(R.id.CalificacionButton)

        nombreProyecto = intent.getStringExtra("nombreProyecto") ?: ""
        juradoUID = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        cargarDatosProyecto()

        // Guardar calificaciones al presionar el botón
        CalificacionButton.setOnClickListener { guardarCalificaciones() }
    }

    private fun cargarDatosProyecto() {
        // Buscar el proyecto cuyo nombre coincida con el recibido
        db.collection("proyectos").whereEqualTo("nombreProyecto", nombreProyecto)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    Toast.makeText(this, "Proyecto no encontrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val proyecto = documentos.first()
                etProjectName.text = proyecto.getString("nombreProyecto") ?: ""
                etCareer.text = proyecto.getString("carrera") ?: ""
                etProjectSummary.text = proyecto.getString("resumen") ?: ""
                etMember.text = proyecto.getString("estudiante") ?: ""
                // Cargar criterios del jurado
                cargarCriteriosJurado(proyecto)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar proyecto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun cargarCriteriosJurado(proyecto: DocumentSnapshot) {
        // Buscar los criterios del jurado
        val jurados = proyecto.get("jurados") as? List<Map<String, Any>> ?: return
        val jurado = jurados.find { it["uid"] == juradoUID }

        if (jurado != null) {
            // Intentar convertir los criterios a Double de forma segura
            val criterios = (jurado["criterios"] as? List<*>)?.map {
                it.toString().toDoubleOrNull() ?: 0.0
            } ?: listOf()

            // Cargar criterios en los EditText correspondientes
            findViewById<EditText>(R.id.notaCriterio1).setText(criterios.getOrNull(0)?.toString() ?: "0")
            findViewById<EditText>(R.id.notaCriterio2).setText(criterios.getOrNull(1)?.toString() ?: "0")
            findViewById<EditText>(R.id.notaCriterio3).setText(criterios.getOrNull(2)?.toString() ?: "0")
            findViewById<EditText>(R.id.notaCriterio4).setText(criterios.getOrNull(3)?.toString() ?: "0")
            findViewById<EditText>(R.id.notaCriterio5).setText(criterios.getOrNull(4)?.toString() ?: "0")

            // Cargar la calificación final
            val calificacion = jurado["calificacion"]?.toString() ?: "Sin calificación"
            findViewById<TextView>(R.id.calificacionFinal).setText(calificacion)
        }
    }


    private fun guardarCalificaciones() {
        // Obtener las calificaciones de los criterios ingresados
        val criterios = listOf(
            findViewById<EditText>(R.id.notaCriterio1).text.toString().toDoubleOrNull(),
            findViewById<EditText>(R.id.notaCriterio2).text.toString().toDoubleOrNull(),
            findViewById<EditText>(R.id.notaCriterio3).text.toString().toDoubleOrNull(),
            findViewById<EditText>(R.id.notaCriterio4).text.toString().toDoubleOrNull(),
            findViewById<EditText>(R.id.notaCriterio5).text.toString().toDoubleOrNull()
        )

        if (criterios.any { it == null }) {
            Toast.makeText(this, "Completa todas las calificaciones.", Toast.LENGTH_SHORT).show()
            return
        }

        // Calificacion Final
        val sumaCalificaciones =
            criterios[0]!! * 0.25 +
            criterios[1]!! * 0.25 +
            criterios[2]!! * 0.15 +
            criterios[3]!! * 0.20 +
            criterios[4]!! * 0.15
        val roundSumaCalificaciones=BigDecimal(sumaCalificaciones).setScale(2, RoundingMode.HALF_UP).toDouble()

        // Cuadro de diálogo de confirmación
        AlertDialog.Builder(this)
            .setTitle("Confirmar")
            .setMessage("¿Está seguro que desea guardar la calificación? La calificación final es de: $roundSumaCalificaciones.")
            .setPositiveButton("Confirmar") { _, _ ->
                db.collection("proyectos").whereEqualTo("nombreProyecto", nombreProyecto)
                    .get()
                    .addOnSuccessListener { documentos ->
                        if (documentos.isEmpty) {
                            Toast.makeText(this, "Proyecto no encontrado", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        val proyectoDocID = documentos.first().id

                        db.collection("proyectos").document(proyectoDocID)
                            .get()
                            .addOnSuccessListener { documento ->
                                val jurados = documento.get("jurados") as? MutableList<Map<String, Any>> ?: mutableListOf()

                                // Actualizar el jurado correspondiente
                                val juradosActualizados = actualizarCriterios(juradoUID, jurados, criterios, roundSumaCalificaciones)

                                // Calcular el nuevo promedio de calificaciones
                                val promedio = calcularPromedio(juradosActualizados)

                                // Guardar las calificaciones y el promedio en la base de datos
                                db.collection("proyectos").document(proyectoDocID)
                                    .update(
                                        mapOf(
                                            "jurados" to juradosActualizados,
                                            "promedio" to promedio
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Calificaciones guardadas exitosamente.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Función para actualizar solo el jurado correspondiente
    private fun actualizarCriterios(
        uid: String,
        jurados: MutableList<Map<String, Any>>,
        criterios: List<Double?>,
        sumaCalificaciones: Double
    ): List<Map<String, Any>> {
        return jurados.map { jurado ->
            if (jurado["uid"] == uid) {
                jurado.toMutableMap().apply {
                    this["calificacion"] = sumaCalificaciones.toString()
                    this["criterios"] = criterios.map { it.toString() }
                }
            } else {
                jurado
            }
        }
    }

    // Función para calcular el promedio de las calificaciones
    private fun calcularPromedio(jurados: List<Map<String, Any>>): Double {
        val totalCalificaciones = jurados.sumOf {
            it["calificacion"].toString().toDoubleOrNull() ?: 0.0
        }
        return totalCalificaciones / jurados.size
    }


}

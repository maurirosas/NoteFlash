package com.example.noteflash

import java.math.BigDecimal
import java.math.RoundingMode
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class Proyectos_Calificados : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var etProjectName: TextView
    private lateinit var etMember: TextView
    private lateinit var etCareer: TextView
    private lateinit var etProjectSummary: TextView

    // Jurado 1
    private lateinit var criterio1_jurado1: TextView
    private lateinit var criterio2_jurado1: TextView
    private lateinit var criterio3_jurado1: TextView
    private lateinit var criterio4_jurado1: TextView
    private lateinit var criterio5_jurado1: TextView
    // Jurado 2
    private lateinit var criterio1_jurado2: TextView
    private lateinit var criterio2_jurado2: TextView
    private lateinit var criterio3_jurado2: TextView
    private lateinit var criterio4_jurado2: TextView
    private lateinit var criterio5_jurado2: TextView
    // Jurado 3
    private lateinit var criterio1_jurado3: TextView
    private lateinit var criterio2_jurado3: TextView
    private lateinit var criterio3_jurado3: TextView
    private lateinit var criterio4_jurado3: TextView
    private lateinit var criterio5_jurado3: TextView

    // Notas Finales
    private lateinit var notaFinalCriterio1: TextView
    private lateinit var notaFinalCriterio2: TextView
    private lateinit var notaFinalCriterio3: TextView
    private lateinit var notaFinalCriterio4: TextView
    private lateinit var notaFinalCriterio5: TextView
    private lateinit var calificacionFinal: TextView

    private lateinit var nombreProyecto: String
    private lateinit var estudianteUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectos_calificados)

        // Inicialización de vistas
        etProjectName = findViewById(R.id.etProject_Name)
        etMember = findViewById(R.id.etMember)
        etCareer = findViewById(R.id.etCareer)
        etProjectSummary = findViewById(R.id.etProject_Summary)

        criterio1_jurado1 = findViewById(R.id.criterio1_jurado1)
        criterio2_jurado1 = findViewById(R.id.criterio2_jurado1)
        criterio3_jurado1 = findViewById(R.id.criterio3_jurado1)
        criterio4_jurado1 = findViewById(R.id.criterio4_jurado1)
        criterio5_jurado1 = findViewById(R.id.criterio5_jurado1)

        criterio1_jurado2 = findViewById(R.id.criterio1_jurado2)
        criterio2_jurado2 = findViewById(R.id.criterio2_jurado2)
        criterio3_jurado2 = findViewById(R.id.criterio3_jurado2)
        criterio4_jurado2 = findViewById(R.id.criterio4_jurado2)
        criterio5_jurado2 = findViewById(R.id.criterio5_jurado2)

        criterio1_jurado3 = findViewById(R.id.criterio1_jurado3)
        criterio2_jurado3 = findViewById(R.id.criterio2_jurado3)
        criterio3_jurado3 = findViewById(R.id.criterio3_jurado3)
        criterio4_jurado3 = findViewById(R.id.criterio4_jurado3)
        criterio5_jurado3 = findViewById(R.id.criterio5_jurado3)

        notaFinalCriterio1 = findViewById(R.id.nota_final_criterio1)
        notaFinalCriterio2 = findViewById(R.id.nota_final_criterio2)
        notaFinalCriterio3 = findViewById(R.id.nota_final_criterio3)
        notaFinalCriterio4 = findViewById(R.id.nota_final_criterio4)
        notaFinalCriterio5 = findViewById(R.id.nota_final_criterio5)

        calificacionFinal = findViewById(R.id.calificacionFinal)

        nombreProyecto = intent.getStringExtra("nombreProyecto") ?: ""

        cargarDatosProyecto()
    }

    private fun cargarDatosProyecto() {
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

                cargarCriteriosJurado(proyecto)
                calcularNotasFinales(jurados = proyecto.get("jurados") as? List<Map<String, Any>> ?: emptyList())
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar proyecto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarCriteriosJurado(proyecto: DocumentSnapshot) {
        val jurados = proyecto.get("jurados") as? List<Map<String, Any>> ?: return

        jurados.forEachIndexed { index, jurado ->
            val criterios = (jurado["criterios"] as? List<*>)?.map { it.toString() } ?: listOf() // Convertir cada elemento a String
            when (index) {
                0 -> {
                    criterio1_jurado1.text = criterios.getOrNull(0) ?: "0"
                    criterio2_jurado1.text = criterios.getOrNull(1) ?: "0"
                    criterio3_jurado1.text = criterios.getOrNull(2) ?: "0"
                    criterio4_jurado1.text = criterios.getOrNull(3) ?: "0"
                    criterio5_jurado1.text = criterios.getOrNull(4) ?: "0"
                }
                1 -> {
                    criterio1_jurado2.text = criterios.getOrNull(0) ?: "0"
                    criterio2_jurado2.text = criterios.getOrNull(1) ?: "0"
                    criterio3_jurado2.text = criterios.getOrNull(2) ?: "0"
                    criterio4_jurado2.text = criterios.getOrNull(3) ?: "0"
                    criterio5_jurado2.text = criterios.getOrNull(4) ?: "0"
                }
                2 -> {
                    criterio1_jurado3.text = criterios.getOrNull(0) ?: "0"
                    criterio2_jurado3.text = criterios.getOrNull(1) ?: "0"
                    criterio3_jurado3.text = criterios.getOrNull(2) ?: "0"
                    criterio4_jurado3.text = criterios.getOrNull(3) ?: "0"
                    criterio5_jurado3.text = criterios.getOrNull(4) ?: "0"
                }
            }
        }
    }

    private fun calcularNotasFinales(jurados: List<Map<String, Any>>) {
        var sumaCriterio1 = 0.0
        var sumaCriterio2 = 0.0
        var sumaCriterio3 = 0.0
        var sumaCriterio4 = 0.0
        var sumaCriterio5 = 0.0
        var sumaCalificaciones = 0.0

        jurados.forEach { jurado ->
            val calificacion = (jurado["calificacion"] as? String)?.toDoubleOrNull() ?: 0.0
            sumaCalificaciones += calificacion
        }

        // Sumar criterios
        sumaCriterio1 = ((criterio1_jurado1.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio1_jurado2.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio1_jurado3.text.toString().toDoubleOrNull() ?: 0.0))/3

        sumaCriterio2 = ((criterio2_jurado1.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio2_jurado2.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio2_jurado3.text.toString().toDoubleOrNull() ?: 0.0))/3

        sumaCriterio3 = ((criterio3_jurado1.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio3_jurado2.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio3_jurado3.text.toString().toDoubleOrNull() ?: 0.0))/3

        sumaCriterio4 = ((criterio4_jurado1.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio4_jurado2.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio4_jurado3.text.toString().toDoubleOrNull() ?: 0.0))/3

        sumaCriterio5 = ((criterio5_jurado1.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio5_jurado2.text.toString().toDoubleOrNull() ?: 0.0) +
                (criterio5_jurado3.text.toString().toDoubleOrNull() ?: 0.0))/3

        // Mostrar notas finales de cada criterio
        notaFinalCriterio1.text = BigDecimal(sumaCriterio1).setScale(2, RoundingMode.HALF_UP).toString()
        notaFinalCriterio2.text = BigDecimal(sumaCriterio2).setScale(2, RoundingMode.HALF_UP).toString()
        notaFinalCriterio3.text = BigDecimal(sumaCriterio3).setScale(2, RoundingMode.HALF_UP).toString()
        notaFinalCriterio4.text = BigDecimal(sumaCriterio4).setScale(2, RoundingMode.HALF_UP).toString()
        notaFinalCriterio5.text = BigDecimal(sumaCriterio5).setScale(2, RoundingMode.HALF_UP).toString()

        // Calcular y mostrar el promedio de las calificaciones de los jurados
        val promedio = if (jurados.isNotEmpty()) sumaCalificaciones / jurados.size else 0.0
        calificacionFinal.text = BigDecimal(promedio).setScale(2, RoundingMode.HALF_UP).toString()
    }

}

package com.example.noteflash

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.noteflash.databinding.ActivityRegistroProyectosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Registro_Proyectos : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroProyectosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firestore: FirebaseFirestore

    var nombre_proyecto = ""
    var integrante = ""  // Ahora es un campo de texto libre
    var carrera = ""
    var jurado1 = ""
    var jurado2 = ""
    var jurado3 = ""
    var resumen_proyecto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroProyectosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Opciones de carrera
        val carreras = listOf("Contaduría General", "Sistemas Informaticos", "Turismo", "Secretariado Ejecutivo")
        val adapterCarreras = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, carreras)
        binding.etCareer.setAdapter(adapterCarreras)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por Favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.cancelPButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.registerPButton.setOnClickListener {
            ValidarInformacion()
        }

        // Mantener la lógica de carga para los jurados
        cargarJurados()
    }

    private fun cargarJurados() {
        firestore.collection("usuarios")
            .whereEqualTo("rol", "Docente")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val docentesList = mutableListOf<Pair<String, String>>()
                for (document in querySnapshot) {
                    val nombres = document.getString("nombres") ?: "Desconocido"
                    val apellidos = document.getString("apellidos") ?: ""
                    val uid = document.id
                    docentesList.add(Pair("$nombres $apellidos", uid))
                }
                val adapterDocentes = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, docentesList.map { it.first })
                binding.etJury1.setAdapter(adapterDocentes)
                binding.etJury1.setOnItemClickListener { _, _, position, _ ->
                    jurado1 = docentesList[position].second
                }
                binding.etJury2.setAdapter(adapterDocentes)
                binding.etJury2.setOnItemClickListener { _, _, position, _ ->
                    jurado2 = docentesList[position].second
                }
                binding.etJury3.setAdapter(adapterDocentes)
                binding.etJury3.setOnItemClickListener { _, _, position, _ ->
                    jurado3 = docentesList[position].second
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar los docentes", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ValidarInformacion() {
        nombre_proyecto = binding.etProjectName.text.toString().trim()
        integrante = binding.etMember.text.toString().trim()  // Ahora solo se extrae el texto del campo
        carrera = binding.etCareer.text.toString().trim()
        resumen_proyecto = binding.etProjectSummary.text.toString().trim()

        if (nombre_proyecto.isEmpty()) {
            binding.etProjectName.error = "Ingrese el Nombre del Proyecto"
            binding.etProjectName.requestFocus()
            return
        }
        if (integrante.isEmpty()) {
            binding.etMember.error = "Ingrese el Integrante"
            binding.etMember.requestFocus()
            return
        }
        if (carrera.isEmpty() || !listOf("Contaduría General", "Sistemas Informaticos", "Turismo", "Secretariado Ejecutivo").contains(carrera)) {
            binding.etCareer.error = "Seleccione la Carrera"
            binding.etCareer.requestFocus()
            return
        }
        if (jurado1.isEmpty()) {
            binding.etJury1.error = "Seleccione al Jurado 1"
            binding.etJury1.requestFocus()
            return
        }
        if (jurado2.isEmpty()) {
            binding.etJury2.error = "Seleccione al Jurado 2"
            binding.etJury2.requestFocus()
            return
        }
        if (jurado3.isEmpty()) {
            binding.etJury3.error = "Seleccione al Jurado 3"
            binding.etJury3.requestFocus()
            return
        }
        if (resumen_proyecto.isEmpty()) {
            binding.etProjectSummary.error = "Ingrese un Resumen del Proyecto"
            binding.etProjectSummary.requestFocus()
            return
        }

        CrearProyectoAdmin()
    }

    private fun CrearProyectoAdmin() {
        progressDialog.setMessage("Creando Proyecto")
        progressDialog.show()
        AgregarInfoBD()
    }

    private fun AgregarInfoBD() {
        progressDialog.setMessage("Guardando Información...")

        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.currentUser?.uid ?: "defaultUID"

        val datos_proy: HashMap<String, Any?> = HashMap()
        datos_proy["uid"] = uid
        datos_proy["nombreProyecto"] = nombre_proyecto
        datos_proy["carrera"] = carrera
        datos_proy["resumen"] = resumen_proyecto
        datos_proy["estudiante"] = integrante  // Ahora solo guarda el string del integrante
        datos_proy["jurados"] = listOf(
            mapOf("uid" to jurado1, "calificacion" to "", "criterios" to emptyList<String>()),
            mapOf("uid" to jurado2, "calificacion" to "", "criterios" to emptyList<String>()),
            mapOf("uid" to jurado3, "calificacion" to "", "criterios" to emptyList<String>())
        )
        datos_proy["promedio"] = ""

        firestore.collection("proyectos").add(datos_proy)
            .addOnSuccessListener { documentReference ->
                val proyectoUid = documentReference.id
                progressDialog.setMessage("Asignando proyecto a usuarios...")

                asignarProyectoAUsuario(jurado1, proyectoUid)
                asignarProyectoAUsuario(jurado2, proyectoUid)
                asignarProyectoAUsuario(jurado3, proyectoUid)

                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Proyecto creado con éxito", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragmentToLoad", "Fragment_admin_proyectos")
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo guardar la información del proyecto", Toast.LENGTH_SHORT).show()
            }
    }

    private fun asignarProyectoAUsuario(usuarioUid: String, proyectoUid: String) {
        val usuarioRef = firestore.collection("usuarios").document(usuarioUid)

        usuarioRef.update("proyectosAsignados", FieldValue.arrayUnion(proyectoUid))
            .addOnSuccessListener {
                Log.d("Firestore", "Proyecto asignado correctamente a usuario: $usuarioUid")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al asignar proyecto a usuario $usuarioUid", e)
            }
    }
}




package com.example.noteflash

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.noteflash.databinding.ActivityRegistroDocentesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registro_Docentes : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroDocentesBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog

    var nombres = ""
    var apellidos = ""
    var ci = ""
    var carrera = ""
    var usuario = ""
    var contraseña = ""
    var rol = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroDocentesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Inicializa FirebaseAuth y Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por Favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Opciones de carrera
        val carreras = listOf(
            "Contaduría General", "Sistemas Informaticos",
            "Turismo", "Secretariado Ejecutivo"
        )
        val adapterCarreras = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, carreras)
        binding.etCareer.setAdapter(adapterCarreras)

        // Opciones de rol
        val roles = listOf("Administrador", "Docente")
        val adapterRoles = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.etRole.setAdapter(adapterRoles)

        binding.cancelDButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.registerDButton.setOnClickListener {
            ValidarInformacion()
        }
    }

    private fun ValidarInformacion() {
        nombres = binding.etNames.text.toString().trim()
        apellidos = binding.etSurnames.text.toString().trim()
        ci = binding.etCi.text.toString().trim()
        carrera = binding.etCareer.text.toString().trim()
        usuario = binding.etUser.text.toString().trim()
        contraseña = binding.etPassword.text.toString().trim()
        rol = binding.etRole.text.toString().trim()

        // Validaciones
        if (rol.isEmpty() || !listOf("Administrador", "Docente").contains(rol)) {
            binding.etRole.error = "Seleccione el Rol del Usuario"
            binding.etRole.requestFocus()
            return
        }
        if (nombres.isEmpty()) {
            binding.etNames.error = "Ingrese los Nombres"
            binding.etNames.requestFocus()
            return
        }
        if (apellidos.isEmpty()) {
            binding.etSurnames.error = "Ingrese los Apellidos"
            binding.etSurnames.requestFocus()
            return
        }
        if (ci.isEmpty()) {
            binding.etCi.error = "Ingrese el CI"
            binding.etCi.requestFocus()
            return
        }
        if (carrera.isEmpty() || !listOf(
                "Contaduría General", "Sistemas Informaticos",
                "Turismo", "Secretariado Ejecutivo"
            ).contains(carrera)) {
            binding.etCareer.error = "Seleccione la Carrera"
            binding.etCareer.requestFocus()
            return
        }
        if (usuario.isEmpty()) {
            binding.etUser.error = "Ingrese el Usuario"
            binding.etUser.requestFocus()
            return
        }
        if (contraseña.isEmpty()) {
            binding.etPassword.error = "Ingrese la Contraseña"
            binding.etPassword.requestFocus()
            return
        }

        // Si todo está validado, procede a crear la cuenta
        CrearCuentaAdmin(usuario, contraseña)
    }

    private fun CrearCuentaAdmin(email: String, password: String) {
        progressDialog.setMessage("Creando Cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                AgregarInfoFirestore()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo crear la Cuenta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun AgregarInfoFirestore() {
        progressDialog.setMessage("Guardando Información...")
        val uid = firebaseAuth.uid ?: return
        val tiempo = System.currentTimeMillis()

        // Datos a guardar en Firestore
        val datosDocente = hashMapOf(
            "uid" to uid,
            "rol" to rol,
            "nombres" to nombres,
            "apellidos" to apellidos,
            "ci" to ci,
            "carrera" to carrera,
            "usuario" to usuario,
            "tiempo_registro" to tiempo,
            "proyectosAsignados" to listOf<String>() // Campo inicializado como array vacío
        )

        // Guardar en la colección "Usuarios"
        firestore.collection("usuarios").document(uid)
            .set(datosDocente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Cuenta creada con Éxito", Toast.LENGTH_SHORT).show()

                // Cambio a la actividad principal o fragmento
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragmentToLoad", "Fragment_admin_docentes")
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo guardar la Información: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

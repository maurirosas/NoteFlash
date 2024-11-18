package com.example.noteflash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.noteflash.databinding.ActivityInicioSesionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Inicio_sesion : AppCompatActivity() {
    private lateinit var binding: ActivityInicioSesionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Manejar el botón de inicio de sesión
        binding.loginButton.setOnClickListener {
            val email = binding.etUser.text.toString() // Cambié a 'email' para ser más claro
            val password = binding.etPassword.text.toString()

            // Verificar si los campos están vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor Ingrese Usuario y Contraseña", Toast.LENGTH_SHORT).show()
            } else {
                // Autenticar con Firebase
                loginWithFirebase(email, password)
            }
        }
    }

    private fun loginWithFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    Toast.makeText(this, "Inicio de Sesión Exitoso", Toast.LENGTH_SHORT).show()

                    // Obtener el uid del usuario autenticado
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    // Referencia a la colección de Firestore para obtener el rol del usuario
                    firestore.collection("usuarios")
                        .whereEqualTo("uid", uid)  // Cambiar a búsqueda por uid
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                            } else {
                                for (document in documents) {
                                    val rol = document.getString("rol")
                                    Toast.makeText(this, "Rol: $rol", Toast.LENGTH_SHORT).show() // Depuración

                                    // Redirigir según el rol
                                    when (rol) {
                                        "Administrador" -> startActivity(Intent(this, MainActivity::class.java))
                                        "Docente" -> startActivity(Intent(this, Docente::class.java))
                                        "Estudiante" -> startActivity(Intent(this, Estudiante::class.java))
                                        else -> {
                                            // Si no hay rol definido, verificar credenciales fijas
                                            if (email == "incostarija@gmail.com" && password == "123456") {
                                                startActivity(Intent(this, Estudiante::class.java))
                                            } else {
                                                Toast.makeText(this, "Rol no definido", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                            finish() // Finaliza la actividad actual
                        }.addOnFailureListener {
                            Toast.makeText(this, "Error al obtener rol: ${it.message}", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    // Error en el inicio de sesión
                    Toast.makeText(this, "Usuario o Contraseña Incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
package com.example.proyectofinal

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase (esto puede ser opcional si ya lo has configurado en el archivo de configuración)
        FirebaseApp.initializeApp(this)

        // Verificar usuario actual (Ejemplo usando el Singleton)
        checkCurrentUser()

        // Lectura básica de Firestore (Ejemplo usando el Singleton)
        fetchFirestoreData()

        // Cargar WelcomeFragment al iniciar la actividad
        if (savedInstanceState == null) {
            loadFragment(WelcomeFragment())
        }
    }

    private fun checkCurrentUser() {
        // Usando el Singleton para acceder a FirebaseAuth
        val currentUser = FirebaseService.auth.currentUser
        if (currentUser != null) {
            Log.d("Firebase", "Usuario actual: ${currentUser.email}")
        } else {
            Log.d("Firebase", "No hay usuario autenticado")
        }
    }

    private fun fetchFirestoreData() {
        // Usando el Singleton para acceder a Firestore
        FirebaseService.firestore.collection("exampleCollection")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Firebase", "Documento: ${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error al leer Firestore", exception)
            }
    }

    private fun loadFragment(fragment: Fragment) {
        // Método para cargar fragmentos en el contenedor
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

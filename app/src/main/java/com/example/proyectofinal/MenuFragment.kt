package com.example.proyectofinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        // Obtener referencias a los botones
        val btnGame1: Button = view.findViewById(R.id.btnGame1)
        val btnGame2: Button = view.findViewById(R.id.btnGame2)
        val btnGame3: Button = view.findViewById(R.id.btnGame3)
        val btnBack: Button = view.findViewById(R.id.btnBack)

        // Configurar los listeners para cada botón
        btnGame1.setOnClickListener {
            replaceFragment(Game1Fragment())
        }

        btnGame2.setOnClickListener {
            replaceFragment(Game2Fragment())
        }

        btnGame3.setOnClickListener {
            replaceFragment(Game3Fragment())
        }

        btnBack.setOnClickListener {
            replaceFragment(WelcomeFragment())
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        // Inicia la transacción de fragmentos
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment) // Cambia "fragmentContainer" por el id de tu contenedor de fragmentos
        transaction.addToBackStack(null) // Agregar a la pila para volver atrás
        transaction.commit()
    }
}
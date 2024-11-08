package com.example.proyectofinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class Game1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_game1, container, false)

        // Obtener referencia al botón de volver al menú
        val btnBackToMenu: Button = view.findViewById(R.id.btnBackToMenu)
        btnBackToMenu.setOnClickListener {
            // Volver al MenuFragment
            replaceFragment(MenuFragment())
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        // Iniciar la transacción de fragmentos
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
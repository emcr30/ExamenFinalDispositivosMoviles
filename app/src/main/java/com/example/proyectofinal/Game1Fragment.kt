package com.example.proyectofinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.FirebaseFirestore

class Game1Fragment : Fragment() {

    private var playerScore = 0
    private var pcScore = 0
    private lateinit var board: Array<Button>
    private lateinit var tvPlayerScore: TextView
    private lateinit var tvPCScore: TextView
    private lateinit var tvResult: TextView
    private lateinit var finalActions: View
    private var currentTurn = "Player" // Alterna entre "Player" y "PC"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game1, container, false)

        // Inicializar vistas
        tvPlayerScore = view.findViewById(R.id.tvPlayerScore)
        tvPCScore = view.findViewById(R.id.tvPCScore)
        tvResult = view.findViewById(R.id.tvResult)
        finalActions = view.findViewById(R.id.finalActions)
        val btnNext: Button = view.findViewById(R.id.btnNext)
        val btnBackToMenu: Button = view.findViewById(R.id.btnBackToMenu)

        // Inicializar tablero
        board = arrayOf(
            view.findViewById(R.id.btnCell0),
            view.findViewById(R.id.btnCell1),
            view.findViewById(R.id.btnCell2),
            view.findViewById(R.id.btnCell3),
            view.findViewById(R.id.btnCell4),
            view.findViewById(R.id.btnCell5),
            view.findViewById(R.id.btnCell6),
            view.findViewById(R.id.btnCell7),
            view.findViewById(R.id.btnCell8)
        )

        // Configurar listeners del tablero
        board.forEachIndexed { index, button ->
            button.setOnClickListener { onCellClicked(index) }
        }

        // Botón "Siguiente" para nueva partida
        btnNext.setOnClickListener { resetBoard() }

        // Botón "Volver" para regresar al menú
        btnBackToMenu.setOnClickListener {
            replaceFragment(MenuFragment())
        }

        return view
    }

    private fun onCellClicked(index: Int) {
        val button = board[index]
        if (button.text.isEmpty() && currentTurn == "Player") {
            button.text = "X" // Jugador siempre coloca "X"
            if (checkWinner()) {
                endGame(currentTurn)
            } else if (isBoardFull()) {
                endGame("Draw") // Detectar empate
            } else {
                currentTurn = "PC"
                playPC() // Turno de la computadora
            }
        }
    }

    private fun playPC() {
        val emptyCells = board.filter { it.text.isEmpty() }
        if (emptyCells.isNotEmpty()) {
            val randomCell = emptyCells.random()
            randomCell.text = "O" // Computadora coloca "O"
            if (checkWinner()) {
                endGame("PC")
            } else if (isBoardFull()) {
                endGame("Draw") // Detectar empate
            } else {
                currentTurn = "Player" // Regresa el turno al jugador
            }
        }
    }

    private fun checkWinner(): Boolean {
        val winningPositions = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8), // Filas
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8), // Columnas
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6) // Diagonales
        )

        for (position in winningPositions) {
            val (a, b, c) = position
            if (board[a].text.isNotEmpty() && board[a].text == board[b].text && board[a].text == board[c].text) {
                return true
            }
        }
        return false
    }

    private fun isBoardFull(): Boolean {
        return board.all { it.text.isNotEmpty() }
    }

    private fun endGame(winner: String) {
        // Determinar el ganador y actualizar los puntajes
        when (winner) {
            "Player" -> {
                playerScore++
                tvResult.text = "¡Ganaste!"
            }
            "PC" -> {
                pcScore++
                tvResult.text = "¡Perdiste!"
            }
            "Draw" -> {
                tvResult.text = "¡Empate!"
            }
        }
        tvPlayerScore.text = "Jugador 1: $playerScore"
        tvPCScore.text = "PC: $pcScore"
        tvResult.visibility = View.VISIBLE
        finalActions.visibility = View.VISIBLE

        // Guardar el resultado de la partida en Firestore
        saveGameHistory(playerScore, pcScore, winner)
    }

    private fun saveGameHistory(playerScore: Int, pcScore: Int, winner: String) {
        val db = FirebaseFirestore.getInstance()

        val gameData = hashMapOf(
            "playerScore" to playerScore,
            "pcScore" to pcScore,
            "winner" to winner,
            "timestamp" to System.currentTimeMillis()  // Timestamp de la partida
        )

        db.collection("gameHistory")
            .add(gameData)
            .addOnSuccessListener {
                Log.d("Firebase", "Historial de juego guardado exitosamente")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al guardar el historial de juego", e)
            }
    }

    private fun resetBoard() {
        board.forEach { it.text = "" }
        tvResult.visibility = View.GONE
        finalActions.visibility = View.GONE
        currentTurn = "Player"
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}

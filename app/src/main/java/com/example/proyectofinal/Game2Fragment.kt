package com.example.proyectofinal

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class Game2Fragment : Fragment() {

    private lateinit var gridBoard: GridLayout
    private lateinit var tvScore: TextView
    private lateinit var btnBackToMenu: Button

    private val board = Array(6) { IntArray(7) { 0 } } // Tablero: 0 = vacío, 1 = jugador, 2 = PC
    private var playerScore = 0
    private var pcScore = 0
    private var currentPlayer = 1 // 1 = jugador, 2 = PC

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game2, container, false)

        gridBoard = view.findViewById(R.id.gridBoard)
        tvScore = view.findViewById(R.id.tvScore)
        btnBackToMenu = view.findViewById(R.id.btnBackToMenu)

        // Inicializar el tablero
        initializeBoard()

        // Manejo de botones de columnas
        for (col in 0..6) {
            val btnCol = view.findViewById<Button>(resources.getIdentifier("btnCol$col", "id", requireContext().packageName))
            btnCol.setOnClickListener {
                handleColumnClick(col)
            }
        }

        // Botón de volver al menú
        btnBackToMenu.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MenuFragment())
                .commit()
        }

        return view
    }

    private fun initializeBoard() {
        gridBoard.removeAllViews()
        for (row in 0..5) {
            for (col in 0..6) {
                val cell = View(requireContext())
                val layoutParams = GridLayout.LayoutParams().apply {
                    width = 100
                    height = 100
                    setMargins(4, 4, 4, 4) // Usa setMargins para establecer los márgenes
                    rowSpec = GridLayout.spec(row)
                    columnSpec = GridLayout.spec(col)
                }
                cell.layoutParams = layoutParams
                cell.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                gridBoard.addView(cell)
            }
        }
        // Resetear el tablero lógico
        for (i in board.indices) {
            board[i].fill(0)
        }
        currentPlayer = 1 // Reiniciar al jugador
    }

    private fun handleColumnClick(col: Int) {
        val row = findAvailableRow(col)
        if (row != -1) {
            placeChip(row, col, currentPlayer)
            if (checkWin(row, col, currentPlayer)) {
                if (currentPlayer == 1) {
                    playerScore++
                    Toast.makeText(requireContext(), "¡Ganaste!", Toast.LENGTH_SHORT).show()
                } else {
                    pcScore++
                    Toast.makeText(requireContext(), "¡Perdiste!", Toast.LENGTH_SHORT).show()
                }
                updateScore()
                initializeBoard()
                return
            }
            if (isBoardFull()) {
                Toast.makeText(requireContext(), "¡Empate!", Toast.LENGTH_SHORT).show()
                initializeBoard()
                return
            }
            currentPlayer = if (currentPlayer == 1) 2 else 1 // Cambiar turno
            if (currentPlayer == 2) pcMove()
        } else {
            Toast.makeText(requireContext(), "Columna llena", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findAvailableRow(col: Int): Int {
        for (row in 5 downTo 0) {
            if (board[row][col] == 0) return row
        }
        return -1
    }

    private fun placeChip(row: Int, col: Int, player: Int) {
        board[row][col] = player
        val cell = gridBoard.getChildAt(row * 7 + col)
        cell.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (player == 1) android.R.color.holo_blue_light else android.R.color.holo_red_light
            )
        )
    }

    private fun checkWin(row: Int, col: Int, player: Int): Boolean {
        return checkDirection(row, col, player, 1, 0) || // Horizontal
                checkDirection(row, col, player, 0, 1) || // Vertical
                checkDirection(row, col, player, 1, 1) || // Diagonal \
                checkDirection(row, col, player, 1, -1)   // Diagonal /
    }

    private fun checkDirection(row: Int, col: Int, player: Int, dRow: Int, dCol: Int): Boolean {
        var count = 0
        for (i in -3..3) {
            val r = row + i * dRow
            val c = col + i * dCol
            if (r in 0..5 && c in 0..6 && board[r][c] == player) {
                count++
                if (count == 4) return true
            } else {
                count = 0
            }
        }
        return false
    }

    private fun isBoardFull(): Boolean {
        return board.all { row -> row.all { cell -> cell != 0 } }
    }

    private fun pcMove() {
        // Movimiento simple aleatorio para la PC
        var col: Int
        do {
            col = (0..6).random()
        } while (findAvailableRow(col) == -1)
        handleColumnClick(col)
    }

    private fun updateScore() {
        tvScore.text = "Jugador: $playerScore | PC: $pcScore"
    }
}
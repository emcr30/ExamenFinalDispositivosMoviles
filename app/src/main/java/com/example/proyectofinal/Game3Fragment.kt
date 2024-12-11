// Aportacion de Evelyn Chipana

package com.example.proyectofinal

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.random.Random

class Game3Fragment : Fragment() {

    private lateinit var gameContainer: FrameLayout
    private lateinit var tvScore: TextView
    private var score = 0
    private val snakeBody = mutableListOf<Point>()
    private var direction = Direction.RIGHT
    private val handler = Handler()
    private val gridSize = 20
    private var foodPosition: Point? = null
    private val snakeSpeed = 1000L // Velocidad inicial (500ms por movimiento)

    private enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game3, container, false)

        gameContainer = view.findViewById(R.id.gameContainer)
        tvScore = view.findViewById(R.id.tvScore)

        val btnUp: Button = view.findViewById(R.id.btnUp)
        val btnDown: Button = view.findViewById(R.id.btnDown)
        val btnLeft: Button = view.findViewById(R.id.btnLeft)
        val btnRight: Button = view.findViewById(R.id.btnRight)

        btnUp.setOnClickListener { if (direction != Direction.DOWN) direction = Direction.UP }
        btnDown.setOnClickListener { if (direction != Direction.UP) direction = Direction.DOWN }
        btnLeft.setOnClickListener { if (direction != Direction.RIGHT) direction = Direction.LEFT }
        btnRight.setOnClickListener { if (direction != Direction.LEFT) direction = Direction.RIGHT }

        startGame()

        return view
    }

    private fun startGame() {
        // Inicializar la serpiente
        snakeBody.clear()
        snakeBody.add(Point(5, 5))
        score = 0
        tvScore.text = "Puntos: $score"

        // Generar comida inicial
        generateFood()

        // Iniciar el ciclo del juego
        handler.postDelayed(::gameLoop, snakeSpeed)
    }

    private fun gameLoop() {
        moveSnake()
        checkCollision()
        updateUI()
        handler.postDelayed(::gameLoop, snakeSpeed)
    }

    private fun moveSnake() {
        val head = snakeBody.first()
        val newHead = when (direction) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }

        snakeBody.add(0, newHead) // Agregar nueva cabeza
        if (newHead != foodPosition) {
            snakeBody.removeLast() // Remover la cola si no come
        } else {
            score += 10
            tvScore.text = "Puntos: $score"
            generateFood()
        }
    }

    private fun checkCollision() {
        val head = snakeBody.first()

        // Colisión con paredes
        if (head.x < 0 || head.x >= gridSize || head.y < 0 || head.y >= gridSize) {
            endGame("Perdiste: Colisión con pared")
        }

        // Colisión con el cuerpo
        if (snakeBody.drop(1).contains(head)) {
            endGame("Perdiste: Colisión contigo mismo")
        }
    }

    private fun generateFood() {
        do {
            val x = Random.nextInt(gridSize)
            val y = Random.nextInt(gridSize)
            foodPosition = Point(x, y)
        } while (foodPosition in snakeBody)
    }

    private fun updateUI() {
        gameContainer.removeAllViews()

        // Dibujar comida
        foodPosition?.let { addCell(it, Color.RED) }

        // Dibujar serpiente
        snakeBody.forEach { addCell(it, Color.GREEN) }
    }

    private fun addCell(position: Point, color: Int) {
        val cellSize = gameContainer.width / gridSize
        val cell = View(context)
        cell.setBackgroundColor(color)
        val params = FrameLayout.LayoutParams(cellSize, cellSize)
        params.leftMargin = position.x * cellSize
        params.topMargin = position.y * cellSize
        gameContainer.addView(cell, params)
    }

    private fun endGame(message: String) {
        handler.removeCallbacksAndMessages(null)
        tvScore.text = message
    }


}
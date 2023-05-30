package com.example.breakingblock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameViewActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onBackPressed() {
        gameView.showCustomDialog()
        gameView.isPlay = false
        gameView.savedBallSpeedY = gameView.ballSpeedY
        gameView.savedBallSpeedX = gameView.ballSpeedX
    }
}


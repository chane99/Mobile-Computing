package com.example.breakingblock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.breakingblock.databinding.ActivityMainBinding

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        gameView = GameView(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.gamestart.setOnClickListener {
            val intent = Intent(this, GameViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {   // 종료되면 메모리 누수를 방지
        super.onDestroy()
        gameView.isEnd = false
    }

}

package com.example.breakingblock

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.example.breakingblock.databinding.ActivityMainBinding

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameView: GameView
    private var bgmPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        gameView = GameView(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bgmPlayer = MediaPlayer.create(this, R.raw.titlemusic) // 미디어 플레이어 초기화
        bgmPlayer?.isLooping = true // 반복 재생 설정
        bgmPlayer?.start()
        binding.gamestart.setOnClickListener {
            val intent = Intent(this, GameViewActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onPause() { //액티비티가 전환되면 호출되는 함수
        super.onPause()
        bgmPlayer?.pause()
    }
    override fun onResume() {
        super.onResume()
        bgmPlayer = MediaPlayer.create(this, R.raw.titlemusic)
        if (bgmPlayer != null) {
            bgmPlayer!!.start()
        }
    }
    override fun onDestroy() {   // 종료되면 메모리 누수를 방지
        super.onDestroy()
        gameView.isEnd = false
        bgmPlayer?.stop() // 배경음악 정지
        bgmPlayer?.reset() // 미디어 플레이어 초기화
        bgmPlayer?.release() // 리소스 해제
    }

}

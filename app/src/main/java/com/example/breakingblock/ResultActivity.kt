package com.example.breakingblock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.breakingblock.databinding.ActivityResultBinding
import com.google.android.play.integrity.internal.t

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_result)

        val intent = intent
        val nickName = intent.getStringExtra("nickName") // MainActivity로부터 닉네임 전달받음
        val photoURL = intent.getStringExtra("photoURL") // MainActivity로부터 프로필사진 전달받음

        binding.tvResult.text = nickName // 닉네임 텍스트를 텍스트 뷰에 설정
        Glide.with(this).load(photoURL).into(binding.ivProfile) // 프로필 URL을 이미지 뷰에 설정
    }
}

package com.example.breakingblock

import android.app.Activity
import android.os.Bundle
import com.example.breakingblock.databinding.ActivityMainBinding

class MainActivity : Activity() {
    lateinit var binding :ActivityMainBinding
    lateinit var m_View : MainView
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        m_View = MainView(this)
        super.onCreate(savedInstanceState)
        setContentView(m_View)
    }



    override fun onDestroy() {   //종료가 될시 메모리 누수를 방지
        super.onDestroy()
        m_View.m_IsEnd = false
    }
}
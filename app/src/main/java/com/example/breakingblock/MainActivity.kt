package com.example.breakingblock


import UserAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.breakingblock.databinding.ActivityMainBinding
import com.example.breakingblock.roomdb.ScoreDatabase
import com.example.breakingblock.roomdb.User
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.*


class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameView: GameView
    private var bgmPlayer: MediaPlayer? = null
    private var backPressedTime: Long = 0
    private lateinit var adapter: UserAdapter
    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증처리






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var db = ScoreDatabase.getInstance(applicationContext)
        mFirebaseAuth = FirebaseAuth.getInstance()


        // 미디어 플레이어 초기화
        bgmPlayer = MediaPlayer.create(this, R.raw.titlemusic)
        bgmPlayer?.isLooping = true



        // 게임 스타트 버튼 클릭시
        binding.gamestart.setOnClickListener {
            val intent = Intent(this, GameViewActivity::class.java)
            startActivity(intent)
        }

        // 로컬 랭킹 버튼 클릭시
        binding.localrank.setOnClickListener {
            adapter = UserAdapter(mutableListOf(), object : UserAdapter.OnItemClickListener {
            override fun onItemLongClick(user: User) {
                // 데이터베이스에서 해당 행 삭제 로직 구현
                CoroutineScope(Dispatchers.IO).launch {
                    db!!.scoreDao().delete(user)
                }
                // dataSet에서도 삭제할 수 있도록 처리
                val position = adapter.deleteSelectScore(user)
                if (position != -1) {
                    adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()
                }
            }
        })
            CoroutineScope(Dispatchers.Main).launch {
                val dialogView = layoutInflater.inflate(R.layout.local_ranking_view, null)
                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setView(dialogView)
                    .create()
                val recyclerView = dialogView.findViewById<RecyclerView>(R.id.localrank_recycler_view)
                val userList =CoroutineScope(Dispatchers.IO).async {
                    db!!.scoreDao().selectAll()
                }.await()
                Log.d("메인코드", "Data 세트: $userList")
                withContext(Dispatchers.Main) {
                    adapter.setList(userList) // 기존 adapter에 새로운 데이터 목록 설정
                    recyclerView.adapter = adapter
                }
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))

                dialog.show()
            }
        }
        binding.worldrank.setOnClickListener {
            val intent = Intent(this, WorldrankActivity::class.java)
            startActivity(intent)
        }
        binding.login.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.logout.setOnClickListener{
            binding.logout.setOnClickListener {
                mFirebaseAuth.signOut()
                if (mFirebaseAuth.currentUser == null) {
                    Toast.makeText(this@MainActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "로그아웃에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }



    private fun deleteRowFromDatabase(user: User) {
        val scoreDao = ScoreDatabase.getInstance(this)?.scoreDao()
        scoreDao?.let {
            it.delete(user)
            adapter.setList(it.selectAll())
        }
    }


    override fun onPause() {
        super.onPause()
        bgmPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        bgmPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.isEnd = false
        bgmPlayer?.stop()
        bgmPlayer?.reset()
        bgmPlayer?.release()
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > 2000) {
            Toast.makeText(this, "한번 더 누르면 게임을 종료합니다", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        } else {
            finishAffinity()
        }
    }
}
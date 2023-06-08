package com.example.breakingblock

import UserAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.breakingblock.databinding.ActivityMainBinding
import com.example.breakingblock.roomdb.ScoreDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Games
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*


class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameView: GameView
    private var bgmPlayer: MediaPlayer? = null

    private var backPressedTime: Long = 0
    private lateinit var adapter: UserAdapter
    private val RC_SIGN_IN = 9001
    private val RC_LEADERBOARD_UI = 9002

    private var googleSignInAccount: GoogleSignInAccount? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var db = ScoreDatabase.getInstance(applicationContext)
        signinsilently()


        // 미디어 플레이어 초기화
        bgmPlayer = MediaPlayer.create(this, R.raw.titlemusic)
        bgmPlayer?.isLooping = true

        // 구글 로그인 버튼 클릭시
        binding.btnGoogle.setOnClickListener {
            bgmPlayer?.pause()
            login()
        }

        // 게임 스타트 버튼 클릭시
        binding.gamestart.setOnClickListener {
            val intent = Intent(this, GameViewActivity::class.java)
            startActivity(intent)
        }

        // 로컬 랭킹 버튼 클릭시
        binding.localrank.setOnClickListener {
            adapter = UserAdapter(mutableListOf())
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
            rank()
        }
        binding.logout.setOnClickListener{
            logout()
        }




    }

    fun login() {
        signinIntent()
    }

    fun logout() {
        signout()
    }


    fun rank() {
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            Games.getLeaderboardsClient(this, it)
                .getLeaderboardIntent(getString(R.string.leaderboard_breakingblock_ranking))
                .addOnSuccessListener { intent ->
                    startActivityForResult(intent, RC_LEADERBOARD_UI)
                }
        }
    }

    private fun signinIntent() {
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        val intent: Intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                googleSignInAccount = task.getResult(ApiException::class.java)

                // 로그인 성공시
                binding.btnGoogle.visibility = View.INVISIBLE
                binding.logout.visibility = View.VISIBLE
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            } catch (apiException: ApiException) {
                val message = apiException.message
                if (message == null || message.isEmpty()) {
                    Toast.makeText(this, "기타 오류 발생", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "$message 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signout() {
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        val logout: Task<Void> = signInClient.signOut()

        logout.addOnCompleteListener(this) { task ->
            // 로그아웃 완료시
            binding.btnGoogle.visibility = View.VISIBLE
            binding.logout.visibility = View.INVISIBLE
        }
    }
    private fun signinsilently() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null && GoogleSignIn.hasPermissions(account, *signInOptions.scopeArray)) {
            // 이미 로그인됨
            googleSignInAccount = account
            binding.btnGoogle.visibility = View.INVISIBLE
            binding.logout.visibility = View.VISIBLE
        } else {
            // 로그인 안됨
            Toast.makeText(this, "오프라인입니다. 로그인하세요.", Toast.LENGTH_SHORT).show()
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
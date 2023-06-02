package com.example.breakingblock

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
import com.example.breakingblock.roomdb.UserAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameView: GameView
    private var bgmPlayer: MediaPlayer? = null
    private lateinit var auth: FirebaseAuth // 파이어베이스 인증 객체
    private lateinit var googleSignInClient: GoogleSignInClient // 구글 로그인 클라이언트 객체
    private val REQ_SIGN_GOOGLE = 100
    private var backPressedTime: Long = 0
    private lateinit var db: ScoreDatabase
    private lateinit var adapter: UserAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CoroutineScope(Dispatchers.Main).launch {
            db = withContext(Dispatchers.IO) { ScoreDatabase.getInstance(applicationContext) }
            val userList = withContext(Dispatchers.IO) { db!!.scoreDao().selectAll() }
            Log.d("Data check", "User List: $userList")
            adapter = UserAdapter(userList)
        }



        // 파이어베이스 인증 객체 초기화
        auth = FirebaseAuth.getInstance()

        // 구글 로그인 옵션 설정
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // 구글 로그인 클라이언트 초기화
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // 미디어 플레이어 초기화
        bgmPlayer = MediaPlayer.create(this, R.raw.titlemusic)
        bgmPlayer?.isLooping = true



        // 구글 로그인 버튼 클릭시
        binding.btnGoogle.setOnClickListener {
            bgmPlayer?.pause()
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, REQ_SIGN_GOOGLE)
        }

        // 게임 스타트 버튼 클릭시
        binding.gamestart.setOnClickListener {
            val intent = Intent(this, GameViewActivity::class.java)
            startActivity(intent)
        }

        // 로컬 랭킹 버튼 클릭시
        binding.localrank.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val dialogView = layoutInflater.inflate(R.layout.local_ranking_view, null)
                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setView(dialogView)
                    .create()

                val recyclerView = dialogView.findViewById<RecyclerView>(R.id.localrank_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))

                val userList = withContext(Dispatchers.IO) { db!!.scoreDao().selectAll() }
                adapter.setList(userList) // 기존 adapter에 새로운 데이터 목록 설정

                recyclerView.adapter = adapter
                dialog.show()
            }
        }








    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_SIGN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                resultLogin(account)
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resultLogin(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, ResultActivity::class.java)
                intent.putExtra("nickName", account?.displayName)
                intent.putExtra("photoURL", account?.photoUrl?.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
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

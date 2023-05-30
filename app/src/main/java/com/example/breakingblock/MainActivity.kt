package com.example.breakingblock

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.breakingblock.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameView: GameView
    private var bgmPlayer: MediaPlayer? = null
    private lateinit var auth: FirebaseAuth // 파이어베이스 인증 객체
    private lateinit var googleSignInClient: GoogleSignInClient // 구글 로그인 클라이언트 객체
    private val REQ_SIGN_GOOGLE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        gameView = GameView(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 파이어베이스 인증 객체 초기화
        auth = FirebaseAuth.getInstance()

        // 구글 로그인 옵션 설정
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // 구글 로그인 클라이언트 초기화
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)


        bgmPlayer = MediaPlayer.create(this, R.raw.titlemusic) // 미디어 플레이어 초기화
        bgmPlayer?.isLooping = true // 반복 재생 설정
        bgmPlayer?.start()

        //구글로그인 버튼 눌렀을때
        binding.btnGoogle.setOnClickListener{
            bgmPlayer?.pause()
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, REQ_SIGN_GOOGLE)
        }
        //게임스타트 버튼 클릭시 액티비티 전환
        binding.gamestart.setOnClickListener {
            val intent = Intent(this, GameViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_SIGN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("TAGMING", "인증성공")
                resultLogin(account)
            } catch (e: ApiException) {
                Log.d("TAGMING", "인증실패: ${e.statusCode}")
                Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun resultLogin(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) { // 로그인 성공시
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, ResultActivity::class.java)
                intent.putExtra("nickName", account?.displayName)
                Log.d("TAGMING", "${account?.displayName}")
                intent.putExtra("photoURL", account?.photoUrl?.toString()) // 특정 자료형을 String 형태로 변환시킴
                startActivity(intent)

            } else { // 로그인 실패
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
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
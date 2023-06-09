package com.example.breakingblock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 회원정보 데이터베이스
    private lateinit var mEtEmail: EditText
    private lateinit var mEtPw: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("회원관리")
        mEtEmail = findViewById(R.id.et_email)
        mEtPw = findViewById(R.id.et_pw)
        val btn_login: Button = findViewById(R.id.btn_login)
        val btn_register: Button = findViewById(R.id.btn_register)
        btn_login.setOnClickListener(View.OnClickListener {
            val strEmail = mEtEmail.text.toString()
            val strPwd = mEtPw.text.toString()

            mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd)
                .addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@LoginActivity, "로그인에 성공하셨습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인에 실패하셨습니다", Toast.LENGTH_SHORT).show()
                    }
                }

        })
        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}

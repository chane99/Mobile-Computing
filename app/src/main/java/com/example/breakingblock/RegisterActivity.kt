package com.example.breakingblock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 회원정보 데이터베이스
    private lateinit var mEtEmail: EditText
    private lateinit var mEtPw: EditText
    private lateinit var mEtNickname: EditText
    private lateinit var mBtnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("회원관리")
        mEtEmail = findViewById(R.id.et_email)
        mEtPw = findViewById(R.id.et_pw)
        mEtNickname = findViewById(R.id.et_nickname)
        mBtnRegister = findViewById(R.id.btn_newregister)

        mBtnRegister.setOnClickListener(View.OnClickListener setOnClickListener@{
            val strEmail = mEtEmail.text.toString()
            val strPwd = mEtPw.text.toString()
            val mNickname = mEtNickname.text.toString()

            if (strPwd.length < 6) {
                Toast.makeText(this@RegisterActivity, "비밀번호는 최소 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이메일 중복 확인
            mFirebaseAuth.fetchSignInMethodsForEmail(strEmail)
                .addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        val signInMethods = emailTask.result?.signInMethods
                        if (signInMethods != null && signInMethods.isNotEmpty()) {
                            // 이미 가입된 이메일인 경우
                            Toast.makeText(this@RegisterActivity, "이미 가입된 이메일입니다", Toast.LENGTH_SHORT).show()
                        } else {
                            // 이메일 중복 없음, 닉네임 중복 확인
                            val nicknameRef = mDatabaseRef.child("UserAccount").orderByChild("nickname").equalTo(mNickname)
                            nicknameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // 이미 존재하는 닉네임인 경우
                                        Toast.makeText(this@RegisterActivity, "이미 존재하는 닉네임입니다", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // 존재하지 않는 이메일과 닉네임인 경우, 회원 가입 진행
                                        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd)
                                            .addOnCompleteListener(this@RegisterActivity, OnCompleteListener<AuthResult> { task ->
                                                if (task.isSuccessful) {
                                                    // 회원가입 성공
                                                    val firebaseUser: FirebaseUser? = mFirebaseAuth.currentUser
                                                    val account = UserAccount()
                                                    account.idToken = firebaseUser?.uid
                                                    account.emailId = firebaseUser?.email
                                                    account.password = strPwd
                                                    account.displayName = mNickname

                                                    // 데이터베이스에 insert
                                                    mDatabaseRef.child("UserAccount").child(firebaseUser?.uid!!).setValue(account)

                                                    // Firebase 사용자 프로필 업데이트
                                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                                        .setDisplayName(mNickname)
                                                        .build()

                                                    firebaseUser?.updateProfile(profileUpdates)
                                                        ?.addOnCompleteListener { profileTask ->
                                                            if (profileTask.isSuccessful) {
                                                                // 프로필 업데이트 성공
                                                                Toast.makeText(this@RegisterActivity, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show()

                                                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                                                startActivity(intent)
                                                                finish() // 현재 액티비티 종료
                                                            } else {
                                                                // 프로필 업데이트 실패
                                                                Toast.makeText(this@RegisterActivity, "프로필 업데이트에 실패하셨습니다", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }

                                                } else {
                                                    // 회원가입 실패
                                                    Toast.makeText(this@RegisterActivity, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // 에러 처리
                                }
                            })
                        }
                    } else {
                        // 이메일 중복 확인 실패
                        Toast.makeText(this@RegisterActivity, "이메일 중복 확인에 실패하셨습니다", Toast.LENGTH_SHORT).show()
                    }
                }
        })

    }
}

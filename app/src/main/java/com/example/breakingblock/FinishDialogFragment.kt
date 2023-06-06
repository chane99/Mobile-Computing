package com.example.breakingblock

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.breakingblock.databinding.FinishLayoutBinding
import com.example.breakingblock.roomdb.ScoreDatabase
import com.example.breakingblock.roomdb.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates


class FinishDialogFragment(private val score: Int) : DialogFragment() {
    private var _binding: FinishLayoutBinding? = null
    private val binding get() = _binding!!
    private var saveClickListener: (() -> Unit)? = null
    private var exitClickListener: (() -> Unit)? = null
    private var retryClickListener: (() -> Unit)? = null
    private lateinit var resultTextView: TextView
    private lateinit var db: ScoreDatabase
    private lateinit var resultTextView2: TextView
    private var countdata by Delegates.notNull<Int>()


    override fun onStart() {
        super.onStart()
        resultTextView.text = (context as AppCompatActivity).getString(R.string.score_label, score)
        getHighestScore()
        getLowestScore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FinishLayoutBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.setCancelable(false)
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        db = ScoreDatabase.getInstance(requireContext().applicationContext)!!
        resultTextView = binding.result
        resultTextView2 = binding.highResult
        binding.newrankmessage.visibility = View.INVISIBLE
        binding.editname.visibility = View.GONE
        binding.saveBtn.visibility = View.GONE

        binding.saveBtn.setOnClickListener {
            val edtname = binding.editname.text.toString().trim()
            if (edtname.isEmpty()) {
                Toast.makeText(context, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val userCount = withContext(Dispatchers.IO) {
                        db.scoreDao().getCount()
                    }
                    countdata = userCount

                    Toast.makeText(context, "점수가 랭킹에 등록됐습니다", Toast.LENGTH_SHORT).show()
                    val scoreEntity = User(name = edtname, score = score)
                    insertScore(scoreEntity)
                    selectAllUsers()
                    if (countdata > 15) {
                        deleteLastScore()
                    }
                    binding.newrankmessage.visibility = View.INVISIBLE
                    binding.editname.visibility = View.GONE
                    binding.saveBtn.visibility = View.GONE

                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.editname.windowToken, 0)
                }
            }
        }




        binding.retryBtn.setOnClickListener {
            retryClickListener?.invoke()
            dismiss()

        }
        binding.exitBtn.setOnClickListener {
            exitClickListener?.invoke()
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnSaveClickListener(listener: () -> Unit) {
        saveClickListener = listener
    }

    fun setOnExitClickListener(listener: () -> Unit) {
        exitClickListener = listener
    }

    fun setOnRetryClickListener(listener: () -> Unit) {
        retryClickListener = listener
    }

    private fun insertScore(user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                db.scoreDao().insert(user)
            }
        }
    }
    private fun selectAllUsers() {
        CoroutineScope(Dispatchers.Main).launch {
            val users = withContext(Dispatchers.IO) {
                db.scoreDao().selectAll()
            }
            // 선택된 사용자들에 대한 처리
            users.forEach { user ->
                // 사용자 정보 처리
                // 예: Log 출력
                Log.d("User", "Name: ${user.name}, Score: ${user.score}")
            }
        }
    }
    fun getHighestScore() {
        CoroutineScope(Dispatchers.Main).launch {
            val user = withContext(Dispatchers.IO) {
                db.scoreDao().getHighestScore()
            }
            var highestScore = user?.score ?: 0
            if (::resultTextView2.isInitialized) { // null 체크 추가
                resultTextView2.text = (context as AppCompatActivity).getString(R.string.high_score_label, highestScore)
            }
        }
    }

    fun getLowestScore() {
        CoroutineScope(Dispatchers.Main).launch {
            val user = withContext(Dispatchers.IO) {
                db.scoreDao().getLowestScore()
            }
            var lowestScore = user?.score ?: 0

            if(lowestScore < score){
                binding.newrankmessage.visibility=View.VISIBLE
                binding.editname.visibility = View.VISIBLE
                binding.saveBtn.visibility = View.VISIBLE
            }

        }
    }


    fun deleteLastScore() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                db.scoreDao().deleteLowestScore()
            }

        }
    }





}
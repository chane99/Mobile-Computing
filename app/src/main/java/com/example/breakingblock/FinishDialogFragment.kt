package com.example.breakingblock

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.breakingblock.databinding.FinishLayoutBinding
import com.example.breakingblock.roomdb.ScoreDatabase
import com.example.breakingblock.roomdb.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FinishDialogFragment(private val score: Int) : DialogFragment() {
    private var _binding: FinishLayoutBinding? = null
    private val binding get() = _binding!!
    private var saveClickListener: (() -> Unit)? = null
    private var exitClickListener: (() -> Unit)? = null
    private var retryClickListener: (() -> Unit)? = null
    private lateinit var resultTextView: TextView
    private lateinit var db: ScoreDatabase

    override fun onStart() {
        super.onStart()
        resultTextView.text = (context as AppCompatActivity).getString(R.string.score_label, score)
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

        binding.saveBtn.setOnClickListener {
            val scoreEntity = User(name = "kingmingseo", score = score)
            insertScore(scoreEntity)
            selectAllUsers()
            dismiss()
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

}
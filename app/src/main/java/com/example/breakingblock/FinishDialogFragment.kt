package com.example.breakingblock

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.breakingblock.databinding.FinishLayoutBinding
import com.example.breakingblock.roomdb.ScoreDatabase
import com.example.breakingblock.roomdb.user
import kotlinx.coroutines.launch

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
        db = Room.databaseBuilder(requireContext(), ScoreDatabase::class.java, "scores-db").build()
        dialog?.setCancelable(false)
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        resultTextView = binding.result

        binding.saveBtn.setOnClickListener {
            val scoreEntity = user(name = "kingmingseo", score = score)
            insertScore(scoreEntity)
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

    private fun insertScore(scoreEntity: user) {
        val scoreDao = db.scoreDao()

        // 코루틴을 사용하여 비동기로 insert 수행
        lifecycleScope.launch {
            val newRowId = scoreDao.insert(scoreEntity)
            Log.d("DB_INSERT", "New row ID: $newRowId") // 로그 추가

            Toast.makeText(requireContext(), "Score saved", Toast.LENGTH_SHORT).show()


        }
    }
}
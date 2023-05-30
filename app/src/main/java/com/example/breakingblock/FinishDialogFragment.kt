package com.example.breakingblock

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.breakingblock.databinding.FinishLayoutBinding
import com.example.breakingblock.databinding.PauseLayoutBinding

class FinishDialogFragment(private val score: Int) : DialogFragment() {
    private var _binding: FinishLayoutBinding? = null
    private val binding get() = _binding!!
    private var saveClickListener: (() -> Unit)? = null
    private var exitClickListener: (() -> Unit)? = null
    private var retryClickListener: (() -> Unit)? = null
    private lateinit var resultTextView: TextView

    override fun onStart() {
        super.onStart()
        resultTextView.text = (context as AppCompatActivity).getString(R.string.score_label, score)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FinishLayoutBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.setCancelable(false)
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        resultTextView = binding.result

        binding.saveBtn.setOnClickListener {
            saveClickListener?.invoke()
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


}
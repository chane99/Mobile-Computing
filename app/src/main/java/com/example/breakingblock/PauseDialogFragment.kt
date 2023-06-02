package com.example.breakingblock

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.breakingblock.databinding.PauseLayoutBinding

class PauseDialogFragment : DialogFragment() {
    private var _binding: PauseLayoutBinding? = null
    private val binding get() = _binding!!
    private var continueClickListener: (() -> Unit)? = null
    private var exitClickListener: (() -> Unit)? = null
    private var retryClickListener: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PauseLayoutBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.setCancelable(false)

        // 레이아웃 배경을 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.retryBtn.setOnClickListener {
            dismiss()
            retryClickListener?.invoke()
        }
        binding.exitBtn.setOnClickListener {
            dismiss()
            exitClickListener?.invoke()

        }
        binding.continueBtn.setOnClickListener {
            dismiss()
            continueClickListener?.invoke()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun setOnContinueClickListener(listener: () -> Unit) {
        continueClickListener = listener
    }
    fun setOnExitClickListener(listener: () -> Unit) {
        exitClickListener = listener
    }
    fun setOnRetryClickListener(listener: () -> Unit) {
        retryClickListener = listener
    }


}
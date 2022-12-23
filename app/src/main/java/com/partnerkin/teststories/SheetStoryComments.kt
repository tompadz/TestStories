package com.partnerkin.teststories

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.partnerkin.teststories.databinding.SheetStoryCommentsBinding

class SheetStoryComments(private val onDismiss : () -> Unit) : BottomSheetDialogFragment() {

    private var _binding : SheetStoryCommentsBinding? = null
    private val binding get() = _binding !!

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        _binding = SheetStoryCommentsBinding.inflate(inflater)
        initCloseButton()
        return binding.root
    }

    private fun initCloseButton() {
        binding.apply {
            buttonClose.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog : DialogInterface) {
        onDismiss()
        super.onDismiss(dialog)
    }

    companion object {
        const val TAG = "SheetStoryComments"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
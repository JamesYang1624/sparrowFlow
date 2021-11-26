package com.yangwz.common.base

import android.app.Dialog
import android.graphics.Color
import androidx.databinding.ViewDataBinding
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment<DB : ViewDataBinding?> : DialogFragment() {
    protected var binding: DB? = null
    public var TAG = javaClass.simpleName
    var isShow = false //防多次点击
    override fun onStart() {
        super.onStart()
        val dialog = dialog!!
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        start(dialog)
    }

    abstract fun start(dialog: Dialog?)
    override fun show(manager: FragmentManager, tag: String?) {
        if (isShow) {
            return
        }
        super.show(manager, tag)
        isShow = true
    }

    override fun dismiss() {
        super.dismiss()
        isShow = false
    }

    override fun onResume() {
        super.onResume()
    }
}
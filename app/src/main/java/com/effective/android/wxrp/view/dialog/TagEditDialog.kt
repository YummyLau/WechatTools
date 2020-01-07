package com.effective.android.wxrp.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.effective.android.wxrp.R
import kotlinx.android.synthetic.main.dialog_add_tag.*

class TagEditDialog constructor(context: Context, commitListener: CommitListener) : Dialog(context, R.style.easy_dialog_style) {

    init {
        val root: View = LayoutInflater.from(context).inflate(R.layout.dialog_add_tag, null, false)
        setContentView(root)
        window?.setGravity(Gravity.CENTER)
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
        setCanceledOnTouchOutside(true)

        layout_left.setOnClickListener {
            dismiss()
        }

        layout_right.setOnClickListener {
            val tag = edit.text.toString()
            commitListener.commit(tag)
            edit.setText("")
            dismiss()
        }
    }

    interface CommitListener {
        fun commit(tag: String)
    }
}
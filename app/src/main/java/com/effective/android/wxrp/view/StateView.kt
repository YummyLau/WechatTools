package com.effective.android.wxrp.view

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.effective.android.wxrp.R
import kotlinx.android.synthetic.main.view_state.view.*

class StateView : LinearLayout {

    lateinit var root: View

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    private fun initView(context: Context) {
        root = LayoutInflater.from(context).inflate(R.layout.view_state, this, true)
        gravity = Gravity.CENTER_VERTICAL
        onStart()
    }

    fun onStart() {
        root.failView.isSelected = true
        root.step1View.isSelected = false
        root.step2View.isSelected = false
        root.step3View.isSelected = false
        root.step4View.isSelected = false
        root.step5View.isSelected = false
        root.successView.isSelected = false
    }

    fun onDoing() {
        root.failView.isSelected = false
        root.step1View.isSelected = true
        root.step2View.isSelected = true
        root.step3View.isSelected = true
        root.step4View.isSelected = false
        root.step5View.isSelected = false
        root.successView.isSelected = false
    }

    fun onEnd() {
        root.failView.isSelected = false
        root.step1View.isSelected = true
        root.step2View.isSelected = true
        root.step3View.isSelected = true
        root.step4View.isSelected = true
        root.step5View.isSelected = true
        root.successView.isSelected = true
    }
}

package com.effective.android.wxrp.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


import com.effective.android.wxrp.R

/**
 * 状态栏view
 * Created by yummylau on 2017/9/09.
 */

class StatusBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    @ColorRes
    private var mStatusBarColor: Int = 0

    init {
        initView(attrs, defStyleAttr)
    }

    private fun initView(attrs: AttributeSet?, defStyle: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_statusbar, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusBarView, defStyle, 0)
        mStatusBarColor = typedArray?.getResourceId(R.styleable.StatusBarView_status_bar_color, R.color.colorPrimary)
                ?: R.color.colorPrimary
        fitSpecialModelStatusBar()
        setBackgroundColor(ContextCompat.getColor(context, mStatusBarColor))
        val statusBarHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) getStatusBarHeight(context) else 0
        view.findViewById<View>(R.id.status_bar).minimumHeight = statusBarHeight
        //        view.findViewById(R.id.status_bar).getLayoutParams().height = statusbarHeight;
    }

    /**
     * 特殊机型的状态栏处理.
     */
    private fun fitSpecialModelStatusBar() {
        if (TextUtils.equals(Build.MODEL, "vivo X6S A")) {
            mStatusBarColor = Color.TRANSPARENT
        }
    }

    companion object {


        fun getStatusBarHeight(context: Context): Int {
            var sbar = 0
            try {
                val c = Class.forName("com.android.internal.R\$dimen")
                val obj = c.newInstance()
                val field = c.getField("status_bar_height")
                val x = Integer.parseInt(field.get(obj).toString())
                sbar = context.resources.getDimensionPixelSize(x)
            } catch (var7: Exception) {
                var7.printStackTrace()
            }

            return sbar
        }
    }
}

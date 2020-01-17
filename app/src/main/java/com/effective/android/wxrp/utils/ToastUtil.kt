package com.effective.android.wxrp.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtil {

    @JvmStatic
    fun toast(context: Context, @StringRes int: Int) = Toast.makeText(context, int, Toast.LENGTH_LONG).show()

    @JvmStatic
    fun toast(context: Context, msg: String) = Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

    @JvmStatic
    fun toast(context: Context, msg: String, time: Int) = Toast.makeText(context, msg, time).show()
}
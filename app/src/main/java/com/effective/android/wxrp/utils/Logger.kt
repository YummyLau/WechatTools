package com.effective.android.wxrp.utils

import android.util.Log
import com.effective.android.wxrp.Constants

object Logger {

    @JvmStatic
    fun e(tag: String, msg: String) = Log.e(Constants.logTag +  " -- $tag", msg)

    @JvmStatic
    fun w(tag: String, msg: String) = Log.w(Constants.logTag +  " -- $tag", msg)

    @JvmStatic
    fun i(tag: String, msg: String) = Log.i(Constants.logTag +  " -- $tag", msg)

    @JvmStatic
    fun d(tag: String, msg: String) = Log.d(Constants.logTag +  " -- $tag", msg)

    @JvmStatic
    fun v(tag: String, msg: String) = Log.v(Constants.logTag +  " -- $tag", msg)
}

package com.effective.android.wxrp.utils

import android.util.Log

object Logger {

    private const val TAG = "logger - "

    @JvmStatic
    fun e(tag: String, msg: String) = Log.e("$TAG $tag", msg)

    @JvmStatic
    fun w(tag: String, msg: String) = Log.w("$TAG $tag", msg)

    @JvmStatic
    fun i(tag: String, msg: String) = Log.i("$TAG $tag", msg)

    @JvmStatic
    fun d(tag: String, msg: String) = Log.d("$TAG $tag", msg)

    @JvmStatic
    fun v(tag: String, msg: String) = Log.v("$TAG $tag", msg)
}

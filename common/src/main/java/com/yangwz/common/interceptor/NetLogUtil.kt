package com.yangwz.common.interceptor

import android.util.Log
import com.yangwz.common.BuildConfig

object NetLogUtil {
    private const val TAG = "xunaidoc_log"
    private const val TAG_NET = "xunaidoc_net"

    fun i(message: String) {
        if (BuildConfig.DEBUG) Log.i(TAG, message)
    }

    fun e(message: String) {
        if (BuildConfig.DEBUG) Log.e(TAG, message)
    }

    fun showHttpHeaderLog(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG_NET, message)
    }

    fun showHttpApiLog(message: String) {
        if (BuildConfig.DEBUG) Log.w(TAG_NET, message)
    }

    fun showHttpLog(message: String) {
        if (BuildConfig.DEBUG) Log.i(TAG_NET, message)
    }

}
package com.yangwz.common.utils

import com.yangwz.common.utils.OneClickUtil
import java.util.*

class OneClickUtil(val methodName: String) {
    private var lastClickTime: Long = 0
    fun check(): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        return if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            false
        } else {
            true
        }
    }

    companion object {
        const val MIN_CLICK_DELAY_TIME = 1000
    }
}
package com.yangwz.common.utils

import java.util.ArrayList
import kotlin.jvm.JvmOverloads

class AntiShake {
    private val utils: MutableList<OneClickUtil> = ArrayList<OneClickUtil>()
    @JvmOverloads
    fun check(o: Any? = null): Boolean {
        var flag: String? = null
        flag = o?.toString() ?: Thread.currentThread().stackTrace[2].methodName
        for (util in utils) {
            if (util.methodName.equals(flag)) {
                return util.check()
            }
        }
        val clickUtil = OneClickUtil(flag!!)
        utils.add(clickUtil)
        return clickUtil.check()
    }
}
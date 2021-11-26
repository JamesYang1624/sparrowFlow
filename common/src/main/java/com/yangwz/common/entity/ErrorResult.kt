package com.xunai.common.repository


/**
 * @author : yangweizheng
 * @date : 2021/7/12 15:05
 */
class ErrorResult @JvmOverloads constructor(
    var code: String ="",
    var errMsg: String? = "",
    var show: Boolean = false,
    var index: Int = 0//表示api类型（确定是哪个api）
)

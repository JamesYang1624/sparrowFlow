package com.xunai.common.http



/**
 * @author : yangweizheng
 * @date : 2021/5/12 17:11
 */
data class BasicResponse<T>(
    val data: T, val code: String, val msg: String
) {
    fun responseData(): T {
        if (code == "0000" || code.toInt() == 0) {
            return data
        } else {
            throw ResponseException(code, msg)
        }
    }
}

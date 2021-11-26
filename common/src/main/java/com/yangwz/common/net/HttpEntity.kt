package com.yangwz.common.net

/**
 * @author : yangweizheng
 * @date : 2021/2/24 14:19
 */
class HttpEntity<T : Any?> {
    var code: String= TODO()
    var data: T = TODO()
        private set
    var msg: String = TODO()
    fun setData(data: T) {
        this.data = data
    }

    override fun toString(): String {
        return "HttpEntity{" +
                "code='" + code + '\'' +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}'
    }
}
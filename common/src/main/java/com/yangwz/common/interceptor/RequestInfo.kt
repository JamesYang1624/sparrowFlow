package com.yangwz.common.interceptor

/**
 * @author : yangweizheng
 * @date : 2021/2/25 17:15
 */
class RequestInfo {
    var url: String= TODO()
    var headers: Map<String, List<String>> = TODO()
    var body: String= TODO()
    var fullRequest: String = TODO()
    var peekBody: String = TODO()
    override fun toString(): String {
        return "RequestInfo{" +
                "url='" + url + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", fullRequest='" + fullRequest + '\'' +
                ", peekBody='" + peekBody + '\'' +
                '}'
    }
}
package com.yangwz.common.interceptor

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @author : ywz
 * @date : 2021/1/29 11:45
 */
class BaseUrInterceptor : Interceptor {
    companion object {
        const val TAG = "BaseUrInterceptor"
        const val URL_NAME = "url_name"
        const val URL_TYPE_1 = "url_name"
        const val URL_TYPE_2 = "url_name"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val requestBuilder = originalRequest.newBuilder()
        val headers = originalRequest.headers(URL_NAME)
        if (headers.isNotEmpty()) {
            requestBuilder.removeHeader(URL_NAME)
            val urlName = headers[0]
            var newBaseUrl: HttpUrl? = null
            if (URL_TYPE_1 == urlName) {
                newBaseUrl = "222".toHttpUrlOrNull()
            } else if (URL_TYPE_2 == urlName) {
                newBaseUrl = "3333".toHttpUrlOrNull()
            }
            Log.d(TAG, "intercept: ")
            var newHttpRequest: HttpUrl = originalUrl.newBuilder()
                    .scheme(newBaseUrl!!.scheme)
                    .host(newBaseUrl.host)
                    .port(newBaseUrl.port)
                    .build()
            val newRequest: Request = requestBuilder.url(newBaseUrl).build()
            return chain.proceed(newRequest)
        } else {
            return chain.proceed(originalRequest)
        }
    }
}
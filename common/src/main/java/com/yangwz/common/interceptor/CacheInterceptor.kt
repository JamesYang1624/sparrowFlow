package com.yangwz.common.interceptor

import okhttp3.*
import okio.Buffer
import kotlin.Throws
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.charset.UnsupportedCharsetException
import java.util.HashMap

/**
 * Date : 2019/4/23 0023
 * Time : 17:43
 * Author : {JamesYang}
 * description :${缓存管理类}
 *
 * @author ywz
 */
class CacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val requestBuffer = Buffer()
        val response: Response = chain.proceed(request)
        val headers = request.headers
        val requestInfo = RequestInfo()
        val body = request.body
        val responseBody = response.body
        try {
            val url = "" + request.url
            requestInfo.url = url
        } catch (e: Exception) {
            requestInfo.url = "" + e.toString()
        }
        val map: MutableMap<String, List<String>?> = HashMap()
        try {
            val stringListMap = headers.toMultimap()
            requestInfo.headers = stringListMap
        } catch (e: Exception) {
            map["no header"] = null
            requestInfo.headers = map
        }
        if (request.body != null) {
            request.body!!.writeTo(requestBuffer)
            val s = requestBuffer.readString(UTF8)
            requestInfo.body = s
        } else {
            requestInfo.body = "body == null "
        }
        var respBody: String? = null
        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer()
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8)
                } catch (e: UnsupportedCharsetException) {
                    e.printStackTrace()
                }
            }
            respBody = buffer.clone().readString(charset!!)
        }
        return response
    }

    companion object {
        private val UTF8 = StandardCharsets.UTF_8
        private const val TAG = "CacheInterceptor"
        val LINE_SEPARATOR = System.getProperty("line.separator")
        @Throws(UnsupportedEncodingException::class)
        private fun parseParams(body: RequestBody, requestBuffer: Buffer): String {
            return if (body.contentType() != null && !body.contentType().toString()
                    .contains("multipart")
            ) {
                URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8")
            } else "null"
        }
    }
}
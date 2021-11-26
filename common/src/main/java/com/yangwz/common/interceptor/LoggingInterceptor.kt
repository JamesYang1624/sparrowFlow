package com.yangwz.common.interceptor

import com.yangwz.common.interceptor.NetLogUtil.showHttpApiLog
import com.yangwz.common.interceptor.NetLogUtil.showHttpHeaderLog
import com.yangwz.common.interceptor.NetLogUtil.showHttpLog
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder


class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val httpUrl = request.url
        val t1 = System.nanoTime() //请求发起的时间
        val response = chain.proceed(request)
        val t2 = System.nanoTime() //收到响应的时间
        val responseBody = response.peekBody(1024 * 1024.toLong())
        if (httpUrl.toString().contains(".png")
            || httpUrl.toString().contains(".jpg")
            || httpUrl.toString().contains(".jpeg")
            || httpUrl.toString().contains(".gif")
        ) {
            return response
        }
        var baseUrl = ""
        if (RetrofitUrlManager.getInstance().baseUrl!=null){
            baseUrl = RetrofitUrlManager.getInstance().baseUrl.toUrl().toString()
        }
        var api = httpUrl.toString().replace(baseUrl, "")
        if (api.contains("?")) {
            api = api.substring(0, api.indexOf("?"))
        }
        val result = responseBody.string()
        showHttpHeaderLog(
            String.format(
                "%n%s%n%s",
                " ",
                request.headers.toString()
            )
        )
        if (request.method == "POST" || request.method == "PUT") {
            if (api.contains("upload")) showHttpApiLog(
                String.format(
                    "%s%n%s%n%s%n%s%n%s%n",
                    "请求URL>>$httpUrl",
                    "API>>$api",
                    "请求方法>>" + request.method,
                    "请求参数>>" + request.body.toString(),
                    "请求耗时>>" + String.format("%.1f", (t2 - t1) / 1e6) + "ms"
                )
            ) else showHttpApiLog(
                java.lang.String.format(
                    "%s%n%s%n%s%n%s%n%s%n",
                    "请求URL>>$httpUrl",
                    "API>>$api",
                    "请求方法>>" + request.method,
                    "请求参数>>" + URLDecoder.decode(bodyToString(request.body), "UTF-8"),
                    "请求耗时>>" + String.format("%.1f", (t2 - t1) / 1e6) + "ms"
                )
            )
        } else {
            showHttpApiLog(
                String.format(
                    "%s%n%s%n%s%n%s%n",
                    "请求URL>>$httpUrl",
                    "API>>$api",
                    "请求方法>>" + request.method,
                    "请求耗时>>" + String.format("%.1f", (t2 - t1) / 1e6) + "ms"
                )
            )
        }

        if (result.length > 4000) {
            val chunkCount = result.length / 4000 // integer division
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                if (max >= result.length) {
                    showHttpLog(
                        String.format(
                            "%s%n%s%n%s%n",
                            "请求结果>>>   " + result.substring(4000 * i),
                            " ",
                            " "
                        )
                    )
                } else {
                    showHttpLog(
                        String.format(
                            "%s%n%s%n%s%n",
                            "请求结果>>>    " + result.substring(4000 * i, max),
                            " ",
                            " "
                        )
                    )
                }
            }
        } else {
            showHttpLog(String.format("%s%n%s%n%s%n", "请求结果>>>    $result", " ", ""))
        }
        return response
    }

    fun bodyToString(request: RequestBody?): String? {
        return try {
            val buffer = Buffer()
            if (request != null) request.writeTo(buffer) else return ""
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }
}
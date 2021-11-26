package com.yangwz.common.net

import android.util.Log
import com.xunai.common.repository.RetrofitCoroutineDSL
import com.yangwz.common.interceptor.CacheInterceptor
import com.yangwz.common.interceptor.LoggingInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.ConnectException
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @author : ywz
 * @date : 2021/1/29 11:05
 */
class HttpRequestManage private constructor() {
    //临时的headers
    private var tmpHeaders: HashMap<String, String> = HashMap()
    private var headers: HashMap<String, String> = HashMap()
    private var retrofit: Retrofit

    private object SingleHolder {
        val holder = HttpRequestManage()
    }


    init {
        //配置缓存的路径，和缓存空间的大小
        val cache = Cache(File("/Users/zeal/Desktop/temp"), 10 * 10 * 1024)
        val level = HttpLoggingInterceptor.Level.BODY
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(
                "httpLoggingInterceptor",
                message
            )
        }

        //        val okHttpClientBuilder = OkHttpClient().newBuilder()
        val okHttpClientBuilder = RetrofitUrlManager.getInstance().with(OkHttpClient().newBuilder())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            //默认重试一次
            .retryOnConnectionFailure(true)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(CacheInterceptor())

        okHttpClientBuilder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            //添加header
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.header("Accept", "application/json");
            for ((key, value) in headers.entries) {
                requestBuilder.header(key, value)
            }
            for ((key, value) in tmpHeaders.entries) {
                requestBuilder.header(key, value)
            }
            val builder = requestBuilder.method(request.method, request.body)
            val newRequest = builder.build()
            chain.proceed(newRequest)
        })
        //打印日志
//        okHttpClientBuilder.addInterceptor(CacheInterceptor())
        okHttpClientBuilder.addInterceptor(LoggingInterceptor())

        retrofit = Retrofit.Builder()
            .baseUrl(RetrofitUrlManager.getInstance().globalDomain)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }


    companion object {
        private val getInstance = SingleHolder.holder

        fun <T> create(service: Class<T>): T {
            return getInstance.retrofit.create(service)!!
        }

        fun reset() {
            return getInstance.reset()
        }

        fun header(key: String, value: String): HttpRequestManage {
            getInstance.header(key, value)
            return getInstance
        }

        fun headers(headers: HashMap<String, String>): HttpRequestManage {
            getInstance.headers(headers)
            return getInstance
        }

        fun useTmpHeader(): HttpRequestManage {
            val map = java.util.HashMap<String, String>()
            map["Authorization"] = "APPCODE 116faa6b9b72449a95dd1c6b57a6878e"
            map["Content-Type"] = "application/x-www-form-urlencoded"
            map["Accept"] = "application/json"
            getInstance.headers(map)
            return getInstance
        }

        fun userCommonHeaders(): HttpRequestManage {
            val map = java.util.HashMap<String, String>()
//            map["Authorization"] = SpUtils.getInstance().token
            map["Content-Type"] = "application/json"
            map["Accept"] = "application/json"
            getInstance.headers(map)
            return getInstance
        }

        fun addHeaders(headers: HashMap<String, String>): HttpRequestManage {
            getInstance.addHeaders(headers)
            return getInstance
        }

        fun alwaysHeaders(key: String, value: String): HttpRequestManage {
            getInstance.alwaysHeaders(key, value)
            return getInstance
        }

    }


    private fun reset() {
        tmpHeaders.clear()
        headers.clear()
    }

    private fun header(key: String, value: String) {
        tmpHeaders[key] = value
    }

    private fun headers(headers: HashMap<String, String>) {
        this.tmpHeaders = headers
    }

    private fun addHeaders(headers: HashMap<String, String>) {
        for ((key, value) in headers.entries) {
            this.tmpHeaders[key] = value
        }
    }

    private fun alwaysHeaders(key: String, value: String) {
        headers[key] = value
    }

    fun <T> CoroutineScope.retrofit(dsl: RetrofitCoroutineDSL<T>.() -> Unit) {
        //在主线程中开启协程
        this.launch(Dispatchers.Main) {
            val coroutine = RetrofitCoroutineDSL<T>().apply(dsl)
            coroutine.api?.let { call ->
                //async 并发执行 在IO线程中
                val deferred = async(Dispatchers.IO) {
                    try {
                        call.execute() //已经在io线程中了，所以调用Retrofit的同步方法
                    } catch (e: ConnectException) {
                        coroutine.onFail?.invoke("网络连接出错", -1)
                        null
                    } catch (e: IOException) {
                        coroutine.onFail?.invoke("未知网络错误", -1)
                        null
                    }
                }
                //当协程取消的时候，取消网络请求
                deferred.invokeOnCompletion {
                    if (deferred.isCancelled) {
                        call.cancel()
                        coroutine.clean()
                    }
                }
                //await 等待异步执行的结果
                val response = deferred.await()
                if (response == null) {
                    coroutine.onFail?.invoke("返回为空", -1)
                } else {
                    response.let {
//                        if (response.isSuccessful) {
//                            //访问接口成功
//                            if (response.body()?.status == 1) {
//                                //判断status 为1 表示获取数据成功
//                                coroutine.onSuccess?.invoke(response.body()!!.data)
//                            } else {
//                                coroutine.onFail?.invoke(response.body()?.msg ?: "返回数据为空", response.code())
//                            }
//                        } else {
//                            coroutine.onFail?.invoke(response.errorBody().toString(), response.code())
//                        }
                    }
                }
                coroutine.onComplete?.invoke()
            }
        }
    }

}
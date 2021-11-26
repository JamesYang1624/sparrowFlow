package com.xunai.common.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import java.io.IOException
import java.net.ConnectException


/**
 * @author : yangweizheng
 * @date : 2021/7/12 11:38
 */
class RetrofitCoroutineDSL<T> {
    var api: (Call<Result<T>>)? = null
    internal var onSuccess: ((T) -> Unit)? = null
        private set
    internal var onFail: ((msg: String, errorCode: Int) -> Unit)? = null
        private set
    internal var onComplete: (() -> Unit)? = null
        private set

    /**
     * 获取数据成功
     * @param block (T) -> Unit
     */
    fun onSuccess(block: (T) -> Unit) {
        this.onSuccess = block
    }

    /**
     * 获取数据失败
     * @param block (msg: String, errorCode: Int) -> Unit
     */
    fun onFail(block: (msg: String, errorCode: Int) -> Unit) {
        this.onFail = block
    }

    /**
     * 访问完成
     * @param block () -> Unit
     */
    fun onComplete(block: () -> Unit) {
        this.onComplete = block
    }

    internal fun clean() {
        onSuccess = null
        onComplete = null
        onFail = null
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
                        if (response.isSuccessful) {
                            //访问接口成功
                            coroutine.onSuccess?.invoke(response.body()!!.getOrNull()!!)
                        } else {
                            coroutine.onFail?.invoke(
                                response.errorBody().toString(),
                                response.code()
                            )
                        }
                    }
                }
                coroutine.onComplete?.invoke()
            }
        }
    }


}
package com.yangwz.common.base.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.Since
import com.xunai.common.http.BasicResponse
import com.xunai.common.http.ResponseException
import com.xunai.common.repository.ErrorResult
import com.yangwz.common.base.MyApp
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CancellationException


typealias Block<T> = suspend () -> T
typealias Error = suspend (e: Exception) -> Unit
typealias Cancel = suspend (e: Exception) -> Unit

/**
 * @author : yangweizheng
 * @date : 2021/5/12 16:20
 */
open class BaseViewModel(application: Application) : BaseLifeCycleViewModel() {
    private val tag = "BaseViewModel"
    val startRefresh = MutableLiveData<Boolean>()
    val startLoadMore = MutableLiveData<Boolean>()
    val noMoreData = MutableLiveData<Boolean>()

    //是否显示loading
    var isShowLoading = MutableLiveData<Boolean>()

    //错误信息
    var errorData = MutableLiveData<ErrorResult>()

    //没有数据
    var emptyData = MutableLiveData<Boolean>()
    protected var mapValue = mutableMapOf<String, Any>()

    /*
   * 启动协程，不带loading
   */
    @Deprecated(message = "建议使用新的launch方法")
    protected fun launch(block: Block<Unit>, error: Error? = null, cancel: Cancel? = null): Job {
        return viewModelScope.launch {
            try {
                block.invoke()
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> {
                        cancel?.invoke(e)
                    }
                    is ResponseException -> {

                    }
                    else -> {
                        handleError(e)
                        error?.invoke(e)
                    }
                }
            }
        }
    }

    /*
    * 启动协程
    */
    protected fun <T> async(block: Block<T>): Deferred<T> {
        return viewModelScope.async { block.invoke() }
    }

    /**
     * 处理异常
     */
    private fun handleError(e: Exception) {
        Toast.makeText(MyApp.instance, e.message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 请求接口，可定制是否显示loading和错误提示
     */
    fun <T> launch(
        block: suspend CoroutineScope.() -> T,//请求接口方法，T表示data实体泛型，调用时可将data对应的bean传入即可
        liveData: MutableLiveData<T>,
        isShowLoading: Boolean = false,
        isShowError: Boolean = true
    ) {
        if (isShowLoading) showLoading()
        viewModelScope.launch {
            val result = block()
            try {

            } catch (e: Throwable) {

            } finally {
                //请求完成
                dismissLoading()
            }
        }
    }

    private fun showLoading() {
        isShowLoading.value = true
    }

    private fun dismissLoading() {
        isShowLoading.value = false
    }

    private fun showError(error: ErrorResult) {
        errorData.value = error
    }
}
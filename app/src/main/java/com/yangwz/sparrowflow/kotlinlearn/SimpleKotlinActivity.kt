package com.yangwz.sparrowflow.kotlinlearn

import androidx.activity.viewModels
import com.yangwz.common.base.BaseActivity
import com.yangwz.sparrowflow.databinding.ActivitySimpleKotlinBinding
import com.yangwz.sparrowflow.viewmodel.SimpleKotlinViewModel
import kotlinx.coroutines.*


/**
 * @author : yangweizheng
 * @date : 2022/2/14 14:20
 */
class SimpleKotlinActivity :
    BaseActivity<ActivitySimpleKotlinBinding>(ActivitySimpleKotlinBinding::inflate) {
    private val viewModel by viewModels<SimpleKotlinViewModel>()
    override fun initViewModel() {
    }

    override fun initView() {

    }

    override fun initData() {
//        main()
//        MyTest()
//        main2()
//        main3()
//        repeatCancel()
//        repeatCancel1()
//        repeatCancelTryCatch()
        repeatCancelWithTimeOut()
    }

    private fun repeatCancelWithTimeOut() = runBlocking {
        withTimeoutOrNull(1500L) {
            repeat(100_000) {
                println(" repeat: I'm sleeping $it")
                delay(500L)
            }
        }.runCatching {
            println(" runCatching: I'm run in runCatching")
        }
    }

    private fun repeatCancelTryCatch() = runBlocking {
        val job = launch {
            try {
                repeat(1000) {
                    println("job :I'm is sleeping $it")
                    delay(500)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job : I'm run at finally")
                    delay(1000L)
                    println("job : and I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L)
        println("main :I'm waiting now quickly")
        job.cancelAndJoin()
        println("main : I'm quit now")
    }

    val currentTime = System.currentTimeMillis()
    private fun repeatCancel1() = runBlocking {
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = currentTime
            var i = 0
//            while (i < 5) {
            while (isActive) {
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L)
        println("main I'm waiting now !!!!")
        job.cancelAndJoin()
        println("main I'm out over")

    }

    private fun repeatCancel() = runBlocking {
        val job = launch {
            repeat(1000) {
                println("job sleeping $it ...")
                delay(500L)
            }
        }
        delay(1300L)
        println("main : I'm  waiting now , quickly ,quickly")
//        job.cancel()
//        job.join()
        job.cancelAndJoin()
        println("main : I'm out ,It's over")
    }


    fun MyTest() = runBlocking {
        val job = GlobalScope.launch {
            delay(1000)
            println("MyTest World!")
        }
        println("MyTest Hello, ")
        job.join()
    }

    fun main() = runBlocking<Unit> {
        GlobalScope.launch {
            delay(1000)
            println("main World!")
        }
        println("main Hello,")
        delay(2000)
//        Thread.sleep(2000)
//        runBlocking {
//            delay(2000)
//        }
    }

    fun main2() = runBlocking {
        launch {
            delay(200L)
            println("Task from runBlocking")
        }
        coroutineScope {
            launch {
                delay(500L)
                println("Task from nested launch")
            }
            delay(100L)
            println("Task from coroutine Scope")
        }
        println("Task scope is over")
    }

    private fun main3() = runBlocking {
        launch { doWorld() }
        println("Hello,")
    }

    suspend fun doWorld() {
        delay(1000L)
        println("World!")
    }
}
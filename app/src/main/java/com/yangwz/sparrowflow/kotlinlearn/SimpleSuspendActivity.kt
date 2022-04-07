package com.yangwz.sparrowflow.kotlinlearn

import com.yangwz.common.base.BaseActivity
import com.yangwz.sparrowflow.databinding.ActivitySimpleSuspendBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime
import kotlinx.coroutines.*
import java.lang.ArithmeticException


/**
 * @author : yangweizheng
 * @date : 2022/2/14 17:27
 */
class SimpleSuspendActivity :
    BaseActivity<ActivitySimpleSuspendBinding>(ActivitySimpleSuspendBinding::inflate) {
    override fun initViewModel() {

    }

    override fun initView() {
    }

    override fun initData() {
//        cuntTime()
        cuntTime2()
    }

    private fun cuntTime2() = runBlocking<Unit> {
        try {
            failedConnectionFun()
        } catch (e: Exception) {
            println("cuntTime2 run with  exception ")
        }
    }


    fun cuntTime() = runBlocking<Unit> {
        val time = measureTimeMillis {
            val one = async { doSomethingUser1() }
            val two = async { doSomethingUser2() }

            println("match result : ${one.await() + two.await()}")
        }
        println("cuntTime : time = $time")
    }

    suspend fun doSomethingUser1(): Int {
        delay(1000L)
        return 13
    }

    suspend fun doSomethingUser2(): Int {
        delay(1000L)
        return 29
    }

    /**
     * 如果其中一个子协程（即 two）失败，第一个 async 以及等待中的父协程都会被取消：
     */
    suspend fun failedConnectionFun(): Int = coroutineScope {
        val one = async<Int> {
            try {
                delay(Long.MAX_VALUE)
                22
            } finally {
                println("failedCon: first async is cancelled")
            }
        }
        val two = async<Int> {
            println("failedCon: second async throw an exception")
            throw ArithmeticException()
        }
        one.await() + two.await()
    }
}
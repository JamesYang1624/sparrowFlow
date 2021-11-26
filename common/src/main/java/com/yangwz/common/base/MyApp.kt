package com.yangwz.common.base

import android.app.Application


/**
 * @author : JamesYang
 * @date : 2021/11/26 14:34
 * @GitHub : https://github.com/JamesYang1624
 */
 class MyApp :Application() {
    companion object{

     var instance: MyApp? = null
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getInstance(): MyApp? {
        return instance
    }
}
package com.yangwz.sparrowflow.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.yangwz.common.base.BaseActivity
import com.yangwz.sparrowflow.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


/**
 * @author : yangweizheng
 * @date : 2021/12/6 10:26
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun initViewModel() {

    }

    override fun initView() {

    }

    override fun initData() {
        justJump2Main()
//        countToMain()
    }

    private fun countToMain() {
        countDownCoroutines(2, onTick = {}, onFinish = {
           justJump2Main()
        })
    }

    private fun justJump2Main() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun countDownCoroutines(
        total: Int, onTick: (Int) -> Unit, onFinish: () -> Unit,
        scope: CoroutineScope = GlobalScope
    ): Job {
        return flow {
            for (i in total downTo 0) {
                emit(i)
                delay(1000)
            }
        }.flowOn(Dispatchers.Default)
            .onCompletion { onFinish.invoke() }
            .onEach { onTick.invoke(it) }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
    }
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        //修改背景为白色
//        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
//    }
}
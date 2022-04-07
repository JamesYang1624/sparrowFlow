package com.yangwz.sparrowflow.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.LogUtils
import com.yangwz.sparrowflow.R
import com.yangwz.sparrowflow.cover.SelCoverTimeActivity
import com.yangwz.sparrowflow.kotlinlearn.SimpleKotlinActivity
import com.yangwz.sparrowflow.kotlinlearn.SimpleSuspendActivity
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.auth.AuthService

import com.netease.nimlib.sdk.NIMClient

import com.netease.nimlib.sdk.RequestCallback
import com.yangwz.sparrowflow.app.ChatAccId
import com.yangwz.sparrowflow.app.ChatAccId.NimAccId
import com.yangwz.sparrowflow.app.ChatAccId.NimToken


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val greeting = findViewById<ComposeView>(R.id.greeting)
        greeting.setContent {
            MaterialTheme {
                PreGreeting()
            }
        }

    }

    @Preview
    @Composable
    fun PreGreeting() {
        Greeting()
    }

    @Composable
    fun Greeting() {
        Column {

            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, VideoCompressionActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
//            Image(
//                painter = rememberImagePainter(
//                    data = "https://i.loli.net/2021/11/18/vgQY3C2a7riLOKT.png"
//                ),
//                contentDescription = "Android Logo",
//                modifier = Modifier.size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(4.dp))
                Text("Button")
            }
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, BitmapFactoryActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
//            Image(
//                painter = rememberImagePainter(
//                    data = "https://i.loli.net/2021/11/18/vgQY3C2a7riLOKT.png"
//                ),
//                contentDescription = "Android Logo",
//                modifier = Modifier.size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(4.dp))
                Text("ImageBitmap")
            }

            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, SelCoverTimeActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("selectCover")
            }
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, TextBitmapActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("textac")
            }
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, WaterMarkActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("drawWaterMark")
            }
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, SimpleKotlinActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("SimpleKotlinActivity")
            }
            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, SimpleSuspendActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("SimpleSuspendActivity")
            }

            Button(
                onClick = {
                    val intent = Intent(this@MainActivity, NimChatActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text("云信")
            }
        }

        doLogin()

    }

    val TAG = "MainActivityTag"
    private fun doLogin() {
        val info = LoginInfo(NimAccId, NimToken)
        val callback: RequestCallback<LoginInfo> = object : RequestCallback<LoginInfo> {
            override fun onSuccess(param: LoginInfo?) {

                LogUtils.json(" $TAG NimDebug login success", param)
                // your code
            }

            override fun onFailed(code: Int) {
                if (code == 302) {
                    LogUtils.i(TAG, "NimDebug login onFailed $code 账号密码错误")
                    // your code
                } else {
                    // your code
                    LogUtils.i(TAG, "NimDebug login onFailed $code")
                }
            }

            override fun onException(exception: Throwable) {
                LogUtils.i(TAG, "NimDebug login onException ${exception.message}")
                // your code
            }
        }

        //执行手动登录

        //执行手动登录
        NIMClient.getService(AuthService::class.java).login(info).setCallback(callback)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //修改背景为白色
        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }
}

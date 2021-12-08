package com.yangwz.sparrowflow

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter


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
        Button(
            onClick = {
                val intent = Intent(this, VideoCompressionActivity::class.java)
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
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //修改背景为白色
        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }
}
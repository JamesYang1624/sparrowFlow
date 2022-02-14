package com.yangwz.sparrowflow.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yangwz.sparrowflow.widgets.ChoiceCover
import com.yangwz.sparrowflow.R


/**
 * @author : yangweizheng
 * @date : 2021/12/16 18:03
 */
class ChoiseCoverActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choise_cover)
        val chioseCover = findViewById<ChoiceCover>(R.id.choseCover)
        chioseCover.setOnScrollBorderListener(object : ChoiceCover.OnScrollBorderListener {
            override fun OnScrollBorder(start: Float, end: Float) {
            }

            override fun onScrollStateChange() {
            }
        })
    }

}
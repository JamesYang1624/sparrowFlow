package com.yangwz.sparrowflow.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import com.yangwz.sparrowflow.R


/**
 * @author : yangweizheng
 * @date : 2022/1/10 09:59
 */
class BitmapFactoryActivity :AppCompatActivity() {
    private val DELAY_TIME = 100L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_factory)
        val bottomView = findViewById<LinearLayout>(R.id.bottomView)
        val ivContainer = findViewById<ImageView>(R.id.ivContainer)
        getDrawingCache(bottomView,ivContainer)

    }

    //View转换为Bitmap
    fun getDrawingCache(sourceImageView: LinearLayout, destImageView: ImageView) {
        Handler().postDelayed(Runnable {
            // TODO Auto-generated method stub
            //开启bitmap缓存
            sourceImageView.isDrawingCacheEnabled = true
            //获取bitmap缓存
            val mBitmap = sourceImageView.drawingCache
            //显示 bitmap
            destImageView.setImageBitmap(mBitmap)

//                                Bitmap mBitmap = sourceImageView.getDrawingCache();
//                                Drawable drawable = (Drawable) new BitmapDrawable(mBitmap);
//                                destImageView.setImageDrawable(drawable);
            Handler().postDelayed(Runnable { // TODO Auto-generated method stub
                //不再显示bitmap缓存
                //destImageView.setImageBitmap(null);
//                destImageView.setImageResource(R.drawable.pet)

                //使用这句话而不是用上一句话是错误的，空指针调用
                //destImageView.setBackgroundDrawable(null);

                //关闭bitmap缓存
                sourceImageView.isDrawingCacheEnabled = false
                //释放bitmap缓存资源
                sourceImageView.destroyDrawingCache()
            }, DELAY_TIME)
        }, DELAY_TIME)
    }
}
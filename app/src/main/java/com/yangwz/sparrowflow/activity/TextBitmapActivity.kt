package com.yangwz.sparrowflow.activity

import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.blankj.utilcode.util.LogUtils
import com.yangwz.sparrowflow.R
import com.yangwz.sparrowflow.cover.ImageUtil


/**
 * @author : yangweizheng
 * @date : 2022/1/20 17:49
 */
class TextBitmapActivity : AppCompatActivity() {
    private var ivImage: ImageView? = null
    private var ivImage1: ImageView? = null
    private var ivImage2: ImageView? = null
    private val TAG = "TextBitmapActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
        ivImage = findViewById<ImageView>(R.id.ivImage)
        ivImage1 = findViewById<ImageView>(R.id.ivImage1)
        ivImage2 = findViewById<ImageView>(R.id.ivImage2)
        val createTextImage = createTextImage(200, 60, 16, "你是什么啊？")
        val textBitmap = getTextBitmap("你是什么啊？", 14)
        LogUtils.i(TAG, createTextImage)
        ivImage?.setImageBitmap(createTextImage)



        val file2Bitmap = ImageUtil.file2Bitmap("/storage/emulated/0/xunaidown/markshadowbg.png")
        ivImage1?.setImageBitmap(file2Bitmap)
//        val drawTextToCenter =
//            ImageUtil.drawTextToCenter(this, file2Bitmap, "你的名字", 16, Color.WHITE)
//        ivImage2?.setImageBitmap(drawTextToCenter)


    }

    /**
     * 创建指定大小的包含文字的图片，背景为透明
     * @param width      图片宽度
     * @param height     图片高度
     * @param txtSize    文字字号
     * @param innerTxt   内容文字
     * @return
     */
    private fun createTextImage(width: Int, height: Int, txtSize: Int, innerTxt: String): Bitmap? {
        //若使背景为透明，必须设置为Bitmap.Config.ARGB_4444
        val bitmap = Bitmap.createBitmap(100, 20, Bitmap.Config.ARGB_4444)
        val back = Bitmap.createBitmap(
            if (bitmap.width % txtSize == 0) bitmap.width else (bitmap.width / txtSize + 1) * txtSize,
            if (bitmap.height % txtSize == 0) bitmap.height else (bitmap.height / txtSize + 1) * txtSize,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(back)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.textSize = txtSize.toFloat()

        //计算得出文字的绘制起始x、y坐标
        val posX = width / 2 - txtSize * innerTxt.length / 2
//        val posY = height / 2 - txtSize / 2
        val posY = height
        canvas.drawText(innerTxt, posX.toFloat(), posY.toFloat(), paint)
        return back
    }

    /**
     * 核心，文本转成图片
     * @param bitmap 原图片
     * @param text 文本
     * @param fontSize 文字大小
     * @return 转好的图片
     */
    fun getTextBitmap( text: String, fontSize: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(100, 20, Bitmap.Config.ARGB_4444)
        requireNotNull(bitmap) { "Bitmap cannot be null." }
        val picWidth = bitmap.width
        val picHeight = bitmap.height
        val back = Bitmap.createBitmap(
            if (bitmap.width % fontSize == 0) bitmap.width else (bitmap.width / fontSize + 1) * fontSize,
            if (bitmap.height % fontSize == 0) bitmap.height else (bitmap.height / fontSize + 1) * fontSize,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(back)
        canvas.drawColor(0xfff)
        var idx = 0
        var y = 0
        while (y < picHeight) {
            var x = 0
            while (x < picWidth) {
                val colors = getPixels(bitmap, x, y, fontSize, fontSize)
                val paint = Paint()
                paint.isAntiAlias = true
                paint.color = getAverage(colors)
                paint.textSize = fontSize.toFloat()
                val fontMetrics = paint.fontMetrics
                val padding =
                    if (y == 0) fontSize + fontMetrics.ascent else (fontSize + fontMetrics.ascent) * 2

                if (idx == text.length) {
                    idx = 0
                }
                x += fontSize
            }
            y += fontSize
        }
        return back
    }

    /**
     * 获取某一块的所有像素的颜色
     * @param bitmap
     * @param x
     * @param y
     * @param w
     * @param h
     * @return 颜色数组
     */
    private fun getPixels(bitmap: Bitmap, x: Int, y: Int, w: Int, h: Int): IntArray {
        val colors = IntArray(w * h)
        var idx = 0
        var i = y
        while (i < h + y && i < bitmap.height) {
            var j = x
            while (j < w + x && j < bitmap.width) {
                val color = bitmap.getPixel(j, i)
                colors[idx++] = color
                j++
            }
            i++
        }
        return colors
    }

    /**
     * 求多个颜色的平均值
     * @param colors
     * @return 平均颜色
     */
    private fun getAverage(colors: IntArray): Int {
        //int alpha=0;
        var red = 0
        var green = 0
        var blue = 0
        for (color in colors) {
            red += color and 0xff0000 shr 16
            green += color and 0xff00 shr 8
            blue += color and 0x0000ff
        }
        val len = colors.size.toFloat()
        //alpha=Math.round(alpha/len);
        red = Math.round(red / len)
        green = Math.round(green / len)
        blue = Math.round(blue / len)
        return Color.argb(0xff, red, green, blue)
    }

    private fun log(log: String) {
        println("-------->Utils:$log")
    }
}
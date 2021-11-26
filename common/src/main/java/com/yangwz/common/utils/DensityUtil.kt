package com.yangwz.common.utils

import android.content.Context
import com.yangwz.common.utils.DensityUtil
import android.view.WindowManager
import android.view.Display
import android.util.DisplayMetrics
import java.lang.Exception

/**
 * Used 尺寸转换工具类（全）
 */
object DensityUtil {
    private const val TAG = "DensityUtil"
    var RATIO = 0.95f //缩放比例值

    /**
     * px 转 dp【按照一定的比例】
     */
    fun px2dipRatio(context: Context, pxValue: Float): Int {
        val scale = getScreenDendity(context) * RATIO
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * dp转px【按照一定的比例】
     */
    fun dip2pxRatio(context: Context, dpValue: Float): Int {
        val scale = getScreenDendity(context) * RATIO
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px 转 dp
     * 48px - 16dp
     * 50px - 17dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = getScreenDendity(context)
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * dp转px
     * 16dp - 48px
     * 17dp - 51px
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = getScreenDendity(context)
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 获取屏幕的宽度（像素）
     */
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels //1080
    }

    /**
     * 获取屏幕的宽度（dp）
     */
    fun getScreenWidthDp(context: Context): Int {
        val scale = getScreenDendity(context)
        return (context.resources.displayMetrics.widthPixels / scale).toInt() //360
    }

    /**
     * 获取屏幕的高度（像素） 不包含虚拟导航栏的高度(系统虚拟按键)
     */
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels //1776
    }

    /**
     * 获取屏幕的高度（像素）
     */
    fun getScreenHeightDp(context: Context): Int {
        val scale = getScreenDendity(context)
        return (context.resources.displayMetrics.heightPixels / scale).toInt() //592
    }

    /**
     * 屏幕密度比例
     */
    fun getScreenDendity(context: Context): Float {
        return context.resources.displayMetrics.density //3
    }

    /**
     * 获取系统虚拟导航栏的高度
     *
     * @param context
     * @return
     */
    fun getVirtualBarHeight(context: Context): Int {
        var vh = 0
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        try {
            val c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            vh = dm.heightPixels - display.height
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vh
    }

    /**
     * 获取状态栏的高度 72px
     * http://www.2cto.com/kf/201501/374049.html
     */
    fun getStatusBarHeight(context: Context): Int {
        var statusHeight = -1
        try {
            val aClass = Class.forName("com.android.internal.R\$dimen")
            val `object` = aClass.newInstance()
            val height = aClass.getField("status_bar_height")[`object`].toString().toInt()
            statusHeight = context.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusHeight

        //依赖于WMS(窗口管理服务的回调)【不建议使用】
        /*Rect outRect = new Rect();
        ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        return outRect.top;*/
    }

    /**
     * 指定机型（displayMetrics.xdpi）下dp转px
     * 18dp - 50px
     */
    fun dpToPx(context: Context, dp: Int): Int {
        return Math.round(dp.toFloat() * getPixelScaleFactor(context))
    }

    /**
     * 指定机型（displayMetrics.xdpi）下px 转 dp
     * 50px - 18dp
     */
    fun pxToDp(context: Context, px: Int): Int {
        return Math.round(px.toFloat() / getPixelScaleFactor(context))
    }

    /**
     * 获取水平方向的dpi的密度比例值
     * 2.7653186
     */
    fun getPixelScaleFactor(context: Context): Float {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.xdpi / 160.0f
    }
}
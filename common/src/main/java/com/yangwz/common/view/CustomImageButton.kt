package com.yangwz.common.view

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import com.yangwz.common.R
import android.graphics.drawable.Drawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.ShapeDrawable.ShaderFactory
import android.graphics.Shader
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

/**
 * @author : yangweizheng
 * @date : 2021/12/10 10:29
 */
class CustomImageButton : AppCompatImageButton {
    private var ctx: Context
    private var bgColorPressed = 0
    private var bgColor = 0

    constructor(context: Context) : super(context) {
        ctx = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        ctx = context
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        ctx = context
        init(attrs)
    }

    private fun init(attrSet: AttributeSet) {
        val theme = ctx.theme
        val arr = theme.obtainStyledAttributes(attrSet, R.styleable.FAB, 0, 0)
        try {
            setBgColor(arr.getColor(R.styleable.FAB_bg_color, Color.BLUE))
            setBgColorPressed(arr.getColor(R.styleable.FAB_bg_color_pressed, Color.GRAY))
            val sld = StateListDrawable()
            sld.addState(intArrayOf(android.R.attr.state_pressed), createButton(bgColorPressed))
            sld.addState(intArrayOf(), createButton(bgColor))
            background = sld
        } catch (t: Throwable) {
        } finally {
            arr.recycle()
        }
    }

    private fun setBgColorPressed(color: Int) {
        bgColorPressed = color
    }

    private fun setBgColor(color: Int) {
        bgColor = color
    }

    private fun createButton(color: Int): Drawable {
        val oShape = OvalShape()
        val sd = ShapeDrawable(oShape)
        setWillNotDraw(false)
        sd.paint.color = color
        val oShape1 = OvalShape()
        val sd1 = ShapeDrawable(oShape)
        sd1.shaderFactory = object : ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    0f, 0f, 0f, height.toFloat(), intArrayOf(
                        Color.WHITE,
                        Color.GRAY,
                        Color.DKGRAY,
                        Color.BLACK
                    ), null, Shader.TileMode.REPEAT)
            }
        }
        val ld = LayerDrawable(arrayOf<Drawable>(sd1, sd))
        ld.setLayerInset(0, 5, 5, 0, 0)
        ld.setLayerInset(1, 0, 0, 5, 5)
        return ld
    }
}
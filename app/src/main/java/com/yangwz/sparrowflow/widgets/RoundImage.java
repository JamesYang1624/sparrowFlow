package com.yangwz.sparrowflow.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.yangwz.sparrowflow.R;

import androidx.appcompat.widget.AppCompatImageView;

public class RoundImage extends AppCompatImageView {
    private float mRadius = 10f;
    private float[] radiusArray = {10f, 10f, 10f, 10f, 0f, 0f, 0f, 0f};

    public RoundImage(Context context) {
        super(context);
    }

    public RoundImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImage);
        mRadius = typedArray.getDimension(R.styleable.RoundImage_ri_radio, 0);
        setRadius(mRadius);
    }

    /**
     * 设置四个角的圆角半径
     */
    public void setRadius(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        radiusArray[0] = leftTop;
        radiusArray[1] = leftTop;
        radiusArray[2] = rightTop;
        radiusArray[3] = rightTop;
        radiusArray[4] = rightBottom;
        radiusArray[5] = rightBottom;
        radiusArray[6] = leftBottom;
        radiusArray[7] = leftBottom;

        invalidate();
    }

    public void setRadius(float radius) {
        radiusArray[0] = radius;
        radiusArray[1] = radius;
        radiusArray[2] = radius;
        radiusArray[3] = radius;
        radiusArray[4] = radius;
        radiusArray[5] = radius;
        radiusArray[6] = radius;
        radiusArray[7] = radius;

        invalidate();
    }


    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        float left = getPaddingLeft();
        float top = getPaddingTop();

        path.addRoundRect(new RectF(left, top, getWidth(), getHeight()), radiusArray,
                Path.Direction.CW);
        canvas.clipPath(path);

        super.onDraw(canvas);
    }

}
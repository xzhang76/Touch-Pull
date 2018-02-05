package com.htsc.touchpull.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zhangxiaoting on 2018/2/2.
 */

public class TouchPullView extends View {

    private static final String TAG = "TouchPullView";

    private Paint mPaint; // 绘制圆的画笔
    private int mRadius = 100; // 圆半径
    private float mCircleX, mCircleY; // 圆心坐标
    private float mTouchMoveProgress; // 移动距离

    public TouchPullView(Context context) {
        super(context);
        initView();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true); // 防抖动
        mPaint.setStyle(Paint.Style.FILL); // 填充圆
        mPaint.setColor(0xff000000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);

    }

    // 控件宽高变化时调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCircleX = getWidth() / 2;
        mCircleY = getHeight() / 2;
        Log.e(TAG, "circle center: mCircleX = " + mCircleX + ", mCircleY = " + mCircleY);
    }

    // 控件对父布局对控件进行测量时调用
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 最小宽度
        int minWith = 2 * mRadius + getPaddingLeft() + getPaddingRight();
        // 最终宽度
        int measuredWidth;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 最小高度 *800表示要比实际拉动的高度大一点
        // mTouchMoveProgress = moveDistance / TOUCH_MOVE_MAX_Y
        int minHeight = (int)(800 * mTouchMoveProgress + 0.5f) + getPaddingBottom() + getPaddingTop();
        // 最终高度
        int measuredHeight;

        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = viewWidth;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // 最大模式的话,取最小值和指定值的最小者
            measuredWidth = Math.min(minWith, viewWidth);
        } else {
            // UNSPECIFIED 没有指定的话就用最小的
            measuredWidth = minWith;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = viewHeight;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // 最大模式的话,取最小值和指定值的最小者
            measuredHeight = Math.min(minHeight, viewHeight);
        } else {
            // UNSPECIFIED 没有指定的话就用最小的
            measuredHeight = minHeight;
        }
        Log.e(TAG, "measuredWidth = " + measuredWidth + ", measuredHeight = " + measuredHeight);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    // 按照下拉时根据进度改变view
    public void setProgressChanged(float progress) {
        Log.e(TAG, "progress: " + progress);
        mTouchMoveProgress = progress;
        // 请求重新测量
        requestLayout();
    }
}

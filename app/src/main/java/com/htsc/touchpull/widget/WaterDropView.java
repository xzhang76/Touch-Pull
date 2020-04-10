package com.htsc.touchpull.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * 水滴粘性效果的view
 * <p>
 * Created by zhangxiaoting on 2018/2/8.
 */

public class WaterDropView extends View {

    private final static String TAG = "WaterDropView";
    // 画起始圆的画笔
    private Paint mStartCirclePaint;
    // 画贝塞尔曲线以及外圆的画笔
    private Paint mBezierCirclePaint;
    // 贝塞尔曲线的路径
    private Path mBezierPath = new Path();
    // 起始圆的半径(可配置)
    private int mStartCircleRadius = 60;
    // 起始圆的圆心位置
    private float mStartCirclePointX;
    private float mStartCirclePointY;
    // 外圆半径(可配置)
    private int mEndCircleRadius = 80;
    // 外圆的圆心位置
    private float mEndCirclePointX;
    private float mEndCirclePointY;

    // 外圆圆心到起始圆圆心的距离
    private float mEndToStartCirclePointDistance;

    // 释放时的动画
    private ValueAnimator mReleaseValueAnim;
    // 拉动的插值器
    private DecelerateInterpolator mProgressInterpolator = new DecelerateInterpolator();
    // 正在拖拽
    private boolean mBeingDragged = false;


    public WaterDropView(Context context) {
        this(context, null);
    }

    public WaterDropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true); // 防抖动
        paint.setStyle(Paint.Style.FILL); // 填充圆
        paint.setColor(0xf4444444);
        mStartCirclePaint = paint;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true); // 防抖动
        paint.setStyle(Paint.Style.FILL); // 填充曲线
        paint.setColor(Color.GRAY);
        mBezierCirclePaint = paint;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mStartCirclePointX, mStartCirclePointY, mStartCircleRadius, mStartCirclePaint);
        canvas.drawCircle(mEndCirclePointX, mEndCirclePointY, mEndCircleRadius, mBezierCirclePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mStartCirclePointX = getWidth() / 2;
        mStartCirclePointY = getHeight() / 2;
    }

    private void updateMainLayout() {

    }

    /**
     * 判断按下的点是不是在该view上
     *
     * @param touchX 触摸点x
     * @param touchY 触摸点y
     * @return
     */
    public boolean isTouchOnMe(float touchX, float touchY) {
        float xDistance = Math.abs(touchX - mStartCirclePointX);
        float yDistance = Math.abs(touchY - mStartCirclePointY);
        return (Math.sqrt(xDistance * xDistance + yDistance * yDistance) - mStartCircleRadius) < 0;
    }

    // 拖动开始
    public void onTouchDragged(float targetX, float targetY) {
        mEndCirclePointX = targetX;
        mEndCirclePointY = targetY;
        float xDistance = Math.abs(mEndCirclePointX - mStartCirclePointX);
        float yDistance = Math.abs(mEndCirclePointY - mStartCirclePointY);
        mEndToStartCirclePointDistance = (float) Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        mBeingDragged = true;
        postInvalidate();
    }

    // 拖动之后释放
    public void onDragReleased() {
        mBeingDragged = false;
        if (null == mReleaseValueAnim) {
            mReleaseValueAnim = ValueAnimator.ofFloat(mEndToStartCirclePointDistance, 0f);
            mReleaseValueAnim.setInterpolator(mProgressInterpolator);
            mReleaseValueAnim.setDuration(400);
            mReleaseValueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue instanceof Float) {
//                        setProgressChanged((Float) animatedValue);
                    }
                }
            });
        } else {
            mReleaseValueAnim.cancel();
            mReleaseValueAnim.setFloatValues(mEndToStartCirclePointDistance, 0f);
        }
        mReleaseValueAnim.start();

    }
}

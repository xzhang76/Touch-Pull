package com.htsc.touchpull.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.htsc.touchpull.R;

import static com.htsc.touchpull.MainActivity.TOUCH_MOVE_MAX_Y;

/**
 * 粘性拉动效果
 * Created by zhangxiaoting on 2018/2/2.
 */

public class TouchPullView extends View {

    private static final String TAG = "TouchPullView";

    private Paint mCirclePaint; // 绘制圆的画笔
    private int mCircleRadius = 80; // 外圆半径
    private int mCirCleMargin = 0; // 内外圆的margin
    private float mCirclePointX, mCirclePointY; // 圆心坐标
    private float mTouchMoveProgress; // 移动距离

    // 目标宽度 决定起点坐标的x
    private float mTargetWidth;
    // 控制点最终高度 决定控制点的y
    private float mControlTargetY = 4.0f;
    // 贝塞尔曲线路径和画笔
    private Path mBezierPath = new Path();
    private Paint mBezierPaint;
    // 角度的变换 0~120度
    private int mTargetAngle = 120;
    // 释放时的动画
    private ValueAnimator mReleaseValueAnim;
    // 拉动的插值器
    private DecelerateInterpolator mProgressInterpolator = new DecelerateInterpolator();

    public TouchPullView(Context context) {
        this(context, null);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TouchPullView);
        mCircleRadius = (int) typedArray.getDimension(R.styleable.TouchPullView_circleRadius, mCircleRadius);
        mControlTargetY = typedArray.getDimension(R.styleable.TouchPullView_controlTargetY, mControlTargetY);
        mTargetAngle = (int) typedArray.getDimension(R.styleable.TouchPullView_targetAngle, mTargetAngle);
        int circlePathColor = typedArray.getColor(R.styleable.TouchPullView_circlePathColor, Color.DKGRAY);
        int bezierPathColor = typedArray.getColor(R.styleable.TouchPullView_bezierPathColor, Color.GRAY);
        mCirCleMargin = (int) typedArray.getDimension(R.styleable.TouchPullView_circleMargin, mCirCleMargin);
        typedArray.recycle();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true); // 防抖动
        paint.setStyle(Paint.Style.FILL); // 填充圆
        paint.setColor(circlePathColor);
        mCirclePaint = paint;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true); // 防抖动
        paint.setStyle(Paint.Style.FILL); // 填充曲线
        paint.setColor(bezierPathColor);
        mBezierPaint = paint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画bezier曲线
        int save = canvas.save();
        canvas.drawPath(mBezierPath, mBezierPaint);
        // 画圆
        canvas.drawCircle(mCirclePointX, mCirclePointY, mCircleRadius, mBezierPaint);
        // 画内圆
        canvas.drawCircle(mCirclePointX, mCirclePointY, mCircleRadius - mCirCleMargin, mCirclePaint);
        canvas.restoreToCount(save);

    }

    // 控件宽高变化时调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 当高度变化时进行路径更新
        updatePathLayout();
    }

    // 控件对父布局对控件进行测量时调用
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 最小宽度
        int minWith = 2 * mCircleRadius + getPaddingLeft() + getPaddingRight();
        // 最终宽度
        int measuredWidth;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 最小高度 *800表示要比实际拉动的高度大一点
        // mTouchMoveProgress = moveDistance / TOUCH_MOVE_MAX_Y, TOUCH_MOVE_MAX_Y = 600
        int minHeight = (int) (TOUCH_MOVE_MAX_Y * mTouchMoveProgress + 0.5f) + getPaddingBottom() + getPaddingTop();
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

    // 控件尺寸变化时进行路径更新
    private void updatePathLayout() {

        float moveProgress = mProgressInterpolator.getInterpolation(mTouchMoveProgress);

        // 重置线 这个非常重要
        mBezierPath.reset();
        mTargetWidth = getWidth() / 3;
        // 获取可绘制区域宽度和高度
        float height = getValueByLine(0, TOUCH_MOVE_MAX_Y, mTouchMoveProgress);
        // 圆心坐标

        // 更新圆心的坐标
        mCirclePointX = getWidth() / 2;
        mCirclePointY = height - mCircleRadius;

        // 获取当前切线的弧度
        double radian = Math.toRadians(getValueByLine(0, mTargetAngle, moveProgress));

        // 起点坐标
        float lStartX = getValueByLine(0, mTargetWidth, moveProgress);

        // 终点坐标
        float lEndX = mCirclePointX - (float) (Math.sin(radian) * mCircleRadius);
        float lEndY = mCirclePointY + (float) (Math.cos(radian) * mCircleRadius);

        // 控制点坐标
        float lControlY = getValueByLine(0, mControlTargetY, moveProgress);
        float lControl2EndY = lEndY - lControlY;
        float lControlX = lEndX - (float) (lControl2EndY / Math.tan(radian));

        Log.e(TAG, "lStartX = " + lStartX + ", mCirclePointX = " + mCirclePointX + ", mCirclePointY = " + mCirclePointY);
        Log.e(TAG, "radian = " + radian + ", lEndX = " + lEndX + ", lEndY = " + lEndY + ", lControlX = " + lControlX + ", lControlY = " + lControlY);
        // 生成左侧贝塞尔曲线
        mBezierPath.moveTo(lStartX, 0);
        mBezierPath.quadTo(lControlX, lControlY, lEndX, lEndY);
        // 连接右侧贝塞尔曲线
        mBezierPath.lineTo(mCirclePointX + (mCirclePointX - lEndX), lEndY);
        mBezierPath.quadTo(mCirclePointX + (mCirclePointX - lControlX), lControlY, mCirclePointX + (mCirclePointX - lStartX), 0);

    }

    // 获取当前值
    private float getValueByLine(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    public void release() {
        if (null == mReleaseValueAnim) {
            mReleaseValueAnim = ValueAnimator.ofFloat(mTouchMoveProgress, 0f);
            mReleaseValueAnim.setInterpolator(mProgressInterpolator);
            mReleaseValueAnim.setDuration(400);
            mReleaseValueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue instanceof Float) {
                        setProgressChanged((Float) animatedValue);
                    }
                }
            });
        } else {
            mReleaseValueAnim.cancel();
            mReleaseValueAnim.setFloatValues(mTouchMoveProgress, 0f);
        }
        mReleaseValueAnim.start();
    }
}

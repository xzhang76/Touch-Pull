package com.htsc.touchpull.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zhangxiaoting on 2018/2/6.
 */

public class BezierView extends View {

    private final static String TAG = "BezierView";

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private float mMoveProgress;

    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // 这里有个小技巧
        // 如果全局变量使用超过三次
        // 最好使用局部变量复制的方式使用它
        Paint paint = mPaint;
        paint.setColor(0xff000000);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);

        // 一阶贝塞尔曲线
        Path path = mPath;
        path.moveTo(100, 100);
        path.lineTo(300, 300);

        // 二阶贝塞尔曲线(绝对坐标0,0开始计算)
        // path.quadTo(500, 100, 700, 300);
        // 相对的实现,相对坐标,视起始点为0,0。与上面等效,推荐这种
        path.rQuadTo(200, -200, 400, 0);

        // 三阶贝塞尔曲线
        path.moveTo(200, 800);
        // path.cubicTo(400 , 700, 500, 1000, 800, 800);
        // 相对实现
        path.rCubicTo(200, -300, 300, 200, 600, 0);

        // 四阶贝塞尔曲线
        mPath.moveTo(0, 0);
    }

    private void init4Bezier() {
        // (0, 0) (300, 300) (200, 700) (500, 500) (700, 1200)
        float[] xPoints = new float[]{0, 300, 200, 0, 700};
        float[] yPoints = new float[]{0, 300, 700, 1500, 0};
        float bezierXPoint = calculateBezier(mMoveProgress, xPoints);
        float bezierYPoint = calculateBezier(mMoveProgress, yPoints);
        Log.e(TAG, "bezierXPoint = " + bezierXPoint + ", bezierYPoint = " + bezierYPoint);
        mPath.lineTo(bezierXPoint, bezierYPoint);
    }

    /**
     * 根据t计算该时刻贝塞尔所处的值(x或y)
     *
     * @param t      时间(0~1)
     * @param values 贝塞尔点集合(x或y)
     * @return 当前t时刻所处点
     */
    private float calculateBezier(float t, float... values) {
        final int pointsCount = values.length;
        // 这里需要理解贝塞尔曲线的原理
        // 从最外层开始 次层的点是最外层相邻两点之间通过如下公式计算得来
        // bezier(t) = p0 + (p1 - p0) * t;
        // 也就是p0点到p1和p0之间乘以t这段的距离
        // 根据这个原理依次往下运算
        for (int i = pointsCount - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                values[j] = values[j] + (values[j+1] - values[j]) * t;
            }
        }

        return values[0];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    public void setProgressChanged(float progress) {
        mMoveProgress = progress;
        init4Bezier();
        postInvalidate();
    }
}

package com.htsc.touchpull;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.htsc.touchpull.widget.WaterDropView;

/**
 * 水滴拖动效果的activity
 *
 * Created by zhangxiaoting on 2018/2/8.
 */

public class WaterDropActivity extends AppCompatActivity implements View.OnTouchListener{

    private WaterDropView mWaterDropView;
    // touch开始的点坐标 用于判断点是不是放在起始圆上
    private float mTouchStartX;
    private float mTouchStartY;
    private boolean mIsTouchOnMe = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_drop);
        mWaterDropView = findViewById(R.id.water_drop_view);
        findViewById(R.id.second_main_view).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = motionEvent.getX();
                mTouchStartY = motionEvent.getY();
                mIsTouchOnMe = mWaterDropView.isTouchOnMe(mTouchStartX, mTouchStartY);
                return mIsTouchOnMe; // 消费掉
            case MotionEvent.ACTION_MOVE:
                float touchX = motionEvent.getX();
                float touchY = motionEvent.getY();
                if (mIsTouchOnMe) {
                    // 起始点放在了起始圆上 想要拖动
                    if (null != mWaterDropView) {
                        mWaterDropView.onTouchDragged(touchX, touchY);
                    }
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                mIsTouchOnMe = false;
                if (null != mWaterDropView) {
                    mWaterDropView.onDragReleased();
                }
                break;
            default:
                break;
        }
        return false;
    }
}

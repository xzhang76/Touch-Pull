package com.htsc.touchpull;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.htsc.touchpull.widget.BezierView;
import com.htsc.touchpull.widget.TouchPullView;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private float mTouchStartY = 0; // 开始下拉的点
    public static final float TOUCH_MOVE_MAX_Y = 350; // 下拉的最大Y值

    private TouchPullView mTouchPullView;
    private BezierView mBezierView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_view).setOnTouchListener(this);
        mTouchPullView = findViewById(R.id.touch_pull_view);
        mBezierView = findViewById(R.id.bezier_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.water_drop_activity:
                Intent intent = new Intent(this, WaterDropActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartY = motionEvent.getY();
                return true; // 消费掉
            case MotionEvent.ACTION_MOVE:
                float moveY = motionEvent.getY();
                if (moveY > mTouchStartY) {
                    //开始下拉
                    float moveDistance = moveY - mTouchStartY;
                    float progress = moveDistance >= TOUCH_MOVE_MAX_Y ?
                            1.0f : moveDistance / TOUCH_MOVE_MAX_Y;
                    if (null != mTouchPullView) {
                        mTouchPullView.setProgressChanged(progress);
                    }
                    if (null != mBezierView) {
                        mBezierView.setProgressChanged(progress);
                    }
                }
                return true; // 消费掉
            case MotionEvent.ACTION_UP:
                if (null != mTouchPullView) {
                    mTouchPullView.release();
                }
                break;
            default:
                break;
        }
        return false;
    }


}

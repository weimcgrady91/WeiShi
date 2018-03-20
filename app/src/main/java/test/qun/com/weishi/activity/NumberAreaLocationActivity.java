package test.qun.com.weishi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import test.qun.com.weishi.App;
import test.qun.com.weishi.R;
import test.qun.com.weishi.util.PreferencesUtil;

public class NumberAreaLocationActivity extends Activity {

    private Button mBtnTop;
    private Button mBtnBottom;
    private ImageView mImgDrag;
    private DisplayMetrics mDisplayMetrics;
    private View mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_area_location);
        initViews();
    }


    private void initViews() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        mBtnTop = findViewById(R.id.btn_top);
        mBtnBottom = findViewById(R.id.btn_bottom);
        mImgDrag = findViewById(R.id.img_drag);
        mRoot = findViewById(R.id.rl_root);

        int left = (int) PreferencesUtil.getData(App.sContext, "location_x", 0);
        int top = (int) PreferencesUtil.getData(App.sContext, "location_y", 0);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = left;
        lp.topMargin = top;
        mImgDrag.setLayoutParams(lp);

        if (top > mDisplayMetrics.heightPixels / 2) {
            mBtnTop.setVisibility(View.VISIBLE);
            mBtnBottom.setVisibility(View.INVISIBLE);
        } else {
            mBtnTop.setVisibility(View.INVISIBLE);
            mBtnBottom.setVisibility(View.VISIBLE);
        }

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.e("weiqun12345", "onDoubleTap");
                int left = mDisplayMetrics.widthPixels / 2 - mImgDrag.getWidth() / 2;
                int top = mDisplayMetrics.heightPixels / 2 - mImgDrag.getHeight() / 2;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.leftMargin = left;
                lp.topMargin = top;
                mImgDrag.setLayoutParams(lp);
                PreferencesUtil.setData(App.sContext, "location_x", mImgDrag.getLeft());
                PreferencesUtil.setData(App.sContext, "location_y", mImgDrag.getTop());
                return super.onDoubleTap(e);
            }
        });

        mImgDrag.setOnTouchListener(new View.OnTouchListener() {
            int lastX = 0;
            int lastY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        Log.e("weiqun12345", "lastX=" + lastX);
                        Log.e("weiqun12345", "lastY=" + lastY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int cX = (int) event.getRawX();
                        int cY = (int) event.getRawY();
                        Log.e("weiqun12345", "cX=" + cX);
                        Log.e("weiqun12345", "cY=" + cY);
                        int xDis = cX - lastX;
                        int yDis = cY - lastY;


                        Log.e("weiqun12345", "xDis=" + xDis);
                        Log.e("weiqun12345", "yDis=" + yDis);
                        int l = mImgDrag.getLeft() + xDis;
                        int t = mImgDrag.getTop() + yDis;
                        int r = mImgDrag.getRight() + xDis;
                        int b = mImgDrag.getBottom() + yDis;
                        Log.e("weiqun12345", "l=" + l + ",t=" + t + ",r=" + r + ",b=" + b);

                        if (l < mRoot.getTop()) {
                            l = mRoot.getTop();
                            r = l + mImgDrag.getWidth();
                        }
                        if (t < mRoot.getLeft()) {
                            t = mRoot.getLeft();
                            b = t + mImgDrag.getHeight();
                        }
                        if (r > mRoot.getWidth()) {
                            l = mRoot.getWidth() - mImgDrag.getWidth();
                            r = l + mImgDrag.getWidth();
                        }
                        if (b > mRoot.getHeight()) {
                            b = mRoot.getHeight();
                            t = b - mImgDrag.getHeight();
                        }
                        mImgDrag.layout(l, t, r, b);

                        lastX = cX;
                        lastY = cY;

                        if (mImgDrag.getTop() > mRoot.getHeight() / 2) {
                            mBtnTop.setVisibility(View.VISIBLE);
                            mBtnBottom.setVisibility(View.INVISIBLE);
                        } else {
                            mBtnTop.setVisibility(View.INVISIBLE);
                            mBtnBottom.setVisibility(View.VISIBLE);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        PreferencesUtil.setData(App.sContext, "location_x", mImgDrag.getLeft());
                        PreferencesUtil.setData(App.sContext, "location_y", mImgDrag.getTop());
                        break;
                }
                return true;
            }
        });
    }
}

package test.qun.com.weishi.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import test.qun.com.weishi.R;
import test.qun.com.weishi.activity.RocketActivity;

public class RocketService extends Service {
    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    private WindowManager mWm;
    private View mRocketView;
    private DisplayMetrics mDisplayMetrics;

    public RocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplayMetrics = new DisplayMetrics();
        mWm.getDefaultDisplay().getMetrics(mDisplayMetrics);


        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        //将吐司放置在左上角显示
        params.gravity = Gravity.TOP + Gravity.LEFT;
        params.setTitle("Toast");

        mRocketView = View.inflate(this, R.layout.view_rocket, null);

        ImageView imgRocket = mRocketView.findViewById(R.id.iv_rocket);
        AnimationDrawable animationDrawable = (AnimationDrawable) imgRocket.getBackground();
        animationDrawable.start();


        mRocketView.setOnTouchListener(new View.OnTouchListener() {
            int lastX;
            int lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int cX = (int) event.getRawX();
                        int cY = (int) event.getRawY();
                        int xDis = cX - lastX;
                        int yDis = cY - lastY;
                        params.x = params.x + xDis;
                        params.y = params.y + yDis;
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mDisplayMetrics.widthPixels - mRocketView.getWidth()) {
                            params.x = mDisplayMetrics.widthPixels - mRocketView.getWidth();
                        }
                        if (params.y > mDisplayMetrics.heightPixels - mRocketView.getHeight()) {
                            params.y = mDisplayMetrics.heightPixels - mRocketView.getHeight();
                        }
                        mWm.updateViewLayout(mRocketView, params);
                        lastX = cX;
                        lastY = cY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.y > 350 && params.x > mDisplayMetrics.widthPixels / 2 - 300 && params.x < mDisplayMetrics.widthPixels / 2 + 300) {
                            sendRocket();
                            Intent intent = new Intent(RocketService.this, RocketActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });
        mWm.addView(mRocketView, params);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            params.y = (Integer) msg.obj;
            //更新到火箭上(窗体)
            mWm.updateViewLayout(mRocketView, params);
        }
    };

    private void sendRocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 11; i++) {
                    int y = 350 - i * 35;
                    //睡眠
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //通过消息机制,将y轴坐标作为主线程火箭竖直方向上的显示位置
                    Message msg = Message.obtain();
                    msg.obj = y;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRocketView != null && mWm != null) {
            mWm.removeView(mRocketView);
        }
    }
}

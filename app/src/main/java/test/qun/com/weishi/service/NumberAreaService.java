package test.qun.com.weishi.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import test.qun.com.weishi.App;
import test.qun.com.weishi.R;
import test.qun.com.weishi.engine.NumberAreaEngine;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.PreferencesUtil;

public class NumberAreaService extends Service {

    private TelephonyManager mTm;
    private WindowManager mWm;
    private View mView;
    private TextView mTvNumberArea;

    public NumberAreaService() {
    }

    private PhoneStateListener mPhoneStateListener;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneStateListener = new PhoneStateListener();
        mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        LogUtil.i("NumberAreaService onCreate");
        mWm = (WindowManager) getSystemService(WINDOW_SERVICE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("NumberAreaService onDestroy");
        if (mPhoneStateListener != null && mTm != null) {
            mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private class PhoneStateListener extends android.telephony.PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    LogUtil.i("CALL_STATE_IDLE");
                    removeToast();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    LogUtil.i("CALL_STATE_OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    showToast(incomingNumber);
                    LogUtil.i("CALL_STATE_RINGING");
                    break;
            }
        }
    }

    private void removeToast() {
        if (mWm != null && mView != null) {
            mWm.removeView(mView);
            mView = null;
        }
    }

    private int[] backgrounds = {R.drawable.call_locate_blue, R.drawable.call_locate_gray
            , R.drawable.call_locate_green, R.drawable.call_locate_orange, R.drawable.call_locate_white};

    private void showToast(String incommingNumber) {
        Toast.makeText(this, incommingNumber + "", Toast.LENGTH_LONG).show();
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        int left = (int) PreferencesUtil.getData(App.sContext,"location_x",0);
        int top = (int) PreferencesUtil.getData(App.sContext,"location_y",0);
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.x = left;
        params.y = top;
        mView = View.inflate(this, R.layout.number_area_toast, null);
        mTvNumberArea = mView.findViewById(R.id.tv_number_area);

        String value = (String) PreferencesUtil.getData(App.sContext, "lp_number_area_style", "0");
        mTvNumberArea.setBackgroundResource(backgrounds[Integer.parseInt(value)]);

        mWm.addView(mView, params);
        queryNumberArea(incommingNumber);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String address = (String) msg.obj;
                mTvNumberArea.setText(address);
            }
        }
    };

    private void queryNumberArea(final String incommingNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NumberAreaEngine engine = new NumberAreaEngine();
                String result = engine.obtainnumberArea(NumberAreaService.this, incommingNumber);
                Message message = mHandler.obtainMessage();
                message.what = 1;
                message.obj = result;
                mHandler.sendMessage(message);
            }
        }).start();
    }


}

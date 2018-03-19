package test.qun.com.weishi.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import test.qun.com.weishi.R;
import test.qun.com.weishi.util.LogUtil;

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
        mView = View.inflate(this, R.layout.number_area_toast, null);
        mTvNumberArea = mView.findViewById(R.id.tv_number_area);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPhoneStateListener != null && mTm != null) {
            mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        LogUtil.i("NumberAreaService onDestroy");
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
        mWm.removeView(mView);
    }

    private void showToast(String incommingNumber) {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWm.addView(mView, params);
//        queryNumberArea(incommingNumber);
    }

}

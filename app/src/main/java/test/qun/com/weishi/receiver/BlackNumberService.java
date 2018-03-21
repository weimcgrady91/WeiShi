package test.qun.com.weishi.receiver;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import test.qun.com.weishi.R;
import test.qun.com.weishi.db.BlackNumberDao;
import test.qun.com.weishi.service.LocationService;
import test.qun.com.weishi.util.LogUtil;

public class BlackNumberService extends Service {
    private SmsReceiver mSmsReceiver = new SmsReceiver();
    private static final String TAG = BlackNumberService.class.getSimpleName();
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;

    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(10000);
        registerReceiver(mSmsReceiver, intentFilter);

        //1,电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //2,监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
        if (mSmsReceiver != null) {
            unregisterReceiver(mSmsReceiver);
        }
        if (mTM != null && mPhoneStateListener != null) {
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }


    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i(TAG, "SmsReceive");
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = smsMessage.getOriginatingAddress();
                int mode = BlackNumberDao.getInstance().getMode(originatingAddress);
                if (mode == 1 || mode == 2) {
                    LogUtil.i(TAG, "receive sms abortBroadcast");
                    abortBroadcast();
                }
            }
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {
        //3,手动重写,电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //挂断电话 	aidl文件中去了
//				mTM.endCall();
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public void endCall(String phone) {
        int mode = BlackNumberDao.getInstance().getMode(phone);

        if (mode == 2 || mode == 3) {
//			ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
            //ServiceManager此类android对开发者隐藏,所以不能去直接调用其方法,需要反射调用
            try {
                //1,获取ServiceManager字节码文件
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2,获取方法
                Method method = clazz.getMethod("getService", String.class);
                //3,反射调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //4,调用获取aidl文件对象方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //5,调用在aidl中隐藏的endCall方法
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

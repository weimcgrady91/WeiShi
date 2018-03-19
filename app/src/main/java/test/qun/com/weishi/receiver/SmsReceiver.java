package test.qun.com.weishi.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import java.io.IOException;

import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.service.LocationService;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.PreferencesUtil;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("sms onReceive");
        boolean saveState = (boolean) PreferencesUtil.getData(context, ConstantValue.KEY_PHONE_SAFE_STATE, false);
        if (saveState) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                String body = smsMessage.getMessageBody();
                if (body.contains("#*alarm*#")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
//                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } else if (body.contains("#*location*#")) {
                    context.startService(new Intent(context, LocationService.class));
                } else if (body.contains("#*lockscreen*#")) {
                    DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    if (mDPM.isAdminActive(new ComponentName(context, AdminReceiver.class))) {
                        //锁屏
                        mDPM.resetPassword("1234", 0);
                        mDPM.lockNow();
                    } else {
                        LogUtil.i("not admin device");
                    }
                }
            }
        }

    }
}

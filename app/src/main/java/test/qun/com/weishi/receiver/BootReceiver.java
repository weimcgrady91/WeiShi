package test.qun.com.weishi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.PreferencesUtil;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("on reboot finish");
        boolean saveState = (boolean) PreferencesUtil.getData(context, ConstantValue.KEY_PHONE_SAFE_STATE, false);
        String isSimBind = (String) PreferencesUtil.getData(context, ConstantValue.KEY_SIM_BOUND, "");
        if (saveState && !TextUtils.isEmpty(isSimBind)) {
            String oldSimSerialNumber = (String) PreferencesUtil.getData(context, ConstantValue.KEY_SIM_BOUND, "");
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String newSimSerialNumber = telephonyManager.getSimSerialNumber();
            if (!oldSimSerialNumber.equals(newSimSerialNumber)) {
                LogUtil.i(TAG, "simSerialNumber is changed");
            } else {
                LogUtil.i(TAG, "simSerialNumber not changed");
            }
            String safeNumber = (String) PreferencesUtil.getData(context,ConstantValue.KEY_PHONE_SAFE_NUMBER,"");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(safeNumber,null,"alarm",null,null);
        }
    }
}

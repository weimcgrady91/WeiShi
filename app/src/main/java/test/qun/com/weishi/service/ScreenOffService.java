package test.qun.com.weishi.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import test.qun.com.weishi.engine.ProcessEngine;

public class ScreenOffService extends Service {

    private ScreenReceive mReceive;

    public ScreenOffService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceive = new ScreenReceive();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceive, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (mReceive != null) {
            unregisterReceiver(mReceive);
        }
    }

    public class ScreenReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessEngine.killAll(context);
        }
    }
}

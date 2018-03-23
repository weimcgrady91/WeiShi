package test.qun.com.weishi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import test.qun.com.weishi.engine.ProcessEngine;

public class KillProcessReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessEngine processEngine = new ProcessEngine();
        processEngine.killAll(context);
    }
}

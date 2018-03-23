package test.qun.com.weishi.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import test.qun.com.weishi.R;
import test.qun.com.weishi.engine.ProcessEngine;
import test.qun.com.weishi.receiver.ProcessWidget;
import test.qun.com.weishi.util.LogUtil;

public class UpdateWidgetService extends Service {
    private static final String TAG = UpdateWidgetService.class.getSimpleName();
    private InnerReceiver mInnerReceiver;
    private Timer mTimer;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);
        startTimer();
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //ui定时刷新
                updateAppWidget();
                LogUtil.i(TAG, "5秒一次的定时任务现在正在运行..........");
            }
        }, 0, 5000);
    }

    private void endTimer() {
        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    ProcessEngine mProcessEngine = new ProcessEngine();

    private void updateAppWidget() {
        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:" + mProcessEngine.obtainProcessCount(this));
        String strAvailSpace = Formatter.formatFileSize(this, mProcessEngine.obtainAvailableMemory(this));
        remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:" + strAvailSpace);

        Intent intent = new Intent("android.intent.action.WEISHI");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        Intent broadCastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcast);

        awm.updateAppWidget(new ComponentName(this, ProcessWidget.class), remoteViews);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mInnerReceiver != null) {
            unregisterReceiver(mInnerReceiver);
        }
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                startTimer();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                endTimer();
            }
        }
    }
}

package test.qun.com.weishi.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import test.qun.com.weishi.activity.EnterPsdActivity;
import test.qun.com.weishi.bean.AppBean;
import test.qun.com.weishi.db.AppLockDao;
import test.qun.com.weishi.util.LogUtil;

public class WatchDogService extends Service {

    private List<AppBean> mLockApps;
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mLockApps.clear();
            mLockApps.addAll(AppLockDao.newInstance().findAll());
        }
    };
    private LocalBroadcastManager mLocalBroadcastManager;
    private InnerReceiver mReceiver;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean isWatch;
    private String mSkipPackagename = "";
    private static final String TAG = WatchDogService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");
        mLockApps = AppLockDao.newInstance().findAll();
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);
        isWatch = true;
        watch();
        mReceiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackagename = intent.getExtras().getString("packagename");
        }
    }

    private void watch() {
        //1,子线程中,开启一个可控死循环
        new Thread() {
            public void run() {
                while (isWatch) {
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    LogUtil.i(TAG,"top packageName=" + packageName);
                    for (AppBean appBean : mLockApps) {
                        if (appBean.getPackageName().equals(packageName)) {
                            if (!mSkipPackagename.equals(packageName)) {
                                //7,弹出拦截界面
                                Intent intent = new Intent(getApplicationContext(), EnterPsdActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("packagename", packageName);
                                startActivity(intent);
                            }
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        isWatch = false;
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }
}

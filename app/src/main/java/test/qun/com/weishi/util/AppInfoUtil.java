package test.qun.com.weishi.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import test.qun.com.weishi.App;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class AppInfoUtil {
    private static final String TAG = "AppInfoUtil";

    public static int obtainVersionCode() {
        int versionCode;
        PackageManager pm = App.sContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(App.sContext.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionCode = -1;
            LogUtil.i(TAG, "package name not found.");
        }
        return versionCode;
    }

    public static String obtainVersionName() {
        String versionName;
        PackageManager pm = App.sContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(App.sContext.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionName = "";
            LogUtil.i(TAG, "package name not found.");
        }
        return versionName;
    }
}

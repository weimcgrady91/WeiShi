package test.qun.com.weishi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import test.qun.com.weishi.activity.SettingActivity;
import test.qun.com.weishi.engine.UpdateEngine;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.PreferencesUtil;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class App extends Application {
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        checkNewVersion();
    }

    private void checkNewVersion() {
        boolean autoUpdateFlag = (Boolean) PreferencesUtil.getData(this, SettingActivity.SettingFragment.KEY_AUTO_UPDATE, false);
        LogUtil.i("autoUpdateFlag=" + autoUpdateFlag);
        if (autoUpdateFlag) {
            UpdateEngine updateEngine = new UpdateEngine();
            updateEngine.fetchNewVersion();
        }
    }
}

package test.qun.com.weishi;

import android.app.Application;
import android.content.Context;

import test.qun.com.weishi.engine.VersionEngine;

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
        VersionEngine versionEngine = new VersionEngine();
        boolean foundNewVersion = versionEngine.obtainNewVersion();
    }
}

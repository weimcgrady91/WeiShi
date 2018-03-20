package test.qun.com.weishi.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2018/3/19.
 */

public class ServiceUtil {

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            String name = info.service.getClassName();
            LogUtil.i("service name=" +name);
            if (name.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}

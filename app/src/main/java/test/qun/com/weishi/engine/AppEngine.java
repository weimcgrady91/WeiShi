package test.qun.com.weishi.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.bean.AppBean;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class AppEngine {
    public static List<AppBean> getAppBeanList(Context context) {
        List<AppBean> list = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : packageInfoList) {
            AppBean appBean = new AppBean();
            appBean.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            appBean.setPackageName(packageInfo.packageName);
            appBean.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                appBean.setSystem(true);
            } else {
                appBean.setSystem(false);
            }
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                appBean.setSDCard(true);
            } else {
                appBean.setSDCard(false);
            }
            list.add(appBean);
        }
        return list;
    }
}

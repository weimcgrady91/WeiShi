package test.qun.com.weishi.engine;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import test.qun.com.weishi.bean.AppBean;
import test.qun.com.weishi.db.AppLockDao;

/**
 * Created by Administrator on 2018/3/24.
 */

public class AppLockEngine {
    public List<AppBean> findAllUnlockApp(Context context) {
        List<AppBean> allAppBeans = AppEngine.getAppBeanList(context);
        List<AppBean> lockBeans = AppLockDao.newInstance().findAll();
        Iterator<AppBean> iterator = allAppBeans.iterator();
        while (iterator.hasNext()) {
            AppBean target = iterator.next();
            for (AppBean lockBean : lockBeans) {
                if (lockBean.getPackageName().equals(target.getPackageName())) {
                    iterator.remove();
                    break;
                }
            }
        }
        return allAppBeans;
    }

    public List<AppBean> findAllLockApp(Context context) {
        List<AppBean> result = new ArrayList<>();

        List<AppBean> allAppBeans = AppEngine.getAppBeanList(context);

        List<AppBean> lockBeans = AppLockDao.newInstance().findAll();

        for (AppBean lockBean : lockBeans) {
            for (AppBean bean : allAppBeans) {
                if (bean.getPackageName().equals(lockBean.getPackageName())) {
                    result.add(bean);
                }
            }
        }
        return result;
    }

}

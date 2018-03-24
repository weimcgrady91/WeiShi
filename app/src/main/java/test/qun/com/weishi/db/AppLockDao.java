package test.qun.com.weishi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.App;
import test.qun.com.weishi.bean.AppBean;

/**
 * Created by Administrator on 2018/3/24.
 */

public class AppLockDao {
    private static volatile AppLockDao mAppLockDao;
    private final AppLockOpenHelper mOpenHelper;
    private Context mContext;

    private AppLockDao() {
        mOpenHelper = new AppLockOpenHelper(App.sContext);
        mContext = App.sContext;
    }

    public static AppLockDao newInstance() {
        if (mAppLockDao == null) {
            synchronized (AppLockDao.class) {
                if (mAppLockDao == null) {
                    mAppLockDao = new AppLockDao();
                }
            }
        }
        return mAppLockDao;
    }

    public void insert(String pckName) {
        ContentValues values = new ContentValues();
        values.put("pck_name", pckName);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.insert("app_lock", null, values);
        db.close();
        mContext.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    public List<AppBean> findAll() {
        List<AppBean> lockBeanList = new ArrayList<>();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("app_lock", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            AppBean bean = new AppBean();
            bean.setPackageName(cursor.getString(1));
            lockBeanList.add(bean);
        }
        cursor.close();
        db.close();
        return lockBeanList;
    }

    public void delete(String pckName) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.delete("app_lock", "pck_name=?", new String[]{pckName});
        db.close();
        mContext.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }
}

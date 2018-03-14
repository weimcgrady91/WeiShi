package test.qun.com.weishi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public class PreferencesUtil {

    public static void setData(Context context, String key, Object value) {
        String type = value.getClass().getSimpleName();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if ("Integer".equals(type)) {
            sp.edit().putInt(key, (int) value).apply();
        } else if ("Boolean".equals(type)) {
            sp.edit().putBoolean(key, (boolean) value).apply();
        } else if ("String".equals(type)) {
            sp.edit().putString(key, (String) value).apply();
        } else if ("Float".equals(type)) {
            sp.edit().putFloat(key, (float) value).apply();
        } else if ("Long".equals(type)) {
            sp.edit().putLong(key, (Long) value).apply();
        }
    }

    public static Object getData(Context context, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return sp.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defValue);
        }
        return defValue;
    }
}

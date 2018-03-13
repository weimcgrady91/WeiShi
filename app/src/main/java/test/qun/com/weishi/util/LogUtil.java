package test.qun.com.weishi.util;

import android.util.Log;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class LogUtil {
    private static final String TAG = "WEI_SHI";

    public static void i(String content) {
        Log.i(TAG, content);
    }

    public static void i(String childTag, String content) {
        Log.i(TAG + "-" + childTag, content);
    }
}

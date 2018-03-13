package test.qun.com.weishi.util;

import android.os.Environment;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class EnvironmentUtil {
    private static final String TAG = EnvironmentUtil.class.getSimpleName();

    /**
     * 检查SD卡是否可用
     *
     * @return
     */
    public static boolean checkSDCardState() {
        boolean state = false;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            state = true;
        }
        return state;
    }

    /**
     * 检查SD卡剩余空间是否满足需求
     *
     * @param apkSize
     * @return
     */
    public static boolean checkUsableSpaceSize(float apkSize) {
        long freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        long usableSpace = Environment.getExternalStorageDirectory().getUsableSpace();
        long totalSpace = Environment.getExternalStorageDirectory().getTotalSpace();
        LogUtil.i(TAG, "onCreate: 剩余空间大小：" + freeSpace / 1024 / 1024 + "M" + " 可用大小:" + usableSpace / 1024 / 1024 + "M" + " 总空间大小:" + totalSpace / 1024 / 1024 + "M");
        float usableSpaceSize = usableSpace / 1024 / 1024;
        if (usableSpaceSize > apkSize) {
            return true;
        }
        return false;
    }
}

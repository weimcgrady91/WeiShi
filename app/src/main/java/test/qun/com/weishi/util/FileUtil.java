package test.qun.com.weishi.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import test.qun.com.weishi.App;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class FileUtil {
    public static boolean checkFileExits(String fileName) {
        Context context = App.sContext;
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {

        }
        return false;
    }
}

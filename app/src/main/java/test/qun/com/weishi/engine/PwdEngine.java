package test.qun.com.weishi.engine;

import android.text.TextUtils;

import org.w3c.dom.Text;

import test.qun.com.weishi.App;
import test.qun.com.weishi.util.Md5Util;
import test.qun.com.weishi.util.PreferencesUtil;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public class PwdEngine {
    public static final String KEY_PWD = "key_pwd";

    public boolean validatePwd(String pwd) {
        String oldPwd = (String) PreferencesUtil.getData(App.sContext, KEY_PWD, "");
        String targetPwd = Md5Util.MD5(pwd);
        if (oldPwd.equals(targetPwd)) {
            return true;
        }
        return false;
    }

    public boolean hasPwd() {
        String pwd = (String) PreferencesUtil.getData(App.sContext, KEY_PWD, "");
        if (TextUtils.isEmpty(pwd)) {
            return false;
        } else {
            return true;
        }
    }

    public void savePwd(String pwd) {
        PreferencesUtil.setData(App.sContext, KEY_PWD, Md5Util.MD5(pwd));
    }
}

package test.qun.com.weishi.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import abc.abc.abc.AdManager;
import abc.abc.abc.nm.cm.ErrorCode;
import abc.abc.abc.nm.sp.SplashViewSettings;
import abc.abc.abc.nm.sp.SpotListener;
import abc.abc.abc.nm.sp.SpotManager;
import abc.abc.abc.nm.sp.SpotRequestListener;
import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.util.AppInfoUtil;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.PreferencesUtil;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        checkRequiredPermission(this);
        initShortCut();
        runApp();
        preloadAd();
//        setupSplashAd();
        boolean hasAd = (boolean) PreferencesUtil.getData(this,"key_ad",false);
        if(hasAd) {
            setupSplashAd();
        } else {
            initEngine();
        }
    }

    private void runApp() {
        AdManager.getInstance(this).init("3677f8b60447cbb2", "8d5cf7826ae7141e", true);
    }

    /**
     * 预加载广告
     */
    private void preloadAd() {
        // 注意：不必每次展示插播广告前都请求，只需在应用启动时请求一次
        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                LogUtil.i(TAG, "请求插播广告成功");
                //				// 应用安装后首次展示开屏会因为本地没有数据而跳过
                //              // 如果开发者需要在首次也能展示开屏，可以在请求广告成功之前展示应用的logo，请求成功后再加载开屏
                //				setupSplashAd();
                PreferencesUtil.setData(SplashActivity.this,"key_ad",true);
            }

            @Override
            public void onRequestFailed(int errorCode) {
                LogUtil.i(TAG, "请求插播广告失败，errorCode: " + errorCode);
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        LogUtil.i(TAG, "网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        LogUtil.i(TAG, "暂无视频广告");
                        break;
                    default:
                        LogUtil.i(TAG, "请稍后再试");
                        break;
                }
            }
        });
    }

    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        // 创建开屏容器
        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, R.id.view_divider);

        // 对开屏进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        //		// 设置是否展示失败自动跳转，默认自动跳转
        //		splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(MainActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(splashLayout);

        // 展示开屏广告
        SpotManager.getInstance(this)
                .showSplash(this, splashViewSettings, new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                        LogUtil.i(TAG, "开屏展示成功");
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        LogUtil.i(TAG, "开屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                LogUtil.i(TAG, "网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                LogUtil.i(TAG, "暂无开屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                LogUtil.i(TAG, "开屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                LogUtil.i(TAG, "开屏展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                LogUtil.i(TAG, "开屏控件处在不可见状态");
                                break;
                            default:
                                LogUtil.i(TAG, "errorCode: " + errorCode);
                                break;
                        }
                    }

                    @Override
                    public void onSpotClosed() {
                        LogUtil.i(TAG, "开屏被关闭");
                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        LogUtil.i(TAG, "开屏被点击");
                        LogUtil.i(TAG, "是否是网页广告？%s" + (isWebPage ? "是" : "不是"));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(this).onDestroy();
    }

    private void initViews() {
        TextView mVersion = findViewById(R.id.tv_version);
        String versionFormat = getResources().getString(R.string.app_version);
        String version = String.format(versionFormat, AppInfoUtil.obtainVersionName());
        mVersion.setText(version);
        View view = findViewById(R.id.container);
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        view.startAnimation(alphaAnimation);
        importDB("address.db");
        importDB("commonnum.db");
        importDB("antivirus.db");
    }

    /**
     * 生成快捷方式
     */
    private void initShortCut() {
        boolean hasShortcut = (boolean) PreferencesUtil.getData(this, ConstantValue.HAS_SHORTCUT, false);
        if (hasShortcut) {
            return;
        }
        //1,给intent维护图标,名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "卫士");
        //2,点击快捷方式后跳转到的activity
        //2.1维护开启的意图对象
        Intent shortCutIntent = new Intent("android.intent.action.WEISHI");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        //3,发送广播
        sendBroadcast(intent);
        //4,告知sp已经生成快捷方式
        PreferencesUtil.setData(this, ConstantValue.HAS_SHORTCUT, true);
    }

    private void importDB(final String dbName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File parent = getFilesDir();
                File f = new File(parent, dbName);
                if (!f.exists()) {
                    FileOutputStream ops = null;
                    InputStream in = null;
                    try {
                        ops = new FileOutputStream(f);
                        in = getAssets().open(dbName);
                        byte[] buff = new byte[1024];
                        int len = 0;
                        while ((len = in.read(buff)) != -1) {
                            ops.write(buff, 0, len);
                        }
                        ops.flush();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        f.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        f.delete();
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (ops != null) {
                                ops.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    LogUtil.i("import " + dbName + " success");
                } else {
                    LogUtil.i(dbName + "  is exits");
                }
            }
        }).start();
    }

    private void initEngine() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            forwardMainActivity();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void forwardMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //所需要申请的权限数组
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.KILL_BACKGROUND_PROCESSES
    };
    //还需申请的权限列表
    private List<String> permissionsList = new ArrayList<String>();
    //申请权限后的返回码
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private void checkRequiredPermission(final Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() != 0) {

            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(SplashActivity.this, "做一些申请成功的权限对应的事！" + permissions[i], Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SplashActivity.this, "权限被拒绝： " + permissions[i], Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

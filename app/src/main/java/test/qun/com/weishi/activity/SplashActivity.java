package test.qun.com.weishi.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.R;
import test.qun.com.weishi.util.AppInfoUtil;
import test.qun.com.weishi.util.LogUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        initEngine();
        checkRequiredPermission(this);
    }

    private void initViews() {
        TextView mVersion = findViewById(R.id.tv_version);
        String versionFormat = getResources().getString(R.string.app_version);
        String version = String.format(versionFormat, AppInfoUtil.obtainVersionName());
        mVersion.setText(version);
        View view = findViewById(R.id.container);
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        view.startAnimation(alphaAnimation);
        importPhoneAreaDB("address.db");
    }

    private void importPhoneAreaDB(final String dbName) {
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
                    LogUtil.i("import address.db success");
                } else {
                    LogUtil.i(" address.db is exits");
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
            Manifest.permission.WRITE_CALL_LOG
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

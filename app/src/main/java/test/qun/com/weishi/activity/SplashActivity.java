package test.qun.com.weishi.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
}

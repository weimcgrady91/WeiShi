package test.qun.com.weishi.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import test.qun.com.weishi.R;
import test.qun.com.weishi.util.AppInfoUtil;

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

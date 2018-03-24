package test.qun.com.weishi.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import test.qun.com.weishi.R;

public class EnterPsdActivity extends AppCompatActivity {

    private String mPackageName;
    private ImageView mIcon;
    private EditText mEtPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_psd);
        initViews();
        Intent intent = getIntent();
        mPackageName = intent.getExtras().getString("packagename");
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(mPackageName, 0);
            Drawable icon = applicationInfo.loadIcon(pm);
            mIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initViews() {
        mIcon = findViewById(R.id.img_icon);
        mEtPwd = findViewById(R.id.et_pwd);
        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = mEtPwd.getText().toString();
                if (pwd.equals("123")) {
                    finish();
                    Intent intent = new Intent("android.intent.action.SKIP");
                    intent.putExtra("packagename", mPackageName);
                    sendBroadcast(intent);
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(EnterPsdActivity.this);
                    localBroadcastManager.sendBroadcast(intent);
                }
            }
        });
    }
}

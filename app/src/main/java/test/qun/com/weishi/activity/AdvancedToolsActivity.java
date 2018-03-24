package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import test.qun.com.weishi.R;
import test.qun.com.weishi.fragment.SmsBackupFragment;

public class AdvancedToolsActivity extends AppCompatActivity {

    public static void enter(Context context) {
        Intent intent = new Intent(context, AdvancedToolsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_tools);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.tv_phone_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterQueryActivity();
            }
        });
        findViewById(R.id.tv_sms_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsBackup();
            }
        });
        findViewById(R.id.tv_common_phone_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterCommonPhoneActivity();
            }
        });
        findViewById(R.id.tv_app_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterAppLockActivity();
            }
        });
    }

    private void enterAppLockActivity() {
        AppLockActivity.enter(this);
    }

    private void enterCommonPhoneActivity() {
        CommonPhoneActivity.enter(this);
    }

    private void enterQueryActivity() {
        NumberAreaActivity.enter(this);
    }

    private SmsBackupFragment fragment;

    private void smsBackup() {
        fragment = SmsBackupFragment.newInstance(0);
        fragment.show(getSupportFragmentManager(), "SmsBackup");
    }
}

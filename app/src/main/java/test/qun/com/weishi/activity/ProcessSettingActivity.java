package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import test.qun.com.weishi.App;
import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.service.ScreenOffService;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.PreferencesUtil;
import test.qun.com.weishi.util.ServiceUtil;

public class ProcessSettingActivity extends AppCompatActivity {


    private CheckBox mCbShowSysProcess;
    private static final String TAG = ProcessManagerActivity.class.getSimpleName();
    public static void enter(Context context) {
        Intent intent = new Intent(context, ProcessSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        initViews();
    }

    private void initViews() {
        mCbShowSysProcess = findViewById(R.id.cb_show_sys);
        boolean showSysProcess = (boolean) PreferencesUtil.getData(App.sContext, ConstantValue.KEY_SHOW_SYS_PROCESS, false);
        if (showSysProcess) {
            mCbShowSysProcess.setChecked(true);
            mCbShowSysProcess.setText("显示系统进程");
        } else {
            mCbShowSysProcess.setChecked(false);
            mCbShowSysProcess.setText("隐藏系统进程");
        }


        mCbShowSysProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.i(TAG,"onCheckedChanged");
                if (isChecked) {
                    mCbShowSysProcess.setText("显示系统进程");
                    PreferencesUtil.setData(App.sContext, ConstantValue.KEY_SHOW_SYS_PROCESS, true);
                } else {
                    mCbShowSysProcess.setText("隐藏系统进程");
                    PreferencesUtil.setData(App.sContext, ConstantValue.KEY_SHOW_SYS_PROCESS, false);
                }
            }
        });
        CheckBox cbCleanScreenOff = findViewById(R.id.cb_screen_off_clean);
        boolean isRunning = ServiceUtil.isServiceRunning(ProcessSettingActivity.this, ScreenOffService.class.getName());
        if (isRunning) {
            cbCleanScreenOff.setChecked(true);
        } else {
            cbCleanScreenOff.setChecked(false);
        }

        cbCleanScreenOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(ProcessSettingActivity.this, ScreenOffService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(ProcessSettingActivity.this, ScreenOffService.class);
                    stopService(intent);
                }
            }
        });
    }
}

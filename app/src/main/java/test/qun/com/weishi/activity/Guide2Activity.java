package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.util.PreferencesUtil;
import test.qun.com.weishi.views.SelectItemView;

public class Guide2Activity extends AppCompatActivity implements View.OnClickListener {
    private SelectItemView mSiv;

    public static void enter(Context context) {
        Intent intent = new Intent(context, Guide2Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide2);
        initViews();
    }

    private void initViews() {
        mSiv = findViewById(R.id.stv);
        mSiv.setOnClickListener(this);
        String simNumber = (String) PreferencesUtil.getData(this, ConstantValue.KEY_SIM_BOUND, "");
        if (TextUtils.isEmpty(simNumber)) {
            mSiv.setChecked(false);
        } else {
            mSiv.setChecked(true);
        }
        findViewById(R.id.nextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });
        findViewById(R.id.prvBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prvPage();
            }
        });
    }

    private void nextPage() {
        Guide3Activity.enter(this);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void prvPage() {
        Intent intent = new Intent(this, Guide1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stv:
                Log.e("weiqun12345", "onClick");
                boolean check = mSiv.isChecked();
                if (check) {
                    //解绑
                    PreferencesUtil.setData(this, ConstantValue.KEY_SIM_BOUND, "");
                    mSiv.setChecked(!check);
                } else {
                    //绑定
                    String simNumber = getSimNumber();
                    Log.e("weiqun12345", "sim number=" + simNumber);
                    if (!TextUtils.isEmpty(simNumber)) {
                        PreferencesUtil.setData(this, ConstantValue.KEY_SIM_BOUND, simNumber);
                        mSiv.setChecked(!check);
                        return;
                    }
                    Toast.makeText(this, "未发现Sim卡", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String getSimNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }
}

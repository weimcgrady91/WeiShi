package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.util.PreferencesUtil;

public class FoundActivity extends AppCompatActivity {

    private TextView mTv_safe_number;
    private ImageView mIv_safe_state;

    public static void forwardFoundActivity(Context context) {
        Intent intent = new Intent(context, FoundActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        boolean guideFinish = (boolean) PreferencesUtil.getData(this, ConstantValue.KEY_GUIDE_FINISH, false);
        if (guideFinish) {
            initView();
        } else {
            Guide1Activity.enter(this);
            finish();
        }
    }

    private void initView() {
        mTv_safe_number = findViewById(R.id.tv_safe_number);
        mIv_safe_state = findViewById(R.id.iv_safe_state);
        findViewById(R.id.ll_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        String saveNumber = (String) PreferencesUtil.getData(this, ConstantValue.KEY_PHONE_SAFE_NUMBER, "");
        mTv_safe_number.setText(saveNumber);
        boolean saveState = (boolean) PreferencesUtil.getData(this, ConstantValue.KEY_PHONE_SAFE_STATE, false);
        if(saveState){
            mIv_safe_state.setImageResource(R.drawable.lock);
        } else {
            mIv_safe_state.setImageResource(R.drawable.unlock);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String saveNumber = (String) PreferencesUtil.getData(this, ConstantValue.KEY_PHONE_SAFE_NUMBER, "");
        mTv_safe_number.setText(saveNumber);
        boolean saveState = (boolean) PreferencesUtil.getData(this, ConstantValue.KEY_PHONE_SAFE_STATE, false);
        if(saveState){
            mIv_safe_state.setImageResource(R.drawable.lock);
        } else {
            mIv_safe_state.setImageResource(R.drawable.unlock);
        }
    }

    private void reset() {
        Guide1Activity.enter(this);
    }
}

package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.util.PreferencesUtil;
import test.qun.com.weishi.views.CheckItemView;

public class Guide4Activity extends AppCompatActivity {

    private CheckItemView mCiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide4);
        initViews();
    }

    private void initViews() {
        mCiv = findViewById(R.id.civ);
        mCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCivClick();
            }
        });
        boolean saveState = (boolean) PreferencesUtil.getData(this, ConstantValue.KEY_PHONE_SAFE_STATE, false);
        if (saveState) {
            mCiv.setChecked(true);
        } else {
            mCiv.setChecked(false);
        }
        findViewById(R.id.prvBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prvPage();
            }
        });
        findViewById(R.id.nextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });
    }

    private void prvPage() {
        Guide3Activity.enter(this);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }

    private void nextPage() {
        PreferencesUtil.setData(this, ConstantValue.KEY_GUIDE_FINISH, true);
        FoundActivity.forwardFoundActivity(this);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void onCivClick() {
        mCiv.setChecked(!mCiv.isChecked());
        PreferencesUtil.setData(this, ConstantValue.KEY_PHONE_SAFE_STATE, mCiv.isChecked());
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, Guide4Activity.class);
        context.startActivity(intent);
    }

}

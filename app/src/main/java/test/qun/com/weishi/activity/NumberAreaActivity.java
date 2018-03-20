package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import test.qun.com.weishi.R;
import test.qun.com.weishi.engine.NumberAreaEngine;

public class NumberAreaActivity extends AppCompatActivity {

    private EditText mEtPhone;
    private Button mBtnQuery;
    private TextView mTvPhoneArea;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String address = (String) msg.obj;
                mTvPhoneArea.setText(address);
            }
        }
    };

    public static void enter(Context context) {
        Intent intent = new Intent(context, NumberAreaActivity.class);
        context.startActivity(intent);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            query(s.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_query);
        initViews();
    }

    private void initViews() {
        mEtPhone = findViewById(R.id.et_phone);
        mEtPhone.addTextChangedListener(mTextWatcher);
        mBtnQuery = findViewById(R.id.btn_query);
        mBtnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query(mEtPhone.getText().toString());
            }
        });
        mTvPhoneArea = findViewById(R.id.tv_phone_area);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEtPhone.removeTextChangedListener(mTextWatcher);
    }

    private void query(final String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            mEtPhone.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                NumberAreaEngine engine = new NumberAreaEngine();
                String result = engine.obtainnumberArea(NumberAreaActivity.this, phoneNumber);
                Message message = mHandler.obtainMessage();
                message.what = 1;
                message.obj = result;
                mHandler.sendMessage(message);
            }
        }).start();
    }
}

package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;

import test.qun.com.weishi.R;

public class PhoneQueryActivity extends AppCompatActivity {

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
        Intent intent = new Intent(context, PhoneQueryActivity.class);
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
        String regex = "^1[3-8]\\d{9}";
        if (phoneNumber.matches(regex)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = getFilesDir().getAbsolutePath() + "/" + "address.db";
                    SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
                    String phone = phoneNumber.substring(0, 7);
                    Cursor cursor = sqLiteDatabase.query("data1", new String[]{"outKey"}, "id=?", new String[]{phone}, null, null, null);
                    if (cursor.moveToNext()) {
                        String outKey = cursor.getString(0);
                        Cursor inCursor = sqLiteDatabase.query("data2", new String[]{"location"}, "id=?", new String[]{outKey}, null, null, null);
                        if (inCursor.moveToNext()) {
                            String address = inCursor.getString(0);
                            Message message = mHandler.obtainMessage();
                            message.what = 1;
                            message.obj = address;
                            mHandler.sendMessage(message);
                            inCursor.close();
                        }
                    }
                    cursor.close();

                }
            }).start();
        } else {
            mTvPhoneArea.setText("未知的号码");
        }
    }
}

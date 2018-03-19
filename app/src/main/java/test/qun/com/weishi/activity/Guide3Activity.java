package test.qun.com.weishi.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.util.PreferencesUtil;

public class Guide3Activity extends AppCompatActivity {

    private EditText mEt_saveNumber;

    public static void enter(Context context) {
        Intent intent = new Intent(context, Guide3Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide3);
        initViews();
    }

    private void initViews() {
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
        findViewById(R.id.btn_select_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });
        mEt_saveNumber = findViewById(R.id.et_safe_number);
        String saveNumber = (String) PreferencesUtil.getData(this, ConstantValue.KEY_PHONE_SAFE_NUMBER, "");
        mEt_saveNumber.setText(saveNumber);
    }

    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Uri contactData = data.getData();
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(contactData, null, null, null, null, null);
                c.moveToFirst();
                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                                + contactId, null, null);
                while (phone.moveToNext()) {
                    String number = phone
                            .getString(phone
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replace("-", "").replace(" ", "");
                    mEt_saveNumber.setText(number);
                }
            }
        }
    }

    private void nextPage() {
        saveSaveNumber();
        Guide4Activity.enter(this);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void saveSaveNumber() {
        String saveNumber = mEt_saveNumber.getText().toString();
        PreferencesUtil.setData(this, ConstantValue.KEY_PHONE_SAFE_NUMBER, saveNumber);
    }

    private void prvPage() {
        Guide2Activity.enter(this);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}

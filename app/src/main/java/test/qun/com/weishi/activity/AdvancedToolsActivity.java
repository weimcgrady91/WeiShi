package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import test.qun.com.weishi.R;

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
    }

    private void enterQueryActivity() {
        NumberAreaActivity.enter(this);
    }
}

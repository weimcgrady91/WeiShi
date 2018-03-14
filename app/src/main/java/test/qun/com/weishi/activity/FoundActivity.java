package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import test.qun.com.weishi.R;

public class FoundActivity extends AppCompatActivity {

    public static void forwardFoundActivity(Context context) {
        Intent intent = new Intent(context, FoundActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
    }
}

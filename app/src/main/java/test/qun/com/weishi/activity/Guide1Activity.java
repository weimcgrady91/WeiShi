package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import test.qun.com.weishi.R;

public class Guide1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide1);
        initView();
    }

    private void initView() {
        findViewById(R.id.nextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guide2Activity.enter(Guide1Activity.this);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, Guide1Activity.class);
        context.startActivity(intent);
    }
}

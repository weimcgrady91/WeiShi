package test.qun.com.weishi.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import test.qun.com.weishi.R;

public class RocketActivity extends Activity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket);
        initViews();
    }

    private void initViews() {
        ImageView imgSmockT = findViewById(R.id.img_smock_t);
        ImageView imgSmockM = findViewById(R.id.img_smock_m);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(100);
        imgSmockM.startAnimation(alphaAnimation);
        imgSmockT.startAnimation(alphaAnimation);
        mHandler.sendEmptyMessageDelayed(1, 500);
    }
}

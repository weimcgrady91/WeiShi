package test.qun.com.weishi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import test.qun.com.weishi.App;
import test.qun.com.weishi.WeiShiIntent;
import test.qun.com.weishi.bean.UpdateBean;
import test.qun.com.weishi.fragment.UpdateFragment;
import test.qun.com.weishi.util.LogUtil;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private LocalBroadcastManager mLocalBroadcastManager;
    private UpdateReceiver mReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(App.sContext);
        mReceiver = new UpdateReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WeiShiIntent.ACTION_UPDATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocalBroadcastManager.registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i(TAG, "receive update receiver");
            Bundle bundle = intent.getBundleExtra("updateBean");
            UpdateBean bean = bundle.getParcelable("updateBean");
            showUpdateDialog(bean);
        }
    }

    public void showUpdateDialog(UpdateBean updateBean) {
        FragmentManager fm = getSupportFragmentManager();
        UpdateFragment.newInstance(updateBean).show(fm, "updateDialog");
    }
}

package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.Format;
import java.util.Formatter;

import test.qun.com.weishi.R;

public class TrafficActivity extends AppCompatActivity {
    public static void enter(Context context) {
        Intent intent = new Intent(context,TrafficActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        TextView tvContent = findViewById(R.id.tv_content);

        //获取手机下载流量
        //获取流量(R 手机(2G,3G,4G)下载流量)
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //获取手机的总流量(上传+下载)
        //T total(手机(2G,3G,4G)总流量(上传+下载))
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
        //total(下载流量总和(手机+wifi))
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        //(总流量(手机+wifi),(上传+下载))
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        tvContent.setText("手机下载流量:" + android.text.format.Formatter.formatFileSize(this, mobileRxBytes)
                + "//" + "手机总流量:" + android.text.format.Formatter.formatFileSize(this, mobileTxBytes));
    }
}

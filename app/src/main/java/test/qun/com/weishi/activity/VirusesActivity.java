package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import test.qun.com.weishi.App;
import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.ScanInfo;
import test.qun.com.weishi.engine.AppEngine;
import test.qun.com.weishi.engine.VirusesEngine;
import test.qun.com.weishi.util.Md5Util;

public class VirusesActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private TextView mTvFinish;
    private LinearLayout mLl_content;
    private ImageView mImgScann;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viruses);
        initViews();
        queryViruses();
        initAnimation();
    }

    private void queryViruses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                VirusesEngine engine = new VirusesEngine();
                List<String> viruses = engine.fetchViruses(VirusesActivity.this);
                PackageManager pm = getPackageManager();
                //2.获取所有应用程序签名文件(PackageManager.GET_SIGNATURES 已安装应用的签名文件+)
                //PackageManager.GET_UNINSTALLED_PACKAGES	卸载完了的应用,残余的文件
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(
                        PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);

                mVirusScanInfoList = new ArrayList<ScanInfo>();
                List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();
                mProgressBar.setMax(packageInfoList.size());
                int index = 0;
                for (PackageInfo packageInfo : packageInfoList) {
                    ScanInfo scanInfo = new ScanInfo();
                    Signature[] signatures = packageInfo.signatures;
                    //获取签名文件数组的第一位,然后进行md5,将此md5和数据库中的md5比对
                    Signature signature = signatures[0];
                    String string = signature.toCharsString();
                    //32位字符串,16进制字符(0-f)
                    String encoder = Md5Util.MD5(string);
                    if (viruses.contains(encoder)) {
                        //5.记录病毒
                        scanInfo.isVirus = true;
                        mVirusScanInfoList.add(scanInfo);
                    } else {
                        scanInfo.isVirus = false;
                    }
                    scanInfo.packageName = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    scanInfoList.add(scanInfo);
                    //7.在扫描的过程中,需要更新进度条
                    index++;
                    mProgressBar.setProgress(index);

                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //8.在子线程中发送消息,告知主线程更新UI(1:顶部扫描应用的名称2:扫描过程中往线性布局中添加view)
                    Message msg = Message.obtain();
                    msg.what = SCANING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    protected static final int SCANING = 100;

    protected static final int SCAN_FINISH = 101;
    private List<ScanInfo> mVirusScanInfoList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANING:
                    //1,显示正在扫描应用的名称
                    ScanInfo info = (ScanInfo) msg.obj;
                    mTvFinish.setText(info.name);
                    //2,在线性布局中添加一个正在扫描应用的TextView
                    TextView textView = new TextView(getApplicationContext());
                    if (info.isVirus) {
                        //是病毒
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒:" + info.name);
                    } else {
                        //不是病毒
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全:" + info.name);
                    }
                    mLl_content.addView(textView, 0);
                    break;
                case SCAN_FINISH:
                    mTvFinish.setText("扫描完成");
                    //停止真正执行的旋转动画
                    mImgScann.clearAnimation();
                    //告知用户卸载包含了病毒的应用
                    unInstallVirus();
                    break;
            }

        }
    };
    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        //指定动画一直旋转
//		rotateAnimation.setRepeatMode(RotateAnimation.INFINITE);
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        //保持动画执行结束后的状态
        rotateAnimation.setFillAfter(true);
        //一直执行动画
        mImgScann.startAnimation(rotateAnimation);
    }
    private void initViews() {
        mLl_content = findViewById(R.id.ll_content);
        mProgressBar = findViewById(R.id.pb);
        mTvFinish = findViewById(R.id.tv_finish);
        mImgScann = findViewById(R.id.img_scan);
    }
    protected void unInstallVirus() {
        for(ScanInfo scanInfo:mVirusScanInfoList){
            String packageName = scanInfo.packageName;
            //源码
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:"+packageName));
            startActivity(intent);
        }
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, VirusesActivity.class);
        context.startActivity(intent);
    }
}

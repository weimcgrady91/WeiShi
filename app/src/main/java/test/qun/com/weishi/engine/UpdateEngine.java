package test.qun.com.weishi.engine;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import test.qun.com.weishi.App;
import test.qun.com.weishi.BuildConfig;
import test.qun.com.weishi.R;
import test.qun.com.weishi.WeiShiIntent;
import test.qun.com.weishi.bean.UpdateBean;
import test.qun.com.weishi.util.AppInfoUtil;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.StorageUtil;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class UpdateEngine {
    private static final String TAG = UpdateEngine.class.getSimpleName();
    private NotificationManager mNotificationManager;
    private int mNotificationId;
    private NotificationCompat.Builder mBuilder;
    private long mTotalSize;

    public void fetchNewVersion() {
        String url = "http://10.8.8.143:8080/WeiShiServer/updateInfo.txt";
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().get();
        builder.url(url);
        Request request = builder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG, "obtainNewVersion failure.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String versionInfo = response.body().string();
                    LogUtil.i(TAG, "updateInfo=" + versionInfo);
                    Gson gson = new Gson();
                    UpdateBean updateBean = gson.fromJson(versionInfo, UpdateBean.class);
                    LogUtil.i(TAG, updateBean.toString());
                    int oldVersionCode = AppInfoUtil.obtainVersionCode();
                    int newVersionCode = updateBean.getVersionCode();
                    if (newVersionCode > oldVersionCode) {
                        neededUpdate(updateBean);
                    } else {
                        LogUtil.i(TAG, "It's the latest version");
                    }
                }
            }
        });
    }

    private void neededUpdate(UpdateBean updateBean) {
        Intent intent = new Intent(WeiShiIntent.ACTION_UPDATE);
        Bundle bundle = new Bundle();
        bundle.putParcelable("updateBean", updateBean);
        intent.putExtra("updateBean", bundle);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(App.sContext);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void update(UpdateBean updateBean) {
        LogUtil.i(TAG, "开始更新");
        if (StorageUtil.checkSDCardState() && StorageUtil.checkUsableSpaceSize(updateBean.getApkSize())) {
            String downLoadPath = updateBean.getDownLoadPath();
            final String fileName = downLoadPath.substring(downLoadPath.lastIndexOf("/") + 1, downLoadPath.length());
            LogUtil.i(TAG, "downLoadPath=" + downLoadPath);
            LogUtil.i(TAG, "fileName=" + fileName);

            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().get();
            builder.url(downLoadPath);
            Request request = builder.build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.i(TAG, "down new APK failure.");
                    LogUtil.i(TAG, "e=" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        onPreExecute(response.body().contentLength());
                        InputStream inputStream = response.body().byteStream();
                        downFile(fileName, inputStream);
                        onPostExecute(fileName);
                    } else {
                        LogUtil.i(TAG, "response not in (code >= 200 && code < 300)");
                    }
                }
            });
        } else {
            Toast.makeText(App.sContext, "Lack of storage space", Toast.LENGTH_SHORT).show();
            LogUtil.i(TAG, "Lack of storage space");
        }
    }

    public void onPreExecute(long totalSize) {
        LogUtil.i(TAG, " onPreExecute totalSize=" + totalSize);
        mBuilder = new NotificationCompat.Builder(App.sContext);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(App.sContext.getResources(), R.mipmap.ic_launcher));   //对应2
        mBuilder.setSmallIcon(R.mipmap.ic_launcher); //对应5
        mBuilder.setContentTitle("WeiShi"); //对应1
        mBuilder.setWhen(System.currentTimeMillis()); //对应6 可以自己设置显示时间
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT); //设置优先级 0-2 默认值是0;
        mBuilder.setAutoCancel(false); //设置点击自动清除
        mBuilder.setOngoing(true); //正在运行不会被清除按钮清除掉
        mBuilder.setDefaults(Notification.DEFAULT_ALL);  //设置默认提示 震动 声音 呼吸灯
        mBuilder.setProgress(100, 0, false);
        mNotificationId = 1;
        mNotificationManager = (NotificationManager) App.sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
        mTotalSize = totalSize;
    }

    public void onProgressUpdate(long currentSize) {
        LogUtil.i(TAG, " onProgressUpdate currentSize=" + currentSize);
        mBuilder.setContentText("正在下载" + (int) ((100 * currentSize) / mTotalSize) + "%");
        mBuilder.setProgress(100, (int) ((100 * currentSize) / mTotalSize), false);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void onPostExecute(String fileName) {
        LogUtil.i(TAG, "onPostExecute");
        mBuilder.setContentTitle("下载完成");
        mBuilder.setContentText("100" + "%");
        mBuilder.setProgress(100, 100, false);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
        mNotificationManager.cancel(mNotificationId);
        autoInstall(fileName);
    }

    private void autoInstall(String fileName) {
        File apkFile = new File(App.sContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setDataAndType(Uri.fromFile(),
//                "application/vnd.android.package-archive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(App.sContext, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        App.sContext.startActivity(intent);
    }

    private void downFile(String fileName, InputStream in) {
        try {
            File file = new File(App.sContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            FileOutputStream os = new FileOutputStream(file.getAbsolutePath());
            byte[] buffer = new byte[1024];
            int len;
            int totalLen = 0;
            while ((len = in.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                totalLen += len;
                onProgressUpdate(totalLen);
            }
            os.flush();
            os.close();
            in.close();
            LogUtil.i(TAG, "下载完成.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

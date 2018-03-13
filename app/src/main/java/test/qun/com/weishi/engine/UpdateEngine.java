package test.qun.com.weishi.engine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

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
import test.qun.com.weishi.WeiShiIntent;
import test.qun.com.weishi.bean.UpdateBean;
import test.qun.com.weishi.util.AppInfoUtil;
import test.qun.com.weishi.util.EnvironmentUtil;
import test.qun.com.weishi.util.LogUtil;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class UpdateEngine {
    private static final String TAG = UpdateEngine.class.getSimpleName();

    public void fetchNewVersion() {
        String url = "http://10.8.8.143:8080/WeiShiServer/updateInfo.txt";
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
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
                        LogUtil.i(TAG,"It's the latest version");
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
        if (EnvironmentUtil.checkSDCardState() && EnvironmentUtil.checkUsableSpaceSize(updateBean.getApkSize())) {
            String downPath = updateBean.getDownLoadPath();
            final String fileName = downPath.substring(downPath.lastIndexOf("/"), downPath.length());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(updateBean.getDownLoadPath())
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.i(TAG, "down new APK failure.");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        InputStream inputStream = response.body().byteStream();
                        downFile(fileName, inputStream);
                    }
                }
            });
        } else {

        }
    }

    private void downFile(String fileName, InputStream in) {
        try {
            File file = new File(App.sContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            FileOutputStream os = new FileOutputStream(file.getAbsolutePath());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            os.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

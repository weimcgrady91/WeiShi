package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.App;
import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.AppBean;
import test.qun.com.weishi.engine.AppEngine;
import test.qun.com.weishi.util.LogUtil;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AppManagerActivity.class.getSimpleName();
    private ListView mListView;
    private List<AppBean> mAppBeanList = new ArrayList<>();
    private int mUserSize;
    private int mSystemSize;
    private AppBeanAdapter mAppBeanAdapter;
    private TextView mTv_title;
    private PopupWindow mPopupWindow;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAppBeanList.clear();
            mAppBeanList.addAll((List<AppBean>) msg.obj);
            mUserSize = msg.arg1;
            mSystemSize = msg.arg2;
            mAppBeanAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryAppInfo();
    }

    private void queryAppInfo() {
        LogUtil.i(TAG, "queryAppInfo");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppBean> beans = AppEngine.getAppBeanList(App.sContext);
                List<AppBean> users = new ArrayList<>();
                List<AppBean> systems = new ArrayList<>();

                for (AppBean appBean : beans) {
                    if (appBean.isSystem()) {
                        systems.add(appBean);
                    } else {
                        users.add(appBean);
                    }
                }
                beans.clear();
                beans.addAll(users);
                beans.addAll(systems);
                LogUtil.i(TAG, "user app size=" + users.size());
                LogUtil.i(TAG, "sys app size=" + systems.size());
                Message message = mHandler.obtainMessage();
                message.obj = beans;
                message.arg1 = users.size();
                message.arg2 = systems.size();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }).start();
    }


    private void initViews() {
        String ciPanPath = Environment.getDataDirectory().getAbsolutePath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String memoryAvailSpace = Formatter.formatFileSize(this, calculateAvailableSpace(ciPanPath));
        String sdMemoryAvailSpace = Formatter.formatFileSize(this, calculateAvailableSpace(sdPath));
        TextView mTvCiPan = findViewById(R.id.tv_cipan);
        TextView mTvSD = findViewById(R.id.tv_sd);
        mTvCiPan.setText("磁盘大小:" + memoryAvailSpace);
        mTvSD.setText("SD卡可用:" + sdMemoryAvailSpace);

        mListView = findViewById(R.id.listView);
        mAppBeanAdapter = new AppBeanAdapter(this);
        mListView.setAdapter(mAppBeanAdapter);

        mTv_title = findViewById(R.id.tv_title);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= mUserSize + 1) {
                    //滚动到了系统条目
                    mTv_title.setText("系统应用(" + mSystemSize + ")");
                } else {
                    //滚动到了用户应用条目
                    mTv_title.setText("用户应用(" + mUserSize + ")");
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mUserSize + 1) {
                    return;
                } else {
                    if (position < mUserSize + 1) {
                        mAppBean = mAppBeanList.get(position - 1);
                    } else {
                        mAppBean = mAppBeanList.get(position  - 2);
                    }
                }
                showPopWindow(view);
            }
        });
    }

    private AppBean mAppBean;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if (mAppBean.isSystem()) {
                    Toast.makeText(getApplicationContext(), "此应用不能卸载", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppBean.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_launch:
                PackageManager packageManager = getPackageManager();
                Intent launchIntent = packageManager.getLaunchIntentForPackage(mAppBean.getPackageName());
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "此应用不能被开启", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用,应用名称为" + mAppBean.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;

        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    private void showPopWindow(View parent) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        View view = View.inflate(this, R.layout.view_app_manager_pop, null);
        TextView tvUnInstall = view.findViewById(R.id.tv_uninstall);
        tvUnInstall.setOnClickListener(this);
        TextView tvLaunch = view.findViewById(R.id.tv_launch);
        tvLaunch.setOnClickListener(this);
        TextView tvShare = view.findViewById(R.id.tv_share);
        tvShare.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
//        popupWindow.showAsDropDown(parent, 100, 0);
        mPopupWindow.showAsDropDown(parent, 200, -250);


        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1,
                0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
    }

    public class AppBeanAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public AppBeanAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mAppBeanList.size() + 2;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mUserSize + 1) {
                //title
                return 0;
            } else {
                //content
                return 1;
            }

        }

        @Override
        public AppBean getItem(int position) {
            if (position == 0 || position == mUserSize + 1) {
                return null;
            } else {
                if (position < mUserSize + 1) {
                    return mAppBeanList.get(position - 1);
                } else {
                    AppBean bean = mAppBeanList.get(position -2);
                    LogUtil.i(TAG, "bean=" + bean);
                    return bean;
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                TitleViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.list_item_title, null);
                    holder = new TitleViewHolder();
                    holder.title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (TitleViewHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.title.setText("用户应用(" + mUserSize + ")");
                } else {
                    holder.title.setText("系统应用(" + mSystemSize + ")");
                }
                return convertView;
            } else {
                AppViewHolder appViewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_item_app_manager, parent, false);
                    appViewHolder = new AppViewHolder();
                    appViewHolder.icon = convertView.findViewById(R.id.img_icon);
                    appViewHolder.packageName = convertView.findViewById(R.id.tv_package_name);
                    appViewHolder.appType = convertView.findViewById(R.id.tv_app_type);
                    convertView.setTag(appViewHolder);
                } else {
                    appViewHolder = (AppViewHolder) convertView.getTag();
                }
                LogUtil.i(TAG, "position=" + position);
                appViewHolder.icon.setImageDrawable(getItem(position).getIcon());
                appViewHolder.packageName.setText(getItem(position).getPackageName());
                appViewHolder.appType.setText(getItem(position).isSDCard() ? "SD卡应用" : "手机应用");
                return convertView;
            }
        }

        private class AppViewHolder {
            ImageView icon;
            TextView packageName;
            TextView appType;
        }

        private class TitleViewHolder {
            TextView title;
        }
    }


    private long calculateTotalSpace(String path) {
        StatFs statFs = new StatFs(path);
        return statFs.getTotalBytes();
    }

    private long calculateAvailableSpace(String path) {
        StatFs statFs = new StatFs(path);
        return statFs.getAvailableBytes();
    }

    private long calculateFreeSpace(String path) {
        StatFs statFs = new StatFs(path);
        return statFs.getFreeBytes();
    }

    private long calculateUseSpace(String path) {
        StatFs statFs = new StatFs(path);
        return statFs.getTotalBytes() - statFs.getFreeBytes();
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, AppManagerActivity.class);
        context.startActivity(intent);
    }
}

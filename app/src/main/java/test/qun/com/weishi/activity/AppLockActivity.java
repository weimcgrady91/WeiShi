package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.App;
import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.AppBean;
import test.qun.com.weishi.db.AppLockDao;
import test.qun.com.weishi.engine.AppLockEngine;

public class AppLockActivity extends AppCompatActivity {
    private List<AppBean> lockApps = new ArrayList<>();
    private List<AppBean> unLockApps = new ArrayList<>();
    private Button mBtnUnLock;
    private LinearLayout mLlUnLock;
    private TextView mTvUnLock;
    private ListView mLvUnLock;
    private LockAdapter mUnLockAdapter;
    private Button mBtnLock;
    private LinearLayout mLlLock;
    private TextView mTvLock;
    private ListView mLvLock;
    private LockAdapter mLockAdapter;
    private TranslateAnimation mMTranslateAnimation;
    private TranslateAnimation mTranslateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initViews();
        mBtnUnLock.performClick();
        mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        mTranslateAnimation.setDuration(500);
    }

    private void initViews() {
        mBtnUnLock = findViewById(R.id.btn_unlock);
        mBtnUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData(0);
                mLlUnLock.setVisibility(View.VISIBLE);
                mLlLock.setVisibility(View.GONE);
                mBtnUnLock.setBackgroundResource(R.drawable.tab_left_pressed);
                mBtnLock.setBackgroundResource(R.drawable.tab_right_default);

            }
        });
        mBtnLock = findViewById(R.id.btn_lock);
        mBtnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData(1);
                mLlUnLock.setVisibility(View.GONE);
                mLlLock.setVisibility(View.VISIBLE);
                mBtnUnLock.setBackgroundResource(R.drawable.tab_left_default);
                mBtnLock.setBackgroundResource(R.drawable.tab_right_pressed);
            }
        });

        mLlUnLock = findViewById(R.id.ll_unlock);
        mLlLock = findViewById(R.id.ll_lock);
        mTvUnLock = findViewById(R.id.tv_unlock);
        mTvLock = findViewById(R.id.tv_lock);
        mLvUnLock = findViewById(R.id.lv_unlock);
        mUnLockAdapter = new LockAdapter(this, unLockApps, 0);
        mLvUnLock.setAdapter(mUnLockAdapter);
        mLvLock = findViewById(R.id.lv_lock);
        mLockAdapter = new LockAdapter(this, lockApps, 1);
        mLvLock.setAdapter(mLockAdapter);
    }

    private class LockAdapter extends BaseAdapter {
        private List<AppBean> mAppBeans;
        private LayoutInflater mInflater;
        private int mode;

        public LockAdapter(Context context, List<AppBean> appBeans, int mode) {
            mInflater = LayoutInflater.from(context);
            mAppBeans = appBeans;
            this.mode = mode;
        }

        @Override
        public int getCount() {
            return mAppBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_app_lock, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = convertView.findViewById(R.id.img_icon);
                viewHolder.pckName = convertView.findViewById(R.id.tv_package_name);
                viewHolder.lock = convertView.findViewById(R.id.img_lock);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Drawable icon = mAppBeans.get(position).getIcon();
            viewHolder.icon.setImageDrawable(icon);
            viewHolder.pckName.setText(mAppBeans.get(position).getPackageName());
            if (mode == 0) {
                viewHolder.lock.setImageResource(R.drawable.unlock);
            } else {
                viewHolder.lock.setImageResource(R.drawable.lock);
            }
            final View animationView = convertView;
            viewHolder.lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animationView.startAnimation(mTranslateAnimation);
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (mode == 0) {
                                AppBean bean = mAppBeans.get(position);
                                unLockApps.remove(bean);
                                AppLockDao.newInstance().insert(bean.getPackageName());
                                notifyDataSetChanged();
                                mTvUnLock.setText("为加锁应用数:" + unLockApps.size());
                            } else {
                                AppBean bean = mAppBeans.get(position);
                                lockApps.remove(bean);
                                AppLockDao.newInstance().delete(bean.getPackageName());
                                notifyDataSetChanged();
                                mTvLock.setText("已加锁应用数:" + lockApps.size());
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView icon;
        TextView pckName;
        ImageView lock;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                unLockApps.clear();
                unLockApps.addAll((List<AppBean>) msg.obj);
                mTvUnLock.setText("为加锁应用数:" + unLockApps.size());
                mUnLockAdapter.notifyDataSetChanged();
            } else if (msg.what == 1) {
                lockApps.clear();
                lockApps.addAll((List<AppBean>) msg.obj);
                mTvLock.setText("已加锁应用数:" + lockApps.size());
                mLockAdapter.notifyDataSetChanged();
            }
        }
    };

    private void fetchData(final int which) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLockEngine engine = new AppLockEngine();
                if (which == 0) {
                    List<AppBean> unLockApps = engine.findAllUnlockApp(App.sContext);
                    Message message = mHandler.obtainMessage();
                    message.obj = unLockApps;
                    message.what = 0;
                    mHandler.sendMessage(message);
                } else if (which == 1) {
                    List<AppBean> lockApps = engine.findAllLockApp(App.sContext);
                    Message message = mHandler.obtainMessage();
                    message.obj = lockApps;
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, AppLockActivity.class);
        context.startActivity(intent);
    }
}

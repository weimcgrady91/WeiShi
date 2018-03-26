package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import test.qun.com.weishi.R;

public class CacheClearActivity extends AppCompatActivity {
    protected static final int UPDATE_CACHE_APP = 100;
    protected static final int CHECK_CACHE_APP = 101;
    protected static final int CHECK_FINISH = 102;
    protected static final int CLEAR_CACHE = 103;
    private PackageManager mPm;
    private ProgressBar mPb;
    private TextView mTvName;
    private LinearLayout mLl_add_text;

    public static void enter(Context context) {
        Intent intent = new Intent(context, CacheClearActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initViews();
        queryData();
    }

    private int mIndex;

    private void queryData() {
        mPm = getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
                mPb.setMax(installedPackages.size());
                for (PackageInfo packageInfo : installedPackages) {
                    String packageName = packageInfo.packageName;
                    getPackageCache(packageName);

                    try {
                        Thread.sleep(100 + new Random().nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mIndex++;
                    mPb.setProgress(mIndex);

                    //每循环一次就将检测应用的名称发送给主线程显示
                    Message msg = Message.obtain();
                    msg.what = CHECK_CACHE_APP;
                    String name = null;
                    try {
                        name = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj = name;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = CHECK_FINISH;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void initViews() {
        Button btnClear = findViewById(R.id.btn_clear);
        mPb = findViewById(R.id.pb);
        mTvName = findViewById(R.id.tv_name);
        mLl_add_text = findViewById(R.id.ll_add_text);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取指定类的字节码文件
                try {
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    //2.获取调用方法对象
                    Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    //3.获取对象调用方法
                    method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded)
                                throws RemoteException {
                            //清除缓存完成后调用的方法(考虑权限)
                            Message msg = Message.obtain();
                            msg.what = CLEAR_CACHE;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class CacheInfo {
        public String name;
        public Drawable icon;
        public String packageName;
        public long cacheSize;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CACHE_APP:
                    //8.在线性布局中添加有缓存应用条目
                    View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);

                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_item_name = (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_memory_info = (TextView) view.findViewById(R.id.tv_memory_info);
                    ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_icon.setBackgroundDrawable(cacheInfo.icon);
                    tv_item_name.setText(cacheInfo.name);
                    tv_memory_info.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));

                    mLl_add_text.addView(view, 0);

                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //清除单个选中应用的缓存内容(PackageMananger)

						/* 以下代码如果要执行成功则需要系统应用才可以去使用的权限
                         * android.permission.DELETE_CACHE_FILES
						 * try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							//2.获取调用方法对象
							Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,IPackageDataObserver.class);
							//3.获取对象调用方法
							method.invoke(mPm, cacheInfo.packagename,new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//删除此应用缓存后,调用的方法,子线程中
									Log.i(tag, "onRemoveCompleted.....");
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
                            //源码开发课程(源码(handler机制,AsyncTask(异步请求,手机启动流程)源码))
                            //通过查看系统日志,获取开启清理缓存activity中action和data
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + cacheInfo.packageName));
                            startActivity(intent);
                        }
                    });
                    break;
                case CHECK_CACHE_APP:
                    mTvName.setText((String) msg.obj);
                    break;
                case CHECK_FINISH:
                    mTvName.setText("扫描完成");
                    break;
                case CLEAR_CACHE:
                    //从线性布局中移除所有的条目
                    mLl_add_text.removeAllViews();
                    break;
            }
        }
    };

    /**
     * 通过包名获取此包名指向应用的缓存信息
     *
     * @param packageName 应用包名
     */
    protected void getPackageCache(String packageName) {
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

            public void onGetStatsCompleted(PackageStats stats,
                                            boolean succeeded) {
                //子线程中方法,用到消息机制

                //4.获取指定包名的缓存大小
                long cacheSize = stats.cacheSize;
                //5.判断缓存大小是否大于0
                if (cacheSize > 0) {
                    //6.告知主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = UPDATE_CACHE_APP;
                    CacheInfo cacheInfo = null;
                    try {
                        //7.维护有缓存应用的javabean
                        cacheInfo = new CacheInfo();
                        cacheInfo.cacheSize = cacheSize;
                        cacheInfo.packageName = stats.packageName;
                        cacheInfo.name = mPm.getApplicationInfo(stats.packageName, 0).loadLabel(mPm).toString();
                        cacheInfo.icon = mPm.getApplicationInfo(stats.packageName, 0).loadIcon(mPm);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj = cacheInfo;
                    mHandler.sendMessage(msg);
                }
            }
        };
        //1.获取指定类的字节码文件
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //3.获取对象调用方法
            method.invoke(mPm, packageName, mStatsObserver);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.BlackNumberBean;
import test.qun.com.weishi.db.BlackNumberDao;
import test.qun.com.weishi.fragment.AddBlackNumberFragment;
import test.qun.com.weishi.util.LogUtil;

public class BlackNumberActivity extends AppCompatActivity implements AddBlackNumberFragment.OnAddBlackNumberListener {
    private static final String TAG = BlackNumberActivity.class.getSimpleName();
    private Button mBtnAdd;
    private ListView mListView;
    private List<BlackNumberBean> mBlackNumberBeanList = new ArrayList<>();
    private BlackNumberAdapter mBlackNumberAdapter;

    public static void enter(Context context) {
        Intent intent = new Intent(context, BlackNumberActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        initViews();
    }

    private boolean mIsLoad = false;
    private int mCount;

    private void initViews() {
        mBtnAdd = findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBlackNumber();
            }
        });
        mListView = findViewById(R.id.listView);
        mBlackNumberAdapter = new BlackNumberAdapter(this);
        mListView.setAdapter(mBlackNumberAdapter);
        mCount = BlackNumberDao.getInstance().getCount();
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && mListView.getLastVisiblePosition() >= mBlackNumberBeanList.size() - 1
                        && !mIsLoad) {
                    if (mCount > mBlackNumberBeanList.size()) {
                        //加载下一页数据
                        mIsLoad = true;
                        new Thread() {
                            public void run() {
                                //1,获取操作黑名单数据库的对象
                                //2,查询部分数据
                                List<BlackNumberBean> moreData = BlackNumberDao.getInstance().find(mBlackNumberBeanList.size());
                                //3,添加下一页数据的过程
//                                mBlackNumberList.addAll(moreData);
                                Message message = mHandler.obtainMessage();
                                message.what = 1;
                                message.obj = moreData;
                                mHandler.sendMessage(message);
                            }
                        }.start();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        queryBlackNumber(0);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<BlackNumberBean> list = (List<BlackNumberBean>) msg.obj;
            LogUtil.i(TAG,"new data size=" + list.size());
            mBlackNumberBeanList.addAll(list);
            mBlackNumberAdapter.notifyDataSetChanged();
            mIsLoad = false;
        }
    };

    private void queryBlackNumber(final int pageIndex) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<BlackNumberBean> list = BlackNumberDao.getInstance().find(pageIndex);
                Message message = mHandler.obtainMessage();
                message.what = 1;
                message.obj = list;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private void addBlackNumber() {
        AddBlackNumberFragment addDialog = AddBlackNumberFragment.newInstance();
        addDialog.show(getSupportFragmentManager(), "addBlackNumberDialog");
    }

    @Override
    public void onAddSuccess(BlackNumberBean bean) {
        LogUtil.i(TAG, bean.toString());
        int mode = BlackNumberDao.getInstance().getMode(bean.phone);
        if (mode != -1) {
            BlackNumberDao.getInstance().update(bean.getPhone(), bean.getMode());
            for (BlackNumberBean bean1 : mBlackNumberBeanList) {
                if (bean1.getPhone().equals(bean.getPhone())) {
                    bean1.setMode(bean.getMode());
                    break;
                }
            }
        } else {
            BlackNumberDao.getInstance().insert(bean.getPhone(), bean.getMode());
            mBlackNumberBeanList.add(0, bean);
        }
        mBlackNumberAdapter.notifyDataSetChanged();

    }

    public class BlackNumberAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public BlackNumberAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mBlackNumberBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.black_number_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvPhoneNumber = convertView.findViewById(R.id.tv_phone_number);
                viewHolder.tvMode = convertView.findViewById(R.id.tv_mode);
                viewHolder.imgDelete = convertView.findViewById(R.id.img_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BlackNumberBean bean = mBlackNumberBeanList.get(position);
            viewHolder.tvPhoneNumber.setText(bean.getPhone());
            int mode = bean.getMode();
            if (mode == 0) {
                viewHolder.tvMode.setText("电话");
            } else if (mode == 1) {
                viewHolder.tvMode.setText("短信");
            } else if (mode == 2) {
                viewHolder.tvMode.setText("全部");
            }
            viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBlackNumberBeanList.remove(bean);
                    BlackNumberDao.getInstance().delete(bean.getPhone());
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView tvPhoneNumber;
            TextView tvMode;
            ImageView imgDelete;
        }
    }
}

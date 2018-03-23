package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.ProcessBean;
import test.qun.com.weishi.engine.ProcessEngine;

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvProcessCount;
    private ProcessEngine mProcessEngine;
    private TextView mTvMemory;
    private List<ProcessBean> mProcessBeans = new ArrayList<>();
    private ProcessAdapter mAdapter;
    private TextView mTvTitle;
    private int mUserProcessSize;
    private int mSysProcessSize;
    private ProcessBean mProcessBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initViews();
        mProcessEngine = new ProcessEngine();
    }

    private void initViews() {
        mTvTitle = findViewById(R.id.tv_title);
        mTvProcessCount = findViewById(R.id.tv_process_count);
        mTvMemory = findViewById(R.id.tv_memory);
        ListView listView = findViewById(R.id.listView);
        mAdapter = new ProcessAdapter(this);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= mUserProcessSize + 1) {
                    mTvTitle.setText("系统进程(" + mSysProcessSize + ")");
                } else {
                    mTvTitle.setText("用户进程(" + mUserProcessSize + ")");
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //view选中条目指向的view对象
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mUserProcessSize + 1) {
                    return;
                } else {
                    if (position < mUserProcessSize + 1) {
                        mProcessBean = mProcessBeans.get(position - 1);
                    } else {
                        //返回系统应用对应条目的对象
                        mProcessBean = mProcessBeans.get(position - 2);
                    }
                    if (mProcessBean != null) {
                        if (!mProcessBean.packageName.equals(getPackageName())) {
                            //选中条目指向的对象和本应用的包名不一致,才需要去状态取反和设置单选框状态
                            //状态取反
                            mProcessBean.isCheck = !mProcessBean.isCheck;
                            //checkbox显示状态切换
                            //通过选中条目的view对象,findViewById找到此条目指向的cb_box,然后切换其状态
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_selected);
                            cb_box.setChecked(mProcessBean.isCheck);
                        }
                    }
                }
            }
        });

        Button btnSelectAll = findViewById(R.id.btn_select_all);
        btnSelectAll.setOnClickListener(this);
        Button btnUnSelectAll = findViewById(R.id.btn_un_select_all);
        btnUnSelectAll.setOnClickListener(this);
        Button btnClear = findViewById(R.id.btn_clear);
        Button btnSetting = findViewById(R.id.btn_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_all:
                for (ProcessBean processBean : mProcessBeans) {
                    processBean.isCheck = true;
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_un_select_all:
                for (ProcessBean processBean : mProcessBeans) {
                    processBean.isCheck = !processBean.isCheck;
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTvProcessCount.setText("正在运行的进程数: " + mProcessEngine.obtainProcessCount(this) + "");
        mTvMemory.setText("剩余/总共:" + Formatter.formatFileSize(this, mProcessEngine.obtainAvailableMemory(this))
                + "/" + Formatter.formatFileSize(this, mProcessEngine.obtainTotalMemory(this)));
        queryProcessInfo();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProcessBeans.clear();
            mProcessBeans.addAll((List<ProcessBean>) msg.obj);
            mUserProcessSize = msg.arg1;
            mSysProcessSize = msg.arg2;
            mAdapter.notifyDataSetChanged();
        }
    };

    public class ProcessAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ProcessAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mProcessBeans.size() + 2;
        }

        @Override
        public ProcessBean getItem(int position) {
            if (position == 0 || position == mUserProcessSize + 1) {
                return null;
            } else {
                if (position < mUserProcessSize + 1) {
                    return mProcessBeans.get(position - 1);
                } else {
                    return mProcessBeans.get(position - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mUserProcessSize + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                TitleViewHolder viewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_item_title, parent, false);
                    viewHolder = new TitleViewHolder();
                    viewHolder.title = convertView.findViewById(R.id.tv_title);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (TitleViewHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewHolder.title.setText("用户进程(" + mUserProcessSize + ")");
                } else {
                    viewHolder.title.setText("系统进程(" + mSysProcessSize + ")");
                }
                return convertView;
            } else {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_item_process_manager, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.icon = convertView.findViewById(R.id.img_icon);
                    viewHolder.packageName = convertView.findViewById(R.id.tv_package_name);
                    viewHolder.memory = convertView.findViewById(R.id.tv_memory);
                    viewHolder.isSelected = convertView.findViewById(R.id.cb_selected);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.icon.setImageDrawable(getItem(position).getIcon());
                viewHolder.packageName.setText(getItem(position).getPackageName());
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                viewHolder.memory.setText("占用内存:" + strSize);
                viewHolder.isSelected.setChecked(getItem(position).isCheck);
                if(getItem(position).packageName.equals(getPackageName())){
                    viewHolder.isSelected.setVisibility(View.GONE);
                }else{
                    viewHolder.isSelected.setVisibility(View.VISIBLE);
                }

                return convertView;
            }

        }

        public class TitleViewHolder {
            TextView title;
        }

        public class ViewHolder {
            ImageView icon;
            TextView packageName;
            TextView memory;
            CheckBox isSelected;
        }
    }

    private void queryProcessInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ProcessBean> beans = mProcessEngine.obtainProcessInfo(ProcessManagerActivity.this);
                List<ProcessBean> userProcess = new ArrayList<>();
                List<ProcessBean> sysProcess = new ArrayList<>();
                for (ProcessBean bean : beans) {
                    if (bean.isSystem()) {
                        sysProcess.add(bean);
                    } else {
                        userProcess.add(bean);
                    }
                }
                beans.clear();
                beans.addAll(userProcess);
                beans.addAll(sysProcess);
                Message message = mHandler.obtainMessage();
                message.obj = beans;
                message.arg1 = userProcess.size();
                message.arg2 = sysProcess.size();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, ProcessManagerActivity.class);
        context.startActivity(intent);
    }
}

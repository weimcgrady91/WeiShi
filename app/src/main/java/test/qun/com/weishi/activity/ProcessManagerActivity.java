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

public class ProcessManagerActivity extends AppCompatActivity {

    private TextView mTvProcessCount;
    private ProcessEngine mProcessEngine;
    private TextView mTvMemory;
    private List<ProcessBean> customList = new ArrayList<>();
    private List<ProcessBean> sysList = new ArrayList<>();
    private ProcessAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initViews();
        mProcessEngine = new ProcessEngine();
    }

    private void initViews() {
        mTvProcessCount = findViewById(R.id.tv_process_count);
        mTvMemory = findViewById(R.id.tv_memory);
        ListView listView = findViewById(R.id.listView);
        mAdapter = new ProcessAdapter(this);
        listView.setAdapter(mAdapter);
        TextView tvTitle = findViewById(R.id.tv_title);
        Button btnSelectAll = findViewById(R.id.btn_select_all);
        Button btnUnSelectAll = findViewById(R.id.btn_un_select_all);
        Button btnClear = findViewById(R.id.btn_clear);
        Button btnSetting = findViewById(R.id.btn_setting);
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
            return sysList.size() + customList.size() + 2;
        }

        @Override
        public ProcessBean getItem(int position) {
            if (position == 0 || position == customList.size() + 1) {
                return null;
            } else {
                if (position < customList.size() + 1) {
                    return customList.get(position - 1);
                } else {
                    return sysList.get(position - customList.size() - 2);
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
            if (position == 0 || position == customList.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                TitleViewHolder viewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.listview_app_item_title, parent, false);
                    viewHolder = new TitleViewHolder();
                    viewHolder.title = convertView.findViewById(R.id.tv_title);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (TitleViewHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewHolder.title.setText("用户进程(" + customList.size() + ")");
                } else {
                    viewHolder.title.setText("系统进程(" + sysList.size() + ")");
                }
                return convertView;
            } else {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.process_manager_item, parent, false);
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
                sysList.clear();
                customList.clear();
                List<ProcessBean> beans = mProcessEngine.obtainProcessInfo(ProcessManagerActivity.this);
                for (ProcessBean bean : beans) {
                    if (bean.isSystem()) {
                        sysList.add(bean);
                    } else {
                        customList.add(bean);
                    }
                }

                mHandler.sendEmptyMessage(1);
            }
        }).start();
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, ProcessManagerActivity.class);
        context.startActivity(intent);
    }
}

package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.CommonPhoneChildBean;
import test.qun.com.weishi.bean.CommonPhoneGroupBean;
import test.qun.com.weishi.engine.CommonPhoneEngine;

public class CommonPhoneActivity extends AppCompatActivity {

    private ExpandableListView mExListView;
    private List<CommonPhoneGroupBean> datas = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            datas.clear();
            datas.addAll((List<CommonPhoneGroupBean>) msg.obj);
            mAdapter.notifyDataSetChanged();
        }
    };
    private CommonPhoneAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_phone);
        initViews();
        fetchData();
    }

    public void fetchData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CommonPhoneGroupBean> data = CommonPhoneEngine.fetchData(CommonPhoneActivity.this);
                Message message = mHandler.obtainMessage();
                message.obj = data;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private void initViews() {
        mExListView = findViewById(R.id.exListView);
        mAdapter = new CommonPhoneAdapter(this);
        mExListView.setAdapter(mAdapter);
        mExListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel://" + datas.get(groupPosition).getChildBeanList().get(childPosition).getNumber()));
                startActivity(intent);
                return true;
            }
        });
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, CommonPhoneActivity.class);
        context.startActivity(intent);
    }

    private class CommonPhoneAdapter extends BaseExpandableListAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        public CommonPhoneAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public int getGroupCount() {
            return datas.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return datas.get(groupPosition).getChildBeanList().size();
        }

        @Override
        public CommonPhoneGroupBean getGroup(int groupPosition) {
            return datas.get(groupPosition);
        }

        @Override
        public CommonPhoneChildBean getChild(int groupPosition, int childPosition) {
            return datas.get(groupPosition).getChildBeanList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setText(datas.get(groupPosition).getName());
            textView.setTextColor(Color.RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_common_phone_child, parent, false);
                viewHolder = new ChildViewHolder();
                viewHolder.name = convertView.findViewById(R.id.tv_name);
                viewHolder.phone = convertView.findViewById(R.id.tv_phone);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChildViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(datas.get(groupPosition).getChildBeanList().get(childPosition).getName());
            viewHolder.phone.setText(datas.get(groupPosition).getChildBeanList().get(childPosition).getNumber());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public class ChildViewHolder {
        TextView name;
        TextView phone;
    }
}

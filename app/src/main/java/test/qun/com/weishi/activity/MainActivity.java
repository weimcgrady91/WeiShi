package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import test.qun.com.weishi.R;
import test.qun.com.weishi.engine.UpdateEngine;
import test.qun.com.weishi.fragment.UpdateFragment;

public class MainActivity extends BaseActivity {

    private GridView mGvSpec;
    private String[] mSpecNames;

    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
        checkUpdate();
    }

    private void initViews() {
        mGvSpec = findViewById(R.id.gv_spec);
    }

    private void initData() {
        mSpecNames = getResources().getStringArray(R.array.specs);
        SpecAdapter adapter = new SpecAdapter(this);
        mGvSpec.setAdapter(adapter);
        mGvSpec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                specClick(position);
            }
        });
    }

    private void specClick(int position) {
        switch (position) {
            case 8:
                SettingActivity.forwardSetting(MainActivity.this);
                break;
        }
    }

    private void checkUpdate() {
        UpdateEngine updateEngine = new UpdateEngine();
        updateEngine.fetchNewVersion();
    }

    private class SpecAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public SpecAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mSpecNames.length;
        }

        @Override
        public Object getItem(int position) {
            return mSpecNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_spec, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.specPic = convertView.findViewById(R.id.iv_spec_pic);
                viewHolder.specName = convertView.findViewById(R.id.tv_spec_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.specPic.setImageResource(mPics[position]);
            viewHolder.specName.setText(mSpecNames[position]);
            return convertView;
        }

        private class ViewHolder {
            ImageView specPic;
            TextView specName;
        }
    }

}

package test.qun.com.weishi.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import test.qun.com.weishi.R;
import test.qun.com.weishi.engine.PwdEngine;
import test.qun.com.weishi.fragment.PwdFragment;

public class MainActivity extends BaseActivity implements PwdFragment.OnPwdFragmentInteractionListener {

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
            case 0:
                showPwdDialog();
                break;
            case 1:
                BlackNumberActivity.enter(this);
                break;
            case 2:
                AppManagerActivity.enter(this);
                break;
            case 3:
                ProcessManagerActivity.enter(this);
                break;
            case 4:
                TrafficActivity.enter(this);
                break;
            case 6:
                CacheClearActivity.enter(this);
                break;
            case 7:
                AdvancedToolsActivity.enter(this);
                break;
            case 8:
                SettingActivity.forwardSetting(MainActivity.this);
                break;
        }
    }

    private void showPwdDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PwdFragment fragment;
        PwdEngine pwdEngine = new PwdEngine();
        boolean hasPwd = pwdEngine.hasPwd();
        if (hasPwd) {
            fragment = PwdFragment.newInstance(PwdFragment.validateMode);
        } else {
            fragment = PwdFragment.newInstance(PwdFragment.inputMode);
        }
        fragment.show(fm, "PwdDialog");
    }

    @Override
    public void onValidateSuccess() {
        FoundActivity.forwardFoundActivity(MainActivity.this);
    }

    @Override
    public void onValidateFailure() {

    }

    @Override
    public void onSetupPwdSuccess() {
        FoundActivity.forwardFoundActivity(MainActivity.this);
    }

    @Override
    public void onCancel() {

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
                convertView = mInflater.inflate(R.layout.grid_item_spec, parent, false);
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

package test.qun.com.weishi.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.R;
import test.qun.com.weishi.fragment.FoundGuide1Fragment;
import test.qun.com.weishi.fragment.FoundGuide2Fragment;

public class FoundSettingActivity extends AppCompatActivity {

    private List<Fragment> mFragmentList;

    public static void forwardFoundSettingActivity(Context context) {
        Intent intent = new Intent(context, FoundSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_setting);
        initViews();
    }

    private void initViews() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        FoundGuide1Fragment guide1Fragment = FoundGuide1Fragment.newInstance();
        FoundGuide2Fragment guide2Fragment = FoundGuide2Fragment.newInstance();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(guide1Fragment);
        mFragmentList.add(guide2Fragment);
        FoundSettingAdapter adapter = new FoundSettingAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

    }

    private class FoundSettingAdapter extends FragmentPagerAdapter {
        public FoundSettingAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}

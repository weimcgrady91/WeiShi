package test.qun.com.weishi.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import test.qun.com.weishi.App;
import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.service.NumberAreaService;
import test.qun.com.weishi.util.ServiceUtil;

public class SettingActivity extends AppCompatActivity {

    public static void forwardSetting(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.container, new SettingFragment()).commit();
    }

    public static class SettingFragment extends PreferenceFragment {
        public SettingFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_pref);
            CheckBoxPreference preference = (CheckBoxPreference) findPreference(ConstantValue.KEY_SHOW_NUMBER_AREAF);
            if (ServiceUtil.isServiceRunning(App.sContext, NumberAreaService.class.getName())) {
                preference.setChecked(true);
            } else {
                preference.setChecked(false);
            }

        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            String key = preference.getKey();
            if (ConstantValue.KEY_SHOW_NUMBER_AREAF.equals(key)) {
                CheckBoxPreference p = (CheckBoxPreference) preference;
                if (p.isChecked()) {
                    //startService
                    Intent intent = new Intent(getActivity(), NumberAreaService.class);
                    getActivity().startService(intent);
                } else {
                    //stopService
                    Intent intent = new Intent(getActivity(), NumberAreaService.class);
                    getActivity().stopService(intent);
                }
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}

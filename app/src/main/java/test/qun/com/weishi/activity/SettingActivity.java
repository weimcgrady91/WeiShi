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

import test.qun.com.weishi.R;

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
        public static final String KEY_AUTO_UPDATE = "cbp_auto_update";

        public SettingFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_pref);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            String key = preference.getKey();
            if (KEY_AUTO_UPDATE.equals(key)) {
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}

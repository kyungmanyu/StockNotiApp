package com.joyful.stock;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class AlarmSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("");
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,
                        new MyPreferenceFragment()).commit();
    }

    // PreferenceFragment 클래스 사용
    public static class MyPreferenceFragment extends
            PreferenceFragment implements OnPreferenceChangeListener {
        public static final String PREF_KEY_ENABLE_NOTI = "enable_noti";
        public static final String PREF_KEY_REPEAT_PERIOD = "repeat_frequency";

        public static final String DB_PREF_KEY_ENABLE_NOTI = "db_enable_noti";
        public static final String DB_PREF_KEY_REPEAT_PERIOD = "db_repeat_frequency";

        private CheckBoxPreference mEnableNoti;
        private EditTextPreference mRepeatPeriod;

        private Context mContext;
        private SharedPreferences mPref;

        @Override
        public void onAttach(Activity activity) {
            // TODO Auto-generated method stub
            super.onAttach(activity);
            mContext = activity;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPref = mContext.getSharedPreferences("com.joyful.stock_general_preferences",
                    Context.MODE_PRIVATE);

            addPreferencesFromResource(R.xml.alarm_settings_activity);
            initPreference();
        }

        @Override
        public void onResume() {
            super.onResume();
            loadPreference();
        }

        private void initPreference() {
            mEnableNoti = (CheckBoxPreference)findPreference(PREF_KEY_ENABLE_NOTI);
            mEnableNoti.setOnPreferenceChangeListener(this);

            mRepeatPeriod = (EditTextPreference)findPreference(PREF_KEY_REPEAT_PERIOD);
            mRepeatPeriod.setOnPreferenceChangeListener(this);
        }

        private void loadPreference() {

            if (mEnableNoti != null) {
                boolean bStatus = mPref.getBoolean(DB_PREF_KEY_ENABLE_NOTI, false);
                mEnableNoti.setChecked(bStatus);
            }

            if (mRepeatPeriod != null) {
                int count = mPref.getInt(DB_PREF_KEY_REPEAT_PERIOD, 0);
                mRepeatPeriod.setSummary(count + " 분");
            }
        }

        @Override
        public boolean onPreferenceChange(Preference arg0, Object arg1) {

            String key = arg0.getKey();
            if (PREF_KEY_ENABLE_NOTI.equals(key)) {
                boolean bStatus = ((Boolean)arg1).booleanValue();
                SharedPreferences.Editor editor = mPref.edit();
                editor.putBoolean(DB_PREF_KEY_ENABLE_NOTI, bStatus);
                editor.commit();
                loadPreference();
                enableAlarm(bStatus);

            } else if (PREF_KEY_REPEAT_PERIOD.equals(key)) {
                int count = -1;
                try {
                    count = Integer.parseInt(arg1.toString().trim());
                } catch (Exception e) {
                }

                if (count != -1) {
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putInt(DB_PREF_KEY_REPEAT_PERIOD, count);
                    editor.commit();
                    loadPreference();
                }

            }
            return false;
        }

        private void enableAlarm(boolean val) {

            if (val) {
                int count = mPref.getInt(DB_PREF_KEY_REPEAT_PERIOD, 0);
                if (count > 0) {
                    Toast.makeText(mContext, count + " 분마다 주식 현재가를 확인합니다.", Toast.LENGTH_SHORT)
                            .show();
                    Util.startRepeatAlarm(mContext, count);
                }
            } else {
                Toast.makeText(mContext, "알람을 off합니다.", Toast.LENGTH_SHORT).show();
                Util.cancelRepeatAlarm(mContext);
            }
        }

    }

}

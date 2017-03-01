package cn.gavinliu.bus.station.ui.setting;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import cn.gavinliu.bus.station.R;

/**
 * Created by Gavin on 17-2-21.
 */

public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting);
    }
}

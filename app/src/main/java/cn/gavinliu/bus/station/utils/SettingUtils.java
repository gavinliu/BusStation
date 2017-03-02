package cn.gavinliu.bus.station.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import cn.gavinliu.bus.station.StationApplication;

/**
 * Created by gavin on 2017/3/2.
 */

public class SettingUtils {

    private static SettingUtils util;
    private SharedPreferences mPreference;

    private synchronized static void createInstance(Context ctx) {
        if (util == null) {
            util = new SettingUtils(ctx);
        }
    }

    public static SettingUtils getInstance() {
        if (util == null) {
            createInstance(StationApplication.get());
        }
        return util;
    }

    private SettingUtils(Context ctx) {
        mPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public boolean isVibrate() {
        return mPreference.getBoolean("isVibrate", true);
    }
}

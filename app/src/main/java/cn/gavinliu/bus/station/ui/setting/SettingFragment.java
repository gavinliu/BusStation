package cn.gavinliu.bus.station.ui.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findPreference("openSource").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://github.com/gavinliu/BusStation"));
                startActivity(intent);
                return true;
            }
        });

        findPreference("disclaimer").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("免责声明")
                        .setMessage("程序数据来至微信公众号：珠海公交巴士\n\n非官方客户端中的盗链仅供学习使用，请在下载后24小时内删除，本人不承担任何法律风险。\n\n到站提示功能如失效，导致您延误工作等，本人不承担任何责任。")
                        .create();
                dialog.show();
                return true;
            }
        });
    }
}

package cn.gavinliu.bus.station.ui.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.widget.BaseActivity;

/**
 * Created by Gavin on 17-2-21.
 */

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}

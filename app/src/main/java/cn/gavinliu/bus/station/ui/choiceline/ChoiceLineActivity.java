package cn.gavinliu.bus.station.ui.choiceline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.widget.BaseActivity;

/**
 * Created by gavin on 2017/2/17.
 */

public class ChoiceLineActivity extends BaseActivity {

    public static final String KEY_STATION = "KEY_STATION";

    private static final String TAG = ChoiceLineActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

        Intent intent = getIntent();
        String station;
        if (intent != null) {
            station = intent.getStringExtra(KEY_STATION);
            if (getSupportActionBar() != null) {
                String title = getResources().getString(R.string.choiceline_actionbar_title, station);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(title);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ChoiceLineFragment.newInstance(station))
                    .commitAllowingStateLoss();
        }

    }

}

package cn.gavinliu.bus.station.ui.linedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.widget.BaseActivity;

/**
 * Created by gavin on 2017/2/18.
 */

public class LineDetailActivity extends BaseActivity {

    private static final String TAG = LineDetailActivity.class.getSimpleName();

    public static final String KEY_LINE = "KEY_LINE";

    private Line mLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);


        Intent intent = getIntent();
        if (intent != null) {
            mLine = (Line) intent.getSerializableExtra(KEY_LINE);
        }

        if (getSupportActionBar() != null && mLine != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mLine.getName());
        }

        if (savedInstanceState == null && mLine != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, LineDetailFragment.newInstance(mLine))
                    .commitAllowingStateLoss();
        }

    }
}

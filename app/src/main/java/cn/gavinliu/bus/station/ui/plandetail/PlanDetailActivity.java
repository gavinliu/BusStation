package cn.gavinliu.bus.station.ui.plandetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.widget.BaseActivity;
import cn.gavinliu.zhuhai.station.R;

/**
 * Created by gavin on 2017/2/17.
 */

public class PlanDetailActivity extends BaseActivity {

    private static final String TAG = PlanDetailActivity.class.getSimpleName();

    public static final String KEY_PLAN = "KEY_PLAN";

    private Plan mPlan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

        Intent intent = getIntent();
        if (intent != null) {
            mPlan = (Plan) intent.getSerializableExtra(KEY_PLAN);
        }

        if (getSupportActionBar() != null && mPlan != null) {
            String title = getResources().getString(R.string.plan_title, mPlan.getStation(), mPlan.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        if (savedInstanceState == null && mPlan != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, PlanDetailFragment.newInstance(mPlan))
                    .commitAllowingStateLoss();
        }
    }

}

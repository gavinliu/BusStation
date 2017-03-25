package cn.gavinliu.bus.station.widget;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.utils.ActivityRouter;

/**
 * Created by gavin on 2017/2/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }

            case R.id.action_setting: {
                ActivityRouter.startSetting(this);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}

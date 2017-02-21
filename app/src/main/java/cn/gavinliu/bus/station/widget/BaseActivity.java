package cn.gavinliu.bus.station.widget;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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
        }
        return super.onOptionsItemSelected(item);
    }
}

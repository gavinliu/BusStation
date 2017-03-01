package cn.gavinliu.bus.station.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by gavin on 2017/2/17.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getArguments(getArguments());
    }

    public void getArguments(Bundle bundle) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
    }

    public abstract String getPageName();
}

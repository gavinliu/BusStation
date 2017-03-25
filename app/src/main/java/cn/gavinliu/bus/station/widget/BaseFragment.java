package cn.gavinliu.bus.station.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import cn.gavinliu.bus.station.StationApplication;


/**
 * Created by gavin on 2017/2/17.
 */

public abstract class BaseFragment extends Fragment {

    protected Tracker mTracker;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getArguments(getArguments());
    }

    public void getArguments(Bundle bundle) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StationApplication application = (StationApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Page：" + getPageName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public abstract String getPageName();
}

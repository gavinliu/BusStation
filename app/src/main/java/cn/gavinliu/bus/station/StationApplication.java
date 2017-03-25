package cn.gavinliu.bus.station;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.litesuits.orm.LiteOrm;

import cn.gavinliu.bus.station.utils.ScreenUtils;

/**
 * Created by gavin on 2017/2/16.
 */

public class StationApplication extends Application {

    private static Context sAppContext;

    private volatile static LiteOrm sLiteOrm;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sAppContext = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenUtils.createInstance(this);
    }

    public static Context get() {
        return sAppContext;
    }

    public static LiteOrm getLiteOrm() {
        if (sLiteOrm == null) {
            synchronized (StationApplication.class) {
                if (sLiteOrm == null) {
                    sLiteOrm = LiteOrm.newSingleInstance(sAppContext, "station.db");
                    sLiteOrm.setDebugged(true);
                }
            }
        }
        return sLiteOrm;
    }

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
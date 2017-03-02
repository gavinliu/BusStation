package cn.gavinliu.bus.station;

import android.app.Application;
import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.umeng.analytics.MobclickAgent;

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
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
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
}
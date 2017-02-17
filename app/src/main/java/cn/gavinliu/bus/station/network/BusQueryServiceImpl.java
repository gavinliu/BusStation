package cn.gavinliu.bus.station.network;

import java.util.List;

import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.rxjava.HttpResultFunc;
import rx.Observable;

/**
 * Created by Gavin on 17-1-6.
 */

public class BusQueryServiceImpl {

    private static BusQueryServiceImpl sInstance;

    private BusQueryService mService;

    private BusQueryServiceImpl() {
        mService = NetworkClient.getInstance().getRetrofit().create(BusQueryService.class);
    }

    public synchronized static BusQueryServiceImpl getInstance() {
        if (sInstance == null) sInstance = new BusQueryServiceImpl();
        return sInstance;
    }


    public Observable<List<Line>> getLineListByLineName(String key) {
        return mService.getLineListByLineName(key).flatMap(new HttpResultFunc<List<Line>>());
    }

    public Observable<List<Line>> getLineListByStationName(String key) {
        return mService.getLineListByStationName(key).flatMap(new HttpResultFunc<List<Line>>());
    }

    public Observable<List<String>> getStationNameList(String key) {
        return mService.getStationNameList(key).flatMap(new HttpResultFunc<List<String>>());
    }

}

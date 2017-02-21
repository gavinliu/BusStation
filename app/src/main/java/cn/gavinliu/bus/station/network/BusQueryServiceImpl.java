package cn.gavinliu.bus.station.network;

import java.io.IOException;
import java.util.List;

import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.entity.Station;
import cn.gavinliu.bus.station.network.bean.ResponseBean;
import cn.gavinliu.bus.station.network.rxjava.HttpResultFunc;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

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

    public Observable<Line> updateStationForLine(Line line) {
        return Observable.just(line)
                .flatMap(new Func1<Line, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Line line) {
                        if (line.getStations() == null) {
                            line.setStations(getStationList(line.getId()));
                        }
                        return Observable.just(line);
                    }
                });
    }

    public Observable<Line> updateBusForLine(Line line) {
        return Observable.just(line)
                .flatMap(new Func1<Line, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Line line) {
                        line.setBuses(getBusListOnLine(line.getName(), line.getFromStation()));
                        return Observable.just(line);
                    }
                });
    }

    private List<Station> getStationList(String lineId) {
        List<Station> result = null;
        try {
            Response<ResponseBean<List<Station>>> response = mService.getStationList(lineId).execute();
            result = response.body().data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Bus> getBusListOnLine(String lineName, String fromStation) {
        List<Bus> result = null;
        try {
            Response<ResponseBean<List<Bus>>> response = mService.getBusListOnLine(lineName, fromStation).execute();
            result = response.body().data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

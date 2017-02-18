package cn.gavinliu.bus.station.network;

import java.util.List;

import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.entity.Station;
import cn.gavinliu.bus.station.network.bean.ResponseBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gavin on 17-1-5.
 */

interface BusQueryService {

    @GET("/Handlers/BusQuery.ashx?handlerName=GetLineListByLineName")
    Observable<ResponseBean<List<Line>>> getLineListByLineName(@Query("key") String key);

    @GET("/Handlers/BusQuery.ashx?handlerName=GetLineListByStationName")
    Observable<ResponseBean<List<Line>>> getLineListByStationName(@Query("key") String key);

    @GET("/Handlers/BusQuery.ashx?handlerName=GetStationList")
    Call<ResponseBean<List<Station>>> getStationList(@Query("lineId") String lineId);

    @GET("/Handlers/BusQuery.ashx?handlerName=GetBusListOnRoad")
    Call<ResponseBean<List<Bus>>> getBusListOnLine(@Query("lineName") String lineName, @Query("fromStation") String fromStation);

    @GET("/Handlers/BusQuery.ashx?handlerName=GetStationNameList")
    Observable<ResponseBean<List<String>>> getStationNameList(@Query("key") String key);

}

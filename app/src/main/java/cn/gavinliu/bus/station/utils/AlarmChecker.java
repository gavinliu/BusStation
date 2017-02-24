package cn.gavinliu.bus.station.utils;

import java.util.List;

import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.service.AlarmManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Gavin on 17-2-24.
 */

public class AlarmChecker implements Func1<Line, Observable<Line>> {

    public String busNumber;
    public String stationName;

    public AlarmChecker() {
        busNumber = AlarmManager.getInstance().getBusNumber();
        stationName = AlarmManager.getInstance().getStationName();
    }

    @Override
    public Observable<Line> call(Line line) {
        List<Bus> buses = line.getBuses();

        if (buses != null) {
            for (Bus bus : buses) {
                if (bus.getBusNumber().equals(busNumber)) {
                    if (bus.getCurrentStation().equals(stationName)) {
                        EventCaster.getInstance().post(new AlarmCheckerEvent());
                    }
                    break;
                }
            }
        }

        return Observable.just(line);
    }


    public static class AlarmCheckerEvent {

    }
}

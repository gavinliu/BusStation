package cn.gavinliu.bus.station.utils;

import android.util.Log;

import java.util.List;

import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.entity.Station;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by gavin on 2017/2/19.
 */

public class CalculateSoonBus implements Func1<Line, Observable<Line>> {

    private static final String TAG = CalculateSoonBus.class.getSimpleName();

    @Override
    public Observable<Line> call(Line line) {
        List<Bus> buses = line.getBuses();
        if (buses != null && buses.size() > 0) {
            if (line.getCurrentStationIndex() == -1) {
                setupCurrentStationIndex(line);
            }

            List<Station> stations = line.getStations();

            int dist = -1;
            Bus soonBus = null;

            for (Bus bus : buses) {
                int index = -1;
                for (int i = 0; i < stations.size(); i++) {
                    Station s = stations.get(i);
                    if (s.getName().equals(bus.getCurrentStation())) {
                        index = i;
                        break;
                    }
                }

                int tempDist = line.getCurrentStationIndex() - index;

                if (tempDist > 0) {
                    if (dist == -1 || tempDist < dist) {
                        dist = tempDist;
                        soonBus = bus;
                    }
                }

            }

            line.setDist(dist);
            line.setSoonBus(soonBus);

            if (soonBus != null) {

                Log.d(TAG, "***DIST: " + dist + ", " + soonBus.getBusNumber() + " " + soonBus.getCurrentStation());
            }


        }

        return Observable.just(line);
    }

    private void setupCurrentStationIndex(Line line) {
        String currentStation = line.getCurrentStation();
        List<Station> stations = line.getStations();
        if (stations != null && stations.size() > 0) {
            for (int i = 0; i < stations.size(); i++) {
                Station s = stations.get(i);
                if (s.getName().equals(currentStation)) {
                    line.setCurrentStationIndex(i);
                    break;
                }
            }
        }
    }
}

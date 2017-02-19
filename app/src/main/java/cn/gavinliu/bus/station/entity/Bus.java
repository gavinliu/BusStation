package cn.gavinliu.bus.station.entity;

import java.io.Serializable;

/**
 * Created by Gavin on 17-1-6.
 */

public class Bus implements Serializable {

    private String BusNumber;

    private String CurrentStation;

    public String getBusNumber() {
        return BusNumber;
    }

    public void setBusNumber(String busNumber) {
        BusNumber = busNumber;
    }

    public String getCurrentStation() {
        return CurrentStation;
    }

    public void setCurrentStation(String currentStation) {
        CurrentStation = currentStation;
    }
}

package cn.gavinliu.bus.station.service;

/**
 * Created by Gavin on 17-2-24.
 */

public class AlarmManager {

    private String lineId;
    private String busNumber;
    private String stationName;

    private static AlarmManager sInstance;

    public synchronized static AlarmManager getInstance() {
        if (sInstance == null) sInstance = new AlarmManager();
        return sInstance;
    }

    private AlarmManager() {

    }

    public void finish() {
        lineId = null;
        busNumber = null;
        stationName = null;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public boolean alarmEnable() {
        return lineId != null && busNumber != null && stationName != null;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}

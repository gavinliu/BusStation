package cn.gavinliu.bus.station.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gavin on 17-1-5.
 */

public class Line implements Serializable {

    private String Id;

    private String Name;

    private String LineNumber;

    private int Direction;

    private String FromStation;

    private String ToStation;

    private String BeginTime;

    private String EndTime;

    private String Price;

    private String Interval;

    private String Description;

    private int StationCount;

    // ---->
    private String mCurrentStation;

    private int mCurrentStationIndex = -1;

    private List<Station> mStations;

    private List<Bus> mBuses;

    private Bus mSoonBus;

    private int mDist = -1;
    // <----

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLineNumber() {
        return LineNumber;
    }

    public void setLineNumber(String lineNumber) {
        LineNumber = lineNumber;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public String getFromStation() {
        return FromStation;
    }

    public void setFromStation(String fromStation) {
        FromStation = fromStation;
    }

    public String getToStation() {
        return ToStation;
    }

    public void setToStation(String toStation) {
        ToStation = toStation;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getInterval() {
        return Interval;
    }

    public void setInterval(String interval) {
        Interval = interval;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getStationCount() {
        return StationCount;
    }

    public void setStationCount(int stationCount) {
        StationCount = stationCount;
    }

    public List<Station> getStations() {
        return mStations;
    }

    public void setStations(List<Station> stations) {
        mStations = stations;
    }

    public List<Bus> getBuses() {
        return mBuses;
    }

    public void setBuses(List<Bus> buses) {
        mBuses = buses;
    }

    public String getCurrentStation() {
        return mCurrentStation;
    }

    public void setCurrentStation(String currentStation) {
        mCurrentStation = currentStation;
    }

    public Bus getSoonBus() {
        return mSoonBus;
    }

    public void setSoonBus(Bus soonBus) {
        mSoonBus = soonBus;
    }

    public int getCurrentStationIndex() {
        return mCurrentStationIndex;
    }

    public void setCurrentStationIndex(int currentStationIndex) {
        mCurrentStationIndex = currentStationIndex;
    }

    public int getDist() {
        return mDist;
    }

    public void setDist(int dist) {
        mDist = dist;
    }


    //    @Override
//    public String toString() {
//        return Name + ": " + FromStation + " -> " + ToStation;
//    }

}

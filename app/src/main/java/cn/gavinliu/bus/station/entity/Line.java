package cn.gavinliu.bus.station.entity;

/**
 * Created by Gavin on 17-1-5.
 */

public class Line {

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

    @Override
    public String toString() {
        return Name + ": " + FromStation + " -> " + ToStation;
    }
}

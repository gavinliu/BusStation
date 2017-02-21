package cn.gavinliu.bus.station.entity;

import java.io.Serializable;

/**
 * Created by Gavin on 17-1-5.
 */

public class Station implements Serializable {

    private static final long serialVersionUID = 3758411441754109245L;

    private String Id;

    private String Name;

    private String Lng;

    private String Lat;

    private String Description;

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

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}

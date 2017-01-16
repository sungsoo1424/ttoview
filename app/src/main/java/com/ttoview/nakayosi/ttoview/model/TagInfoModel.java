package com.ttoview.nakayosi.ttoview.model;

import java.sql.Timestamp;

/**
 * Created by sungs on 2016-09-19.
 */
public class TagInfoModel {

    private String certification;
    private String uid;
    private int place_code;
    private int record_count;
    private String lat;
    private String lng;
    private String location;
    private Timestamp last_record_time;

    private String current_Location;



    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPlace_code() {
        return place_code;
    }

    public void setPlace_code(int place_code) {
        this.place_code = place_code;
    }

    public int getRecord_count() {
        return record_count;
    }

    public void setRecord_count(int record_count) {
        this.record_count = record_count;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getLast_record_time() {
        return last_record_time;
    }

    public void setLast_record_time(Timestamp last_record_time) {
        this.last_record_time = last_record_time;
    }

    public String getCurrent_Location() {
        return current_Location;
    }

    public void setCurrent_Location(String current_Location) {
        this.current_Location = current_Location;
    }

    @Override
    public String toString() {
        return "TagInfoModel{" +
                "certification='" + certification + '\'' +
                ", uid='" + uid + '\'' +
                ", place_code=" + place_code +
                ", record_count=" + record_count +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", location='" + location + '\'' +
                ", last_record_time=" + last_record_time +
                ", current_Location='" + current_Location + '\'' +
                '}';
    }
}

package com.cinemaled.bean;

/**
 * created by fred
 * on 2020/1/14
 */
public class LocationBean {
    //详细地址
    private String address;
    //维度
    private String latitude;
    //经度
    private String longitude;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

package com.example.funmaps.bean;

import java.io.Serializable;

public class FavoriteAddress {

    private int id;//主键
    private String username;//匹配账号信息
    private String addr;    //获取详细地址信息
    private double latitude;    //获取纬度信息
    private double longitude;    //获取经度信息

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

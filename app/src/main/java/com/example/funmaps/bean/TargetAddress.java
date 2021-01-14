package com.example.funmaps.bean;

import java.io.Serializable;

public class TargetAddress implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private double latitude;//纬度信息
    private double longitude;//经度信息
    private String addr;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

}

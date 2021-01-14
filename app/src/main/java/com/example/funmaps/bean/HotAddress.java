package com.example.funmaps.bean;

public class HotAddress implements Comparable<HotAddress>{
    private String address;
    private int rate;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public int compareTo(HotAddress o) {
        return o.getRate()-this.rate;//降序排列
    }

    @Override
    public String toString() {
        return "HotAddress{" +
                "address='" + address + '\'' +
                ", rate=" + rate +
                '}';
    }
}

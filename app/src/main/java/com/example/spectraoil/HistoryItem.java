package com.example.spectraoil;

public class HistoryItem {

    String oilType;
    String status;
    int icon;

    public HistoryItem(String oilType, String status, int icon) {
        this.oilType = oilType;
        this.status = status;
        this.icon = icon;
    }
}
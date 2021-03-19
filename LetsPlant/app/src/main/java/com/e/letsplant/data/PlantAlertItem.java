package com.e.letsplant.data;

public class PlantAlertItem {
    public final String text;
    public final int icon;

    public PlantAlertItem(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return text;
    }
}

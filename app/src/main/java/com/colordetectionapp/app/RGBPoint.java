package com.colordetectionapp.app;
import java.util.ArrayList;
import java.util.List;

public class RGBPoint {
    private int red;
    private int green;
    private int blue;

    public RGBPoint(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}

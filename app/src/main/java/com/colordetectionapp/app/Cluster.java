package com.colordetectionapp.app;
import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private RGBPoint center;
    private List<RGBPoint> points;

    public Cluster(RGBPoint center) {
        this.center = center;
        this.points = new ArrayList<>();
    }

    public RGBPoint getCenter() {
        return center;
    }

    public void setCenter(RGBPoint center) {
        this.center = center;
    }

    public List<RGBPoint> getPoints() {
        return points;
    }

    public void addPoint(RGBPoint point) {
        points.add(point);
    }

    public void clearPoints() {
        points.clear();
    }
}

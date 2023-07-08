package com.colordetectionapp.app;
import java.util.ArrayList;
import java.util.List;

public class KMeans {
    public List<Cluster> cluster(List<RGBPoint> points, int k) {
        // Initialize k clusters randomly
        List<Cluster> clusters = initializeClusters(points, k);

        boolean converged = false;
        while (!converged) {
            // Assign each point to the nearest cluster
            assignPointsToClusters(points, clusters);

            // Update the cluster centers
            converged = updateClusterCenters(clusters);
        }

        return clusters;
    }

    private List<Cluster> initializeClusters(List<RGBPoint> points, int k) {
        List<Cluster> clusters = new ArrayList<>();

        // Randomly select k points as initial cluster centers
        for (int i = 0; i < k; i++) {
            RGBPoint point = points.get((int) (Math.random() * points.size()));
            Cluster cluster = new Cluster(point);
            clusters.add(cluster);
        }

        return clusters;
    }

    private void assignPointsToClusters(List<RGBPoint> points, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            cluster.clearPoints();
        }

        for (RGBPoint point : points) {
            Cluster nearestCluster = getNearestCluster(point, clusters);
            nearestCluster.addPoint(point);
        }
    }

    private Cluster getNearestCluster(RGBPoint point, List<Cluster> clusters) {
        Cluster nearestCluster = null;
        double minDistance = Double.MAX_VALUE;

        for (Cluster cluster : clusters) {
            double distance = calculateDistance(point, cluster.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                nearestCluster = cluster;
            }
        }

        return nearestCluster;
    }

    private double calculateDistance(RGBPoint p1, RGBPoint p2) {
        int redDiff = p1.getRed() - p2.getRed();
        int greenDiff = p1.getGreen() - p2.getGreen();
        int blueDiff = p1.getBlue() - p2.getBlue();
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    private boolean updateClusterCenters(List<Cluster> clusters) {
        boolean converged = true;

        for (Cluster cluster : clusters) {
            RGBPoint oldCenter = cluster.getCenter();
            RGBPoint newCenter = calculateNewCenter(cluster.getPoints());

            cluster.setCenter(newCenter);
            cluster.clearPoints();

            if (!oldCenter.equals(newCenter)) {
                converged = false;
            }
        }

        return converged;
    }

    private RGBPoint calculateNewCenter(List<RGBPoint> points) {
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;

        for (RGBPoint point : points) {
            totalRed += point.getRed();
            totalGreen += point.getGreen();
            totalBlue += point.getBlue();
        }

        int numPoints = points.size();
        int newRed = totalRed / numPoints;
        int newGreen = totalGreen / numPoints;
        int newBlue = totalBlue / numPoints;

        return new RGBPoint(newRed, newGreen, newBlue);
    }
}

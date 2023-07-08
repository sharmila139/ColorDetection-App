package com.colordetectionapp.app;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import android.widget.TextView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class YourActivity extends AppCompatActivity {
    private static final int request_code = 101;
    private Bitmap bitmap;
    private Bitmap hsvBitmap;
    private ArrayList<float[]> hsvDataset;
    private List<String> uniqueColors;
    private TextView colorTextView;
    //private ArrayList<float[]> hsvDataset;

    //private static final String COLORS_DATASET_PATH = "assets/colors.csv";

    // ...

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    request_code);
        }
    }

    ImageView imageview;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        //dataset loading code  //SREP1
        try {
            InputStream inputStream = getAssets().open("colors.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            hsvDataset = new ArrayList<>();
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                int r = Integer.parseInt(values[3]);
                int g = Integer.parseInt(values[4]);
                int b = Integer.parseInt(values[5]);


                // Convert RGB to HSV
                float[] hsvValues = new float[3];
                Color.RGBToHSV(r, g, b, hsvValues);

                // Add the HSV values to the dataset
                hsvDataset.add(hsvValues);
            }

            reader.close();
            Log.d(TAG, "Dataset loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load dataset");
        }


        if (!OpenCVLoader.initDebug()) {
            // OpenCV initialization failed
            Log.e("MainActivity", "OpenCV not loaded");
        } else {
            // OpenCV successfully loaded
            Log.d("MainActivity", "OpenCV loaded");
        }
        button = findViewById(R.id.camera);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, request_code);

            }
        });
    }

    //accessing camera permission and displaying image captured
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setContentView(R.layout.activity2);
        imageview = findViewById(R.id.display);
        //TextView colorTextView = findViewById(R.id.colorTextView);

        imageview.setLayoutParams(new RelativeLayout.LayoutParams(500, 400));
        imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //colorTextView = findViewById(R.id.colorTextView);


        if (requestCode == request_code) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            // Convert the captured image to HSV color space
            convertImageToHSV(bitmap);
            // Set the HSV image to the ImageView
            imageview.setImageBitmap(bitmap);
            // Apply color histogram analysis
            applyColorHistogram();

            //colorTextView.setText(detectedColors);

        }

    }

    //converting loaded image to hsv colorspace //STEP2
    private void convertImageToHSV(Bitmap bitmap) {
        hsvBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[] hsvValues = new float[3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                // Convert RGB to HSV
                Color.RGBToHSV(r, g, b, hsvValues);

                // Update the pixel with the HSV values
                int hsvPixel = Color.HSVToColor(hsvValues);
                hsvBitmap.setPixel(x, y, hsvPixel);
            }
        }

        // Set the converted HSV image to the ImageView
        imageview.setImageBitmap(hsvBitmap);

        // Calculate histogram and analyze color distribution
        applyColorHistogram();
    }

    //STEP 3
    private void applyColorHistogram() {
        // Calculate histogram for the HSV dataset
        int[] hsvDatasetHistogram = calculateHistogram1(hsvDataset);

        // Calculate histogram for the loaded HSV image
        int[] hsvImageHistogram = calculateHistogram2(hsvBitmap);

        // Analyze the color distribution
        analyzeColorDistribution(hsvDatasetHistogram, hsvImageHistogram);
    }

    // for loaded image
    private int[] calculateHistogram2(Bitmap hsvBitmap) {
        // Convert the HSV bitmap to an array of pixels
        int width = hsvBitmap.getWidth();
        int height = hsvBitmap.getHeight();
        int[] pixels = new int[width * height];
        hsvBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // Initialize the histogram array
        int[] histogram = new int[256];

        // Iterate over the pixels and increment the histogram bins
        for (int pixel : pixels) {
            // Extract the HSV values from the pixel
            float[] hsvValues = new float[3];
            Color.colorToHSV(pixel, hsvValues);

            // Increment the corresponding histogram bin
            int bin = Math.round(hsvValues[0] * 255 / 360); // Hue value ranges from 0 to 360 degrees
            histogram[bin]++;
        }

        return histogram;
    }

    //  for dataset loaded
    private int[] calculateHistogram1(ArrayList<float[]> hsvData) {
        // Initialize the histogram array
        int[] histogram = new int[256];

        // Iterate over the HSV data and increment the histogram bins
        for (float[] hsvValues : hsvData) {
            // Extract the hue value from the HSV values
            float hue = hsvValues[0];

            // Convert the hue value to a bin index
            int bin = Math.round(hue * 255 / 360); // Assuming hue value ranges from 0 to 360 degrees

            // Increment the corresponding histogram bin
            histogram[bin]++;
        }

        return histogram;

    }

    private void analyzeColorDistribution(int[] hsvDatasetHistogram, int[] hsvImageHistogram) {
        List<Integer> uniqueHues = new ArrayList<>();

        // Iterate over the histogram bins
        for (int i = 0; i < hsvImageHistogram.length; i++) {
            // Check if the bin has a non-zero count in the image histogram
            if (hsvImageHistogram[i] > 0) {
                // Check if the bin has a non-zero count in the dataset histogram
                if (hsvDatasetHistogram[i] > 0) {
                    // Add the hue value to the list of unique hues
                    uniqueHues.add(i);
                }
            }
        }
        // Get the color names for the unique hues
        //List<String> colorNames = getColorNamesFromHues(uniqueHues);

        // Print the unique hue values
        /*for (int hue : uniqueHues) {
            Log.d(TAG, "Detected Color: Hue " + hue);
        }*/


        // Apply K-means clustering to group similar hues
        int numClusters = 5; // Set the desired number of color clusters
        List<List<Integer>> clusters = kmeansClustering(uniqueHues, numClusters);

        // Print the representative hue value for each cluster
        for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
            List<Integer> cluster = clusters.get(clusterIndex);
            int representativeHue = computeRepresentativeHue(cluster);
            Log.d(TAG, "Cluster " + clusterIndex + ": Detected Color: Hue " + representativeHue);
        }
    }

    private int computeRepresentativeHue(List<Integer> cluster) {
        int sum = 0;
        for (int hue : cluster) {
            sum += hue;
        }
        return sum / cluster.size();
    }

    private List<List<Integer>> kmeansClustering(List<Integer> data, int numClusters) {
        // Initialize the cluster centroids
        List<Integer> centroids = initializeCentroids(data, numClusters);

        // Create a mapping of data points to their cluster assignments
        Map<Integer, Integer> assignments = new HashMap<>();

        // Iterate until convergence (no more reassignments)
        boolean converged = false;
        while (!converged) {
            // Assign data points to the nearest cluster centroid
            for (int i = 0; i < data.size(); i++) {
                int point = data.get(i);
                int nearestCentroid = findNearestCentroid(point, centroids);
                assignments.put(point, nearestCentroid);
            }

            // Update the cluster centroids
            List<Integer> newCentroids = updateCentroids(data, assignments, numClusters);

            // Check for convergence
            converged = centroids.equals(newCentroids);

            // Update the centroids for the next iteration
            centroids = newCentroids;
        }

        // Create clusters based on the assignments
        List<List<Integer>> clusters = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            clusters.add(new ArrayList<>());
        }
        for (int point : data) {
            int cluster = assignments.get(point);
            clusters.get(cluster).add(point);
        }

        return clusters;
    }

    private List<Integer> initializeCentroids(List<Integer> data, int numClusters) {
        // Shuffle the data to randomize the selection of initial centroids
        Collections.shuffle(data);

        // Select the first 'numClusters' data points as initial centroids
        return data.subList(0, numClusters);
    }

    private int findNearestCentroid(int point, List<Integer> centroids) {
        int minDistance = Integer.MAX_VALUE;
        int nearestCentroid = -1;

        // Find the nearest centroid based on Euclidean distance
        for (int i = 0; i < centroids.size(); i++) {
            int centroid = centroids.get(i);
            int distance = Math.abs(point - centroid);
            if (distance < minDistance) {
                minDistance = distance;
                nearestCentroid = i;
            }
        }

        return nearestCentroid;
    }

    private List<Integer> updateCentroids(List<Integer> data, Map<Integer, Integer> assignments, int numClusters) {
        List<Integer> newCentroids = new ArrayList<>();

        // Compute the mean value of each cluster and update the centroids
        for (int cluster = 0; cluster < numClusters; cluster++) {
            List<Integer> clusterPoints = new ArrayList<>();
            for (int point : data) {
                if (assignments.get(point) == cluster) {
                    clusterPoints.add(point);
                }
            }
            int meanValue = computeMean(clusterPoints);
            newCentroids.add(meanValue);
        }

        return newCentroids;
    }

    private int computeMean(List<Integer> data) {
        int sum = 0;
        for (int value : data) {
            sum += value;
        }
        return sum / data.size();
    }
}

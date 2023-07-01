package com.colordetectionapp.app;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import org.opencv.android.OpenCVLoader;
import org.opencv.engine.OpenCVEngineInterface;


public class MainActivity extends AppCompatActivity
{
    private static final int request_code = 101;

    // ...

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    request_code );
        }
    }
    ImageView imageview;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setContentView(R.layout.activity2);
        imageview = findViewById(R.id.display);

        if(requestCode==request_code){
            Bitmap imgbitmap = (Bitmap)data.getExtras().get("data");
            imageview.setImageBitmap(imgbitmap);

        }
    }
}


/*public class MainActivity extends AppCompatActivity
{

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private static final int request_code = 101;

    // ...

    private void requestCameraPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    request_code);
        } else {
            launchCamera();
        }
    }
    private void launchCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ActivityResultLauncher<Intent> cameraLauncher = null;
        cameraLauncher.launch(intent);
    }
    ImageView imageview;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);


        imageview = findViewById(R.id.imageview);
        button = findViewById(R.id.camera);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
            }
        });

    }



}*/













        /*activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Handle the result here
                        Intent data = result.getData();
                        // Process the data
                    }
                }
        );

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                registerForActivityResult()
            }
        });
    }

}*/
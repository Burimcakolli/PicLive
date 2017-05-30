package cakhen.piclive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;


public class CameraActivity extends AppCompatActivity {
    Camera mCamera;
    FrameLayout preview;
    CameraPreview mPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT < 16)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);


        int hasPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(hasPerm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
        }
        mCamera = getCameraInstance(0);
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(this, mCamera, this);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        // Set up the user interaction to manually show or hide the system UI.
        Button button = (Button) findViewById(R.id.button_capture);
        button.bringToFront();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    }



    public void switchCamera(View view){
        mPreview.swapCamera(view);
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(750);
        rotate.setInterpolator(new LinearInterpolator());

        Button swap= (Button) findViewById(R.id.button_swap);

        swap.startAnimation(rotate);

    }



    public static Camera getCameraInstance(int i){
        Camera c = null;
        try {
            c = Camera.open(i); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}

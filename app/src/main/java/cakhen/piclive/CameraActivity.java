package cakhen.piclive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO;

@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity {

    Camera mCamera;
    FrameLayout preview;
    CameraPreview mPreview;
    Camera.Parameters params;
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        findViewById(R.id.loadingCircle).setVisibility(View.GONE);

        mCamera = getCameraInstance(0);
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(this, mCamera, this);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        try {
            setParameters();
        } catch (Exception ignored) {

        }

        // Set up the user interaction to manually show or hide the system UI.
        Button button = (Button) findViewById(R.id.button_capture);
        button.bringToFront();

        Button back = (Button) findViewById(R.id.button_back);
        back.bringToFront();

        Button swap = (Button) findViewById(R.id.button_swap);
        swap.bringToFront();

        ProgressBar load = (ProgressBar) findViewById(R.id.loadingCircle);
        load.bringToFront();
        load.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        TextView text = (TextView)findViewById(R.id.bearbeitungstext);
        text.bringToFront();

    }

    public void takePic(View view) {
        try{

            mPreview.createPicture();

        }catch(Exception e){

        }finally {
            loadPictureFullScreen();
        }


    }

    private void loadPictureFullScreen() {
        findViewById(R.id.loadingCircle).setVisibility(View.VISIBLE);
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.bearbeitungstext).setVisibility(View.VISIBLE);
        findViewById(R.id.button_capture).setVisibility(View.GONE);
        findViewById(R.id.button_back).setVisibility(View.GONE);
        findViewById(R.id.button_swap).setVisibility(View.GONE);
        findViewById(R.id.flash).setVisibility(View.GONE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!mPreview.pictureRotated){

                    }
                    path = mPreview.getFile().getAbsolutePath();
                    Log.d("Bild gefunden", path);
                    showPicutreFullSize(path);
                }catch (Exception e){

                }
            }
        };
        thread.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        finish();
    }

    public void goBack(View view) {
        finish();
    }

    public void switchCamera(View view) {
        mPreview.swapCamera();
        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(750);
        rotate.setInterpolator(new LinearInterpolator());
        Button swap = (Button) findViewById(R.id.button_swap);

        swap.startAnimation(rotate);


    }

    public static Camera getCameraInstance(int i) {
        Camera c = null;
        try {
            c = Camera.open(i); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    public void setParameters() {
        mCamera.stopPreview();
        params = mCamera.getParameters();
        try {
            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            Camera.Size size = sizes.get(0);
            for (int i = 0; i < sizes.size(); i++) {

                if (sizes.get(i).width > size.width)
                    size = sizes.get(i);


            }

            params.setJpegQuality(100);
            params.setRotation(90);
            params.setPictureSize(size.width, size.height);
            //automatischer Fokus
            params.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
            mCamera.startPreview();
        }catch (Exception e){

        }
    }

    public void showPicutreFullSize(String path){
        try{
            if(path.equals("") || path == null){
                return;
            }else{
                Intent intent = new Intent(this, ImageActivity.class);
                intent.putExtra("pathName", path);
                startActivity(intent);

            }
        }catch (Exception e) {
            Log.d("FEEEEEEEHHLEEEER", e.getMessage());
        }


    }


}

package cakhen.piclive;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.hardware.Camera.Parameters.EFFECT_MONO;
import static android.hardware.Camera.Parameters.EFFECT_SEPIA;
import static android.hardware.Camera.Parameters.SCENE_MODE_SUNSET;

/**
 * Created by Toshiki on 30.05.2017.
 */

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    Activity activity;
    int cam = 0;
    private Camera.PictureCallback  mPicture;
    public CameraPreview(final Context context, Camera camera, final Activity activity) {

        super(context);
        mCamera = camera;
        this.activity = activity;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // ältere Einstellung, wird gebraucht für ältere Android-Versionen
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        /**
         * PictureCallback, wird gebraucht für das speichern des Bildes
         */
        mPicture = new Camera.PictureCallback() {

            public static final String TAG = "Error";

            /**
             * Holt sich das Bild der Kamera
             * Holt sich Datei in Bytes und speichert diese auf der SD-Karte
             * @param data
             * @param camera
             */
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {


            }
        };

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            mCamera.setDisplayOrientation(90);
            // Setting BestPreviewSize for Current Pixels
            Camera.Parameters p = mCamera.getParameters();

            Camera.Size size = null;
            List<Camera.Size> list = p.getSupportedPreviewSizes();

            size = list.get(0);

            for(int i = 1; i < list.size(); i++) {
                if((list.get(i).width * list.get(i).height) > (size.width * size.height))
                    size = list.get(i);
            }

            if(size != null) {
                p.setPreviewSize(size.width, size.height);
                mCamera.setParameters(p);
            }
            mCamera.startPreview();
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            //Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void swapCamera(View view) {
        try {
            if(cam == 1){
                //öffnet Rückkamera, setzt die Orientation zum Portraitmodus
                //startet die neue Preview
                //WICHTIG!: zuerst immer ein Camera.release durchführen, da man nur auf eine Kamera zugreifen kann und es sonst einen Fehler gibt
                mCamera.release();
                mCamera = Camera.open(0);
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                Camera.Parameters params = mCamera.getParameters();

                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                for(int i=0;i<sizes.size();i++)
                {

                    if(sizes.get(i).width > size.width)
                        size = sizes.get(i);


                }

                params.setJpegQuality(100);
                params.setRotation(90);
                params.setPictureSize(size.width, size.height);

                //automatischer Fokus
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(params);
                cam = 0;
            }else{
                //öffnet Vorderkamera, setzt die Orientation zum Portraitmodus
                //startet die neue Preview
                //WICHTIG!: zuerst immer ein Camera.release durchführen, da man nur auf eine Kamera zugreifen kann und es sonst einen Fehler gibt
                mCamera.release();

                mCamera = Camera.open(1);
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                Camera.Parameters params = mCamera.getParameters();

                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                for(int i=0;i<sizes.size();i++)
                {

                    if(sizes.get(i).width > size.width)
                        size = sizes.get(i);


                }
                params.setJpegQuality(100);
                params.setRotation(270);
                params.setPictureSize(size.width, size.height);

                //automatischer Fokus
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(params);
                cam = 1;
            }

        } catch (RuntimeException e) {

        } catch (IOException e) {


        }
    }
}

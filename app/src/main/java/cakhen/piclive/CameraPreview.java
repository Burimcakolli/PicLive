package cakhen.piclive;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by Toshiki on 30.05.2017.
 * Version 1.2.3
 */
@SuppressWarnings("deprecation")
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {
    File file;
    int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    Activity activity;
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


        mPicture = new Camera.PictureCallback() {

            static final String TAG = "Error";

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null){
                    Throwable e = new Exception();
                    Log.d(TAG, "Error creating media file, check storage permissions: " +
                            e.getMessage());
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    setFile(pictureFile);

                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }
        };

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            mCamera.setDisplayOrientation(90);
            // Setting BestPreviewSize for Current Pixels
            Camera.Parameters p = mCamera.getParameters();
            if (p.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            Camera.Size size;
            List<Camera.Size> list = p.getSupportedPreviewSizes();
            try{
                p.setJpegQuality(100);
            }catch(Exception ignored){

            }

            size = list.get(0);

            for(int i = 1; i < list.size(); i++) {
                if((list.get(i).width * list.get(i).height) > (size.width * size.height))
                    size = list.get(i);
            }
            if(size != null) {
                p.setPreviewSize(size.width, size.height);

            }

            mCamera.setParameters(p);
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

    public void createPicture(){
        mCamera.takePicture(null, null, mPicture);
        mCamera.startPreview();
}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);

            mCamera.release();
            mCamera = null;
        }
    }

    public void swapCamera() {

        mCamera.stopPreview();

        //NB: if you don't release the current camera before switching, you app will crash
        mCamera.release();

        //swap the id of the camera to be used

        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera = Camera.open(currentCameraId);
        Camera.Parameters params = mCamera.getParameters();
        //Code snippet for this method from somewhere on android developers, i forget where
        try {
            //this step is critical or preview on new camera will no know where to render to
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();


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
            if (params.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();

    }

    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "PicLive");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("PicLive", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }
}

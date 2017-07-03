package cakhen.piclive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cakhen.piclive.models.Globals;
import cakhen.piclive.models.LocationDTO;
import cakhen.piclive.models.PictureUploadDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageActivity extends AppCompatActivity {
    public static final MediaType FORM = MediaType.parse("application/json");
    ImageView imageView;
    Drawable d;
    EditText picName;
    Button post;
    ProgressBar progressBar;
    String path;
    String base64;

    private OkHttpClient client;
    private LocationDTO MyLocationDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.imageViewer);
        post = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressCircle);
        Intent intent = getIntent();
        showImage(intent.getStringExtra("pathName"));
        this.path = intent.getStringExtra("pathName");
        picName = (EditText) findViewById(R.id.picName);
        Thread thread = new Thread() {
            @Override
            public void run() {
                base64 = createByteFromImage();
            }
        };
        thread.start();
        client = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.SECONDS)
                .writeTimeout(500, TimeUnit.SECONDS)
                .readTimeout(500, TimeUnit.SECONDS)
                .build();

    }

    private void Home(){
        Intent i_login = new Intent(getApplicationContext(), PicLiveActivity.class);
        i_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i_login);
        finish();
    }

    public void showImage(final String path){

        d = Drawable.createFromPath(path);
        imageView.setImageDrawable(d);

        }

    private void backToPicture(){
        findViewById(R.id.overlayimage).setVisibility(View.GONE);
        findViewById(R.id.textPosting).setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        post.setEnabled(true);
        picName.setEnabled(true);
    }

    public void postPicture(View view) {

        if(picName.getText().length() >= 6){
            findViewById(R.id.overlayimage).setVisibility(View.VISIBLE);
            findViewById(R.id.overlayimage).bringToFront();
            findViewById(R.id.textPosting).setVisibility(View.VISIBLE);
            findViewById(R.id.textPosting).bringToFront();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            post.setEnabled(false);
            picName.setEnabled(false);
            Log.d("SIZE OF Base64", base64.length() + "");
            new GetLocationAsyncTask().execute();
        }
        else{
            picName.setError( "Name must have at least 6 characters" );
        }//-else
    }

    private String createByteFromImage(){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //Log.d("Filepath", path);
        //Bitmap compressed = Bitmap.createScaledBitmap(bitmap, 2560, 1440, false);
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,75, baos);
        //Log.d("sizebaos: ", baos.size() + "");
        byte[] b = baos.toByteArray();
        //
        //Log.d("bytetoString: ", b + "");
        String temp=Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        Log.d("bytesadsadas: ", temp.length() + "");
        return temp;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Home();

        }
        return super.onKeyDown(keyCode, event);
    }

    private Response PostPicture(PictureUploadDTO Upload){
        // Create a new HttpClient and Post Header
        Log.d("NAME", Upload.Name);
        Log.d("City", Upload.City);
        Log.d("Lat", Upload.Lat + "");
        Log.d("Lng", Upload.Lng + "");
        //Log.d("IMAGE", Upload.Image);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", Upload.Name);
            jsonObject.put("image", base64);
            jsonObject.put("lng", String.valueOf(Upload.Lng));
            jsonObject.put("lat", String.valueOf(Upload.Lat));
            jsonObject.put("city", Upload.City);
            Log.d("Json-Object", String.valueOf(jsonObject));
            Log.d("Correct Base64", base64);
            RequestBody body = RequestBody.create(FORM, jsonObject.toString());
            //Log.d("Requestbody", jsonObject.get("image") +"");
            Request request = new Request.Builder()
                    .header("Authorization", TokenSaver.getToken(getApplicationContext()))
                    .url(Globals.API + "Pictures")
                    .post(body)
                    .build();
            return client.newCall(request).execute();
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class UploadPictureAsyncTask extends AsyncTask<PictureUploadDTO, Integer, Response> {
        @Override
        protected Response doInBackground(PictureUploadDTO... params) {
            // TODO Auto-generated method stub
            return PostPicture(params[0]);
        }

        protected void onPostExecute(Response http_response){
            if(http_response.code() == 200){
                Toast.makeText(getApplicationContext(), "Upload Successfull", Toast.LENGTH_SHORT).show();
                Home();
                backToPicture();
            }
            else if(http_response.code() == 400){
                backToPicture();
                Toast.makeText(getApplicationContext(), "Not all required Data inputs sent", Toast.LENGTH_SHORT).show();
                try {
                    Log.d("HTTP_RESPONSE", http_response.body().string().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                backToPicture();
                try {
                    Log.d("Fail Message", http_response.message().toString());
                    Log.d("Fail Body", http_response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "FAIL! Something unusual happened", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /*public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private class GetLocationAsyncTask extends AsyncTask<Void, Void, LocationDTO> {
        @Override
        protected LocationDTO doInBackground(Void... params) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Log.d("INFO", "START");
            LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            android.location.Location bestLocation = null;
            for (String provider : providers) {
                android.location.Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> listAddresses = null;
            try {
                listAddresses = geocoder.getFromLocation(bestLocation.getLatitude(), bestLocation.getLongitude(), 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String _Location = listAddresses.get(0).getLocality();
                    Log.d("Location", _Location);
                    return new LocationDTO(bestLocation.getLongitude(), bestLocation.getLatitude(), _Location);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(LocationDTO Location){
            Log.d("VAR", Location.Lng + "" );
            Log.d("VAR", Location.Lat + "");
            Log.d("VAR", Location.City);

            if(Location.Lng != 0 && Location.Lat != 0 && Location.City != null){
                MyLocationDTO = new LocationDTO(Location.Lng, Location.Lat, Location.City);
                new UploadPictureAsyncTask().execute(
                        new PictureUploadDTO(
                                picName.getText().toString(),
                                base64,
                                MyLocationDTO.Lng,
                                MyLocationDTO.Lat,
                                MyLocationDTO.City
                        )
                );
            }
            else{
                Toast.makeText(getApplicationContext(), "Location not found, Check your Settings", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void cancelUpload(View view){
        base64 = null;
        Intent i = new Intent(ImageActivity.this, CameraActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


}

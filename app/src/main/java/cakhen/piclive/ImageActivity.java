package cakhen.piclive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cakhen.piclive.models.Globals;
import cakhen.piclive.models.Location;
import cakhen.piclive.models.LocationDTO;
import cakhen.piclive.models.PictureUploadDTO;
import cakhen.piclive.models.UserRegisterDTO;
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

        client = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.SECONDS)
                .writeTimeout(500, TimeUnit.SECONDS)
                .readTimeout(500, TimeUnit.SECONDS)
                .build();

    }

    private void Home(){
        Intent i_login = new Intent(getApplicationContext(), PicLiveActivity.class);
        startActivity(i_login);
        finish();
    }

    public void showImage(final String path){

        d = Drawable.createFromPath(path);
        imageView.setImageDrawable(d);

        }

    public void postPicture(View view) {
        findViewById(R.id.overlayimage).setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        post.setEnabled(false);
        picName.setEnabled(false);


        if(picName.getText().length() >= 6){
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
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        //Log.d("sizebaos: ", baos.size() + "");
        byte[] b = baos.toByteArray();
        //Log.d("bytesadsadas: ", b.length + "");
        //Log.d("bytetoString: ", b + "");
        String temp=Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        //System.out.println(temp.length());
        return temp;
    }

    private Response PostPicture(PictureUploadDTO Upload){
        // Create a new HttpClient and Post Header
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("City", Upload.City);
            jsonObject.put("Image", Upload.Image);
            jsonObject.put("Lat", Upload.Lat);
            jsonObject.put("Lng", Upload.Lng);

            RequestBody body = RequestBody.create(FORM, String.valueOf(jsonObject));
            Request request = new Request.Builder()
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
            }
            else if(http_response.code() == 400){
                Toast.makeText(getApplicationContext(), "Not all required Data inputs sent", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "FAIL! Something unusual happened", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class GetLocationAsyncTask extends AsyncTask<Void, Void, Location> {
        @Override
        protected Location doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return new Location(getApplicationContext());
        }

        protected void onPostExecute(Location Location){
            if(Location.Lng != 0 && Location.Lat != 0 && Location.City != null){
                MyLocationDTO = new LocationDTO(Location.Lng, Location.Lat, Location.City);
                new UploadPictureAsyncTask().execute(
                        new PictureUploadDTO(
                                picName.getText().toString(),
                                createByteFromImage(),
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


}

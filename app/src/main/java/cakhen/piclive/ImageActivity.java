package cakhen.piclive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import cakhen.piclive.models.Location;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    Drawable d;
    EditText picName;
    Button post;
    ProgressBar progressBar;
    String path;
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
        try{
            Runnable runnable = new Runnable() {
                public void run() {

                    String image = createByteFromImage();
                    Log.d("Byte: ", image);
                    // PostConnection postConnection = new PostConnection();
                    //postConnection.postPicture(picName.getText().toString(), image,  8.538114,47.365003, "Zürich");

                    Location location = new Location(ImageActivity.this);
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();
        }catch(Exception e) {
            Log.d("Exception", e.getMessage());
        }


    }

    private String createByteFromImage(){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.d("Filepath", path);
        //Bitmap compressed = Bitmap.createScaledBitmap(bitmap, 2560, 1440, false);
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        Log.d("sizebaos: ", baos.size() + "");
        byte[] b = baos.toByteArray();
        Log.d("bytesadsadas: ", b.length + "");
        Log.d("bytetoString: ", b + "");
        String temp=Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        System.out.println(temp.length());
        return temp;
    }


}

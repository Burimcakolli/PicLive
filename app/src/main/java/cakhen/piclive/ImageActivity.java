package cakhen.piclive;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.imageViewer);
        Intent intent = getIntent();
        showImage(intent.getStringExtra("pathName"));
    }

    public void showImage(final String path){
        Drawable d = Drawable.createFromPath(path);
        imageView.setImageDrawable(d);

        }
}

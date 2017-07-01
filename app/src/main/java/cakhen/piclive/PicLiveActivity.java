package cakhen.piclive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class PicLiveActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    int hasPermCamera = ContextCompat.checkSelfPermission(PicLiveActivity.this, Manifest.permission.CAMERA);
                    int hasPermLocation = ContextCompat.checkSelfPermission(PicLiveActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if (hasPermCamera != PackageManager.PERMISSION_GRANTED && hasPermLocation != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PicLiveActivity.this, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
                    }
                    if(hasPermCamera == PackageManager.PERMISSION_GRANTED && hasPermLocation == PackageManager.PERMISSION_GRANTED){
                        Intent i = new Intent(PicLiveActivity.this, CameraActivity.class);
                        startActivity(i);
                    }

                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_live);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(PicLiveActivity.this, CameraActivity.class);
                    startActivity(i);

                } else {

                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

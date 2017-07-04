package cakhen.piclive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.Objects;

import cakhen.piclive.fragments.FeedFragment;


public class PicLiveActivity extends AppCompatActivity {
    public static String INFOTYPE = "PicLiveActivity";
    private final int MY_PERMISSIONS_REQUEST = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new FeedFragment();
                    break;
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
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragmentContainer, fragment)
                        .commit();
                //Log.println(Log.DEBUG, TAG, "click " + item.getTitle());
                return true;
            } else {
                return false;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_live);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Log.d(INFOTYPE, TokenSaver.getToken(getApplicationContext()));
        //TokenSaver.setToken(getApplicationContext(), ""); Just for Testing Login

        if(TokenSaver.getToken(getApplicationContext()) == null | Objects.equals(TokenSaver.getToken(getApplicationContext()), "")){
            Intent i_login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i_login);
            finish();
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentContainer, new FeedFragment())
                .commit();

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

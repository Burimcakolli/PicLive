package cakhen.piclive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.Objects;

import cakhen.piclive.fragments.FeedFragment;

public class PicLiveActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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
                    Intent i = new Intent(PicLiveActivity.this, CameraActivity.class);
                    startActivity(i);
                    break;
                case R.id.navigation_notifications:
                    break;
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragmentContainer, fragment)
                        .commit();
                Log.println(Log.DEBUG, TAG, "click " + item.getTitle());
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

        //TokenSaver.setToken(getApplicationContext(), "");
        Log.d(TAG, TokenSaver.getToken(getApplicationContext()));

       if(TokenSaver.getToken(getApplicationContext()) == null | Objects.equals(TokenSaver.getToken(getApplicationContext()), "")){
            Intent i_login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i_login);
            finish();
        }

    }

}

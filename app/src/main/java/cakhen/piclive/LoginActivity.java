package cakhen.piclive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Burim Cakolli on 30.06.2017.
 */

public class LoginActivity extends AppCompatActivity {

    public EditText edt_username;
    public EditText edt_password;
    public AppCompatButton btn_login;
    TextView txt_toRegister;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_username = (EditText) findViewById(R.id.input_username);
        edt_password = (EditText) findViewById(R.id.input_password);
        txt_toRegister = (TextView) findViewById(R.id.link_register);

        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();
    }

    public void LogMeIn(View view){

    }

    public void OpenRegister(View view){
        Intent i_register = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i_register);
        finish();
    }

}

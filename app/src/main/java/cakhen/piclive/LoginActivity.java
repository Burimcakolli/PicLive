package cakhen.piclive;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cakhen.piclive.models.Globals;
import cakhen.piclive.models.UserLoginDTO;
import cakhen.piclive.models.UserRegisterDTO;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Burim Cakolli on 30.06.2017.
 */

public class LoginActivity extends AppCompatActivity {
    public static final MediaType FORM = MediaType.parse("multipart/form-data");
    public static String INFOTYPE = "LoginActivity";

    public EditText edt_username;
    public EditText edt_password;
    public AppCompatButton btn_login;
    TextView txt_toRegister;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TokenSaver.setToken(getApplicationContext(),"");
        edt_username = (EditText) findViewById(R.id.input_username);
        edt_password = (EditText) findViewById(R.id.input_password);
        txt_toRegister = (TextView) findViewById(R.id.link_register);

        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();
    }

    private void Home(){
        Intent i_login = new Intent(getApplicationContext(), PicLiveActivity.class);
        startActivity(i_login);
        finish();
    }

    public void LogMeIn(View view) {
        if (!edt_username.getText().toString().trim().equals("")) {
            if(edt_password.getText().length() >= 6) {
                new LoginAsyncTask().execute(
                        new UserLoginDTO(
                                edt_username.getText().toString(),
                                edt_password.getText().toString()
                        )
                );
            }//-if
            else{
                edt_password.setError("Password must be at least 6 chars!");
            }//-else
        }//-if
        else {
            edt_username.setError("Username is required!");
        }//-else
    }

    public void OpenRegister(View view){
        Intent i_register = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i_register);
        finish();
    }

    private Response PostLogin(UserLoginDTO User){
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("grant_type", "password")
                    .add("username", User.Username)
                    .add("password", User.Password)
                    .build();
            Request request = new Request.Builder()
                    .url(Globals.URL + "token")
                    .post(formBody)
                    .build();
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
      return null;
    }

    private class LoginAsyncTask extends AsyncTask<UserLoginDTO, Integer, Response> {
        @Override
        protected Response doInBackground(UserLoginDTO... params) {
            // TODO Auto-generated method stub
            return PostLogin(params[0]);
        }

        protected void onPostExecute(Response http_response){
            if(http_response.code() == 200){
                try {
                    if(http_response.body() == null){
                        Toast.makeText(getApplicationContext(), "No Response-Body", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String responseData = http_response.body().string();
                    JSONObject Jobject = new JSONObject(responseData);
                    if(Jobject.getString("access_token") != null && !Objects.equals(Jobject.getString("access_token"), "")){
                        Log.d(INFOTYPE, Jobject.getString("access_token"));
                        TokenSaver.setToken(getApplicationContext(),"bearer " +Jobject.getString("access_token"));
                        Toast.makeText(getApplicationContext(), "Welcome to PicLive!", Toast.LENGTH_SHORT).show();
                        Home();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No Auth-Token", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "FAIL! Not JSON", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(http_response.code() == 400){
                edt_username.setError( "Username or Password incorrect" );
            }
            else{
                Toast.makeText(getApplicationContext(), "FAIL! Something unusual happened", Toast.LENGTH_SHORT).show();
            }
        }

    }

}

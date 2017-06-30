package cakhen.piclive;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cakhen.piclive.models.Globals;
import cakhen.piclive.models.UserRegisterDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Burim Cakolli on 29.06.2017.
 * Class for Register
 */

public class RegisterActivity extends AppCompatActivity {
    public static final MediaType FORM = MediaType.parse("application/json");
    public EditText edt_username;
    public EditText edt_password;
    public EditText edt_confirm_password;
    public AppCompatButton btn_register;
    public TextView txt_toLogin;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_username = (EditText) findViewById(R.id.input_username);
        edt_password = (EditText) findViewById(R.id.input_password);
        edt_confirm_password = (EditText) findViewById(R.id.confirm_input_password);
        btn_register = (AppCompatButton) findViewById(R.id.btn_register);
        txt_toLogin = (TextView) findViewById(R.id.link_login);

        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();

        //Intent intent = getIntent();
    }

    private void Login(){
        Intent i_login = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i_login);
        finish();
    }

    public void BackToLogin(View view){
        Login();
    }

    public void RegistMe (View view){
        if(!edt_username.getText().toString().trim().equals("")){
            if(edt_password.getText().length() >= 6){
                if(edt_password.getText().toString().equals(edt_confirm_password.getText().toString())){
                    new RegisterAsyncTask().execute(
                            new UserRegisterDTO(
                                edt_username.getText().toString(),
                                edt_password.getText().toString(),
                                edt_confirm_password.getText().toString()
                            )
                    );
                }//-if
                else{
                    edt_confirm_password.setError( "Passwords must match" );
                    edt_password.setError( "Passwords must match" );
                }
            }//-if
            else{
                edt_password.setError( "Password must have at least 6 characters" );
            }//-else
        }//-if
        else{
            edt_username.setError( "Username is required!" );
        }//-else
    }

    public Integer PostRegister (UserRegisterDTO NewUser) {
        // Create a new HttpClient and Post Header
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", NewUser.Username);
            jsonObject.put("Password", NewUser.Password);
            jsonObject.put("ConfirmPassword", NewUser.ConfirmPassword);

            RequestBody body = RequestBody.create(FORM, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(Globals.API + "account/register")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.code();
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class RegisterAsyncTask extends AsyncTask<UserRegisterDTO, Integer, Integer> {
        @Override
        protected Integer doInBackground(UserRegisterDTO... params) {
            // TODO Auto-generated method stub
            return PostRegister(params[0]);
        }

        protected void onPostExecute(Integer http_response_code){
            if(http_response_code == 200){
                Toast.makeText(getApplicationContext(), "Registered Successfull", Toast.LENGTH_SHORT).show();
                Login();
            }
            else if(http_response_code == 400){
                edt_username.setError( "Username already given" );
            }
            else{
                Toast.makeText(getApplicationContext(), "FAIL! Something unusual happened", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
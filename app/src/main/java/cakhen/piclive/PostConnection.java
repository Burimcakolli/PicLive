package cakhen.piclive;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Toshiki on 20.06.2017.
 */

public class PostConnection {
    public static final MediaType FORM = MediaType.parse("application/json");

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(200, TimeUnit.SECONDS)
            .writeTimeout(200, TimeUnit.SECONDS)
            .readTimeout(200, TimeUnit.SECONDS)
            .build();

    public void postPicture(String name, byte[] image, double lng, double lat, String city) {

          /*  JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name", name);
            jsonObject.put("Image", image);
            jsonObject.put("Lng", lng);
            jsonObject.put("Lat", lat);
            jsonObject.put("City", city); */


    }

    public void register() throws IOException, JSONException {
        // Create a new HttpClient and Post Header
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Username", "Testuserp");
        jsonObject.put("Password", "Testuserp");
        jsonObject.put("ConfirmPassword", "Testuserp");

        RequestBody body = RequestBody.create(FORM, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url("http://10.4.57.223/PicLive/API/account/register")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }
        System.out.println(response.body().string());
    }
}


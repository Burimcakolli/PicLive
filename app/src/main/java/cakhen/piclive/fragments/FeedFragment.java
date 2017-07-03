package cakhen.piclive.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cakhen.piclive.R;
import cakhen.piclive.models.Globals;
import cakhen.piclive.models.PictureDTO;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    public static final MediaType FORM = MediaType.parse("application/json");
    public List<PictureDTO> FeedList = new ArrayList<PictureDTO>();
    public ProgressBar LoadingCircle;

    private OkHttpClient client;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        LoadingCircle = (ProgressBar) view.findViewById(R.id.loadingCircle);
        client = new OkHttpClient.Builder()
                .connectTimeout(800, TimeUnit.SECONDS)
                .writeTimeout(800, TimeUnit.SECONDS)
                .readTimeout(800, TimeUnit.SECONDS)
                .build();
        new DownloadFeedsTask().execute();
        return view;
    }

    private Response LoadPictures(){
        // Create a new HttpClient and Post Header
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Lat", "47.36417");
            jsonObject.put("Lng", "8.546292");

            RequestBody body = RequestBody.create(FORM, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(Globals.API + "Pictures/Feed")
                    .post(body)
                    .build();
            return client.newCall(request).execute();
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class DownloadFeedsTask extends AsyncTask<Void, Integer, List<PictureDTO>> {
        protected List<PictureDTO> doInBackground(Void... params) {
            Response responses = LoadPictures();
            String jsonData = null;
            try {
                jsonData = responses.body().string();
                Log.d("Feed", jsonData);
                JSONArray jsonArr = new JSONArray(jsonData);
                List<PictureDTO> pictures = new ArrayList<>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    PictureDTO pictureDTO = new PictureDTO(
                            jsonObj.getInt("PicImageId"),
                            jsonObj.getString("Name"),
                            Base64.decode(jsonObj.getString("Name"), Base64.DEFAULT),
                            jsonObj.getDouble("Lng"),
                            jsonObj.getDouble("Lat"),
                            jsonObj.getString("City"),
                            jsonObj.getInt("Likes"),
                            jsonObj.getString("CreationDate")
                    );
                    pictures.add(pictureDTO);
                }
                return pictures;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<PictureDTO> result) {
            LoadingCircle.setVisibility(View.GONE);
            FeedList = result;
            Toast.makeText(getActivity(), "Loading succeed", Toast.LENGTH_SHORT).show();
        }
    }

}

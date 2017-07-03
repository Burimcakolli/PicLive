package cakhen.piclive.models;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cakhen.piclive.ImageActivity;

/**
 * Created by Toshiki on 20.06.2017.
 */

public class Location {
    android.location.Location location;
    Context context;
    public double Lng = 0;
    public double Lat = 0;
    public String City = null;

    public Location(Context context) {
        this.context = context;
        new GetLocationAsyncTask().execute();
    }


    private class GetLocationAsyncTask extends AsyncTask<Void, Void, android.location.Location> {

        protected void onPostExecute(android.location.Location location){
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String _Location = listAddresses.get(0).getLocality();
                    Log.d("Location", _Location);
                    City = _Location;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected android.location.Location doInBackground(Void... params) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // get the last know location from your location manager.

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Lng = location.getLongitude();
            Lat = location.getLongitude();
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }
}




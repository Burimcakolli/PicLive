package cakhen.piclive.models;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Toshiki on 20.06.2017.
 */

public class Location {
    android.location.Location location;
    public Location(Context context) {
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
            return;
        }
        try{
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // now get the lat/lon from the location and do something with it.
            Log.d("TRINN", "IST TRINN");
            Log.d("Ortschaft", location.getLatitude() + " " + location.getLongitude());
        }catch(Exception e){

        }finally{
            getLocationName(location.getLatitude(), location.getLongitude(), context);
        }



    }

    public void getLocationName(double latitude, double longitude, Context context){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                String _Location = listAddresses.get(0).getLocality();
                Log.d("Location", _Location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}




package cakhen.piclive.models;

/**
 * Created by Burim Cakolli on 02.07.2017.
 */

public class LocationDTO {
    public double Lng;
    public double Lat;
    public String City;

    public LocationDTO(double lng, double lat, String city) {
        Lng = lng;
        Lat = lat;
        City = city;
    }
}

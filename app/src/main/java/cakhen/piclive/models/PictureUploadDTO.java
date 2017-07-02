package cakhen.piclive.models;

/**
 * Created by Burim Cakolli on 02.07.2017.
 */

public class PictureUploadDTO {
    public String Name;
    public String Image;
    public double Lng;
    public double Lat;
    public String City;

    public PictureUploadDTO(String name, String image, double lng, double lat, String city) {
        Name = name;
        Image = image;
        Lng = lng;
        Lat = lat;
        City = city;
    }
}

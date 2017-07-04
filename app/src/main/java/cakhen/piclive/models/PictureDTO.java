package cakhen.piclive.models;

import java.util.Date;

/**
 * Created by Burim Cakolli on 02.07.2017.
 */

public class PictureDTO {
    public int PicImageId;
    public String Name;
    public String Image;
    public double Lng;
    public double Lat;
    public String City;
    public int Likes;
    public String CreationDate;

    public PictureDTO(int picImageId, String name, String image, double lng, double lat, String city, int likes, String creationDate) {
        PicImageId = picImageId;
        Name = name;
        Image = image;
        Lng = lng;
        Lat = lat;
        City = city;
        Likes = likes;
        CreationDate = creationDate;
    }
}

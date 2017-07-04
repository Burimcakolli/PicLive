package cakhen.piclive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cakhen.piclive.models.PictureDTO;

/**
 * Created by Burim Cakolli on 04.07.2017.
 */

public class PictureAdapter extends ArrayAdapter<PictureDTO> {

    int layoutResourceId;
    ArrayList<PictureDTO> data = new ArrayList<>();
    LayoutInflater inflater;

    public PictureAdapter(Context context, int textViewResourceId, ArrayList<PictureDTO> objects) {
        super(context, textViewResourceId, objects);
        data = objects;
        layoutResourceId = textViewResourceId;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PictureHolder holder = null;

        if(row == null)
        {
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PictureHolder();
            holder.PicHeaderName = (TextView)row.findViewById(R.id.PicHeaderName);
            holder.PicHeaderDate = (TextView)row.findViewById(R.id.PicHeaderDate);
            holder.PicHeaderOrtschaft = (TextView)row.findViewById(R.id.PicHeaderOrtschaft);
            holder.Image = (ImageView)row.findViewById(R.id.Image);
            holder.ImageLikeText = (TextView)row.findViewById(R.id.ImageLikeText);

            row.setTag(holder);
        }
        else
        {
            holder = (PictureHolder)row.getTag();
        }

        PictureDTO picture = data.get(position);
        holder.PicHeaderName.setText(picture.Name);
        holder.PicHeaderDate.setText(picture.CreationDate.substring(0, 10));
        holder.PicHeaderOrtschaft.setText(picture.City);
        byte[] decodedString = Base64.decode(picture.Image, Base64.NO_WRAP);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.Image.setImageBitmap(decodedByte);
        holder.ImageLikeText.setText(String.valueOf(picture.Likes)+" Gefällt mir Angaben");
        return row;
    }

    static class PictureHolder
    {
        TextView PicHeaderName;
        TextView PicHeaderDate;
        TextView PicHeaderOrtschaft;
        ImageView Image;
        TextView ImageLikeText;
    }
}

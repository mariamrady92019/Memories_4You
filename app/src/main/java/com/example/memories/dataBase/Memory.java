package com.example.memories.dataBase;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.memories.Base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

@Entity
public class Memory  implements Serializable {

    @PrimaryKey(autoGenerate = true )
    int id  ;
    String titlee;
    String description ;
    String date ;
    String time ;
    String stringOfImage;
    double lattitude ;
    double longtude ;
    @Ignore
    Location myLocation;
    @Ignore
    Bitmap image ;

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongtude() {
        return longtude;
    }

    public void setLongtude(double longtude) {
        this.longtude = longtude;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public Memory() {
    }

   @Ignore
    public Memory(String titlee, String description, String date, String time , Bitmap image, Location location) {
        this.titlee = titlee;
        this.description = description;
        this.date = date;
        this.time = time;
        this.image= image;
        this.stringOfImage= BitMapToString(image);
        this.myLocation= location;
        if(location!=null){
        this.lattitude= myLocation.getLatitude();
        this.longtude= myLocation.getLongitude();}
    }
    @Ignore
    public Memory(String titlee, String description, String date, String time,Location location ) {
        this.titlee = titlee;
        this.description = description;
        this.date = date;
        this.time = time;
        stringOfImage = null;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


    public String getStringOfImage() {
        return stringOfImage;
    }

    public void setStringOfImage(String stringOfImage) {
        this.stringOfImage = stringOfImage;
    }

    public int getId() {
        return id;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitlee() {
        return titlee;
    }

    public void setTitlee(String titlee) {
        this.titlee = titlee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

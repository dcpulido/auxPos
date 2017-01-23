package com.example.sirope.posaux;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by sirope on 18/01/17.
 */

public class elem {
    private String id;
    private LatLng pos = new LatLng(0, 0);



    public elem(){
        this.id="";
    }
    public elem(String i,double lat,double lng){
        this.id=i;
        this.pos=new LatLng(lng,lat);
    }

    public String get_id(){
        return this.id;
    }
    public void set_id(String i){
        this.id=i;
    }

    public LatLng get_pos(){
        return this.pos;
    }
    public void set_latlng(LatLng p){
        this.pos=p;
    }
    public void set_Latlng(double lat,double lng){
        this.pos=new LatLng(lat,lng);
    }

    public String toString(){
        return this.id + ": " + this.pos.toString();
    }
}

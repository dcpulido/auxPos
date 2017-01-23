package com.example.sirope.posaux;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sirope on 23/01/17.
 */

public class campo {
    private String name;
    private LatLng ubicacion;
    private int tilt;

    public campo(String name,double lat,double lon,int tilt){
        this.name=name;
        this.ubicacion=new LatLng(lat,lon);
        this.tilt=tilt;
    }
    public String get_name(){
        return this.name;
    }
    public void set_name(String n){
        this.name=n;
    }
    public LatLng get_pos(){
        return this.ubicacion;
    }
    public double get_tilt(){
        return this.tilt;
    }
}

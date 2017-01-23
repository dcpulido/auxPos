
package com.example.sirope.posaux;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mapController extends FragmentActivity implements OnMapReadyCallback {
    private static final LatLng CTAG = new LatLng(42.4, -8.1);
    private static final campo lalingrado=new campo("lalingrado",42.387273,-8.02800,0);
    private GoogleMap mMap;
    private WebSocketClient mWebSocketClient;
    private List<elem> elems = new ArrayList<elem>();
    private GPSTracker gps;
    private List<Marker> markers = new ArrayList<Marker>();
    private String server;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_controller);
        Intent intent = getIntent();
        server = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).addApi(LocationServices.API).build();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(0, 0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        connectWebSocket();
        initRender(mMap);
    }

    public void initRender(GoogleMap googleMap) {

        get_position();
        renderMap(googleMap, (ArrayList<elem>) elems);
    }

    private void renderMap(GoogleMap googleMap, ArrayList<elem> elems) {

        mMap = googleMap;


        for (elem e: elems) {
            Log.i("Websocket",e.toString());
            if(e.get_id()=="self"){
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        //.target(e.get_pos())      // Sets the center of the map to Mountain View
                        .target(lalingrado.get_pos())
                        .zoom(19)                   // Sets the zoom
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            mMap.addMarker(new MarkerOptions().position(e.get_pos()).title(e.get_id()));
        }
        get_position();

    }
    private void get_position(){
        gps = new GPSTracker(mapController.this);
        // check if GPS enabled
        if(gps.canGetLocation()){

            Log.i("Websocket","entra");
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            boolean flag=false;
            for (elem e: elems) {
                if(e.get_id()=="self"){
                    e.set_Latlng(latitude,longitude);
                    flag=true;
                    mWebSocketClient.send(Build.MANUFACTURER + " " + Build.MODEL+"/"+longitude +"/"+ latitude);
                }
            }
            if(flag==false){
                elems.add(new elem("self", latitude, longitude));
                mWebSocketClient.send(Build.MANUFACTURER + " " + Build.MODEL+"/"+longitude +"/"+ latitude);
            }
            Log.i("Websocket", "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
        }else{
            Log.i("Websocket","no entra");
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(server);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Websocket", "Message" + message);
                        String[] separated = message.split(",");
                        boolean flag=false;
                        for (elem e: elems) {
                            if(e.get_id()==separated[0]){
                                e.set_Latlng(Double.parseDouble(separated[2]),Double.parseDouble(separated[1]));
                                flag=true;
                            }
                        }
                        if(flag==false){
                            elems.add(new elem(separated[0], Double.parseDouble(separated[2]), Double.parseDouble(separated[1])));
                        }
                        mMap.clear();
                        renderMap(mMap, (ArrayList<elem>) elems);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
        SystemClock.sleep(3000);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


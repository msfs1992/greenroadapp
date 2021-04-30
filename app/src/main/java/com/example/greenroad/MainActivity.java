package com.example.greenroad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MainActivity extends AppCompatActivity {
    private static final String SOURCE_ID = "SOURCE_ID";
    public Double latitude, longitude;
    private SymbolManager symbolManager;
    public String slatitude, slongitude;
    public MapboxMap mapbox;
    private MarkerView markerView;
    public Boolean markerFlag = false;
    private MarkerViewManager markerViewManager;
    private MapView mapView;
    private String puntos_mapa = "";
    private FusedLocationProviderClient fusedLocationClient;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            setLocation();
// Create a symbol at the specified location.

// Use the manager to draw the symbol.


            /*MarkerView markerView = new MarkerView(new LatLng(latitude,longitude), mapView.getRootView());

            markerViewManager.addMarker(markerView);*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);
        //getActionBar().hide();
        OkHttpClient client = new OkHttpClient();
        String url = "https://greenroad.000webhostapp.com/get-puntos.php";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.d("FABBIANI", myResponse);
                            puntos_mapa = myResponse;
                            loadPoints();
                        }
                    });
                }
            }
        });

       /* URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://greenroad.000webhostapp.com/get-puntos.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Log.d("FABBIANI", in+"");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }*/



        //getActionBar().hide();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //REQUETS PERMISSION
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, 2, mLocationListener);
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        //mLocationManager.getCurrentLocation();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapbox = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Create symbol manager object.
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);


// Set non-data-driven properties.
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconTranslate(new Float[]{-4f, 5f});
                        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        getLocation();

                    }
                });

                /*mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(latitude,longitude))
                        .zoom(10)
                        .build());*/
                //Log.d("LOCATION3", "Latitude:" + getLat() + ", Longitude:" + getLong());

            }
        });

    }

    /*public void onLocationChanged(Location location) {
        Log.d("LOCATION", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }*/
    private void loadPoints(){
        JSONArray mainObject = null;
        try {

                JSONArray jsonArray=new JSONArray(puntos_mapa);
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    //Log.d("FABBIANI4", jsonObject+"");
                    String tipo = jsonObject.getString("tipo");
                    String lat = jsonObject.getString("latitud");
                    String lon = jsonObject.getString("longitud");
                    Double _lat = Double.parseDouble(lat);
                    Double _lon = Double.parseDouble(lon);
                    Log.d("FABBIANI5", tipo);
                    IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                    Icon icon;
                    switch (tipo){
                        case "Pilas":
                            icon = iconFactory.fromResource(R.drawable.flag_red);
                            mapbox.addMarker(new MarkerOptions()
                                    .position(new LatLng(_lat, _lon))
                                    .icon(icon));
                            break;
                        case "Plásticos":
                            icon = iconFactory.fromResource(R.drawable.flag_blue);
                            mapbox.addMarker(new MarkerOptions()
                                    .position(new LatLng(_lat, _lon))
                                    .icon(icon));
                            break;
                        case "Aceites Domiciliarios Usados":
                            icon = iconFactory.fromResource(R.drawable.flag_yellow);
                            mapbox.addMarker(new MarkerOptions()
                                    .position(new LatLng(_lat, _lon))
                                    .icon(icon));
                            break;
                        case "Vidrios":
                            icon = iconFactory.fromResource(R.drawable.flag_green);
                            mapbox.addMarker(new MarkerOptions()
                                    .position(new LatLng(_lat, _lon))
                                    .icon(icon));
                            break;
                        case "Cartón y Papel":
                            icon = iconFactory.fromResource(R.drawable.flag_orange);
                            mapbox.addMarker(new MarkerOptions()
                                    .position(new LatLng(_lat, _lon))
                                    .icon(icon));
                            break;
                        default:
                            break;
                    }



/*                    String X=jsonObject.getString("X");
                    String Y=jsonObject.getString("Y");
                    String Count=jsonObject.getString("Count");

                    JSONArray jsonArraydates=jsonObject.getJSONArray("Dates");

                    for (int j=0;j<jsonArraydates.length();j++)
                    {
                        JSONObject jsonObjectDates=jsonArraydates.getJSONObject(j);


                        String Date=jsonObjectDates.getString("Date");
                        String Time=jsonObjectDates.getString("Time");


                    }*/

                }

            /*JSONObject uniObject = mainObject.getJSONObject("university");
            String  uniName = uniObject.getString("name");
            String uniURL = uniObject.getString("url");

            JSONObject oneObject = mainObject.getJSONObject("1");
            String id = oneObject.getString("id");*/
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("FABBIANI3", e.getMessage());
        }

    }
    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d("UBICACION", location.getLatitude() + "");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            setLocation();
                        }
                    }
                });
    }
    private void setLocation() {

        slatitude = latitude.toString().substring(0, 8);
        slongitude = longitude.toString().substring(0, 8);

        latitude = Double.parseDouble(slatitude);
        longitude = Double.parseDouble(slongitude);
        Log.d("testing", "Latitude:" + latitude + ", Longitude:" + longitude);
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) // Sets the new camera position
                .zoom(14) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder



        if (markerFlag == false) {
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            SymbolOptions symbolOptions = new SymbolOptions()
                    .withLatLng(new LatLng(latitude, longitude))
                    .withIconSize(1.3f);
            symbolManager.create(symbolOptions);
            mapbox.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Me"));

            markerFlag = true;
            mapbox.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 2000);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    public Double getLat(){
        return latitude;
    }
    public Double getLong(){
        return longitude;
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
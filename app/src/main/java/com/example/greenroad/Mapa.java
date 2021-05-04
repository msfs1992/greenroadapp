package com.example.greenroad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.permissions.PermissionsListener;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.turf.TurfMeta;
import com.mapbox.turf.TurfTransformation;
import static com.mapbox.turf.TurfConstants.UNIT_DEGREES;
import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;
import static com.mapbox.turf.TurfConstants.UNIT_MILES;
import static com.mapbox.turf.TurfConstants.UNIT_RADIANS;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class Mapa extends AppCompatActivity implements LocationListener{
    LocationManager mlocationManager;
    String provider;
    private static final String TURF_CALCULATION_FILL_LAYER_GEOJSON_SOURCE_ID
            = "TURF_CALCULATION_FILL_LAYER_GEOJSON_SOURCE_ID";
    private static final String TURF_CALCULATION_FILL_LAYER_ID = "TURF_CALCULATION_FILL_LAYER_ID";
    private static final String CIRCLE_CENTER_SOURCE_ID = "CIRCLE_CENTER_SOURCE_ID";
    private static final String CIRCLE_CENTER_ICON_ID = "CIRCLE_CENTER_ICON_ID";
    private static final String CIRCLE_CENTER_LAYER_ID = "CIRCLE_CENTER_LAYER_ID";
    private static final Point DOWNTOWN_KATHMANDU = Point.fromLngLat(85.323283875, 27.7014884022);
    private static final int RADIUS_SEEKBAR_DIFFERENCE = 1;
    private static final int STEPS_SEEKBAR_DIFFERENCE = 1;
    private static final int STEPS_SEEKBAR_MAX = 360;
    private static final int RADIUS_SEEKBAR_MAX = 100;

    // Min is 4 because LinearRings need to be made up of 4 or more coordinates.
    private static final int MINIMUM_CIRCLE_STEPS = 4;
    private Point lastClickPoint = DOWNTOWN_KATHMANDU;
    private static final String SOURCE_ID = "SOURCE_ID";
    private String circleUnit = UNIT_KILOMETERS;
    private int circleSteps = 180;
    private int circleRadius = 50;
    private PermissionsListener requestPermissionLauncher;
    public Double latitude, longitude;
    private SymbolManager symbolManager;
    public String slatitude, slongitude;
    private static final int REQUEST_LOCATION = 123;
    public MapboxMap mapbox;
    private MarkerView markerView;
    public Boolean markerFlag = false;
    private MarkerViewManager markerViewManager;
    private MapView mapView;
    private String puntos_mapa = "";
    ArrayList<String> myarray = new ArrayList<String>();
    ArrayList<String> selectedPuntos = new ArrayList<String>();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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
    private MapboxMap mapboxMap;

    private void getInfo(){
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
                    Mapa.this.runOnUiThread(new Runnable() {
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
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //{"Vidrios", "Pilas", "Aceites Domiciliarios Usados", "Cartón y papel", "Plásticos"}
        myarray.add("Vidrios");
        myarray.add("Pilas");
        myarray.add("Plásticos");
        myarray.add("Cartón y papel");
        myarray.add("Aceites Domiciliarios Usados");
        selectedPuntos.add("Vidrios");
        selectedPuntos.add("Pilas");
        selectedPuntos.add("Plásticos");
        selectedPuntos.add("Cartón y papel");
        selectedPuntos.add("Aceites Domiciliarios Usados");
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_mapa);
        //getActionBar().hide();
        Log.d("FABBIANI20", myarray+"");
        getInfo();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //REQUETS PERMISSION
            /*ActivityCompat.requestPermissions(this,

                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,

                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, REQUEST_LOCATION);*/
            Log.d("FABBIANI", "Location Service not enabled");
            ActivityCompat.requestPermissions(Mapa.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION);
            return;
        }
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, 2, mLocationListener);
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        //mLocationManager.getCurrentLocation();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        loadMap();


    }

    private void adjustRadius(int string, TextView textView, int progress, int difference) {
        circleRadius = progress;
        final TextView circleRadiusTextView = findViewById(R.id.circle_radius_textview);
        circleRadiusTextView.setText(circleRadius+" km");
        drawPolygonCircle(lastClickPoint);
    }

    private void adjustSteps(int string, TextView textView, int progress, int difference) {
        circleSteps = progress;
        drawPolygonCircle(lastClickPoint);
    }
    /**
     * Use the Turf library {@link TurfTransformation#circle(Point, double, int, String)} method to
     * retrieve a {@link Polygon} .
     *
     * @param centerPoint a {@link Point} which the circle will center around
     * @param radius the radius of the circle
     * @param steps  number of steps which make up the circle parameter
     * @param units  one of the units found inside {@link com.mapbox.turf.TurfConstants}
     * @return a {@link Polygon} which represents the newly created circle
     */
    private Polygon getTurfPolygon(@NonNull Point centerPoint, @NonNull double radius,
                                   @NonNull int steps, @NonNull String units) {
        return TurfTransformation.circle(centerPoint, radius, steps, units);
    }
    private void initPolygonCircleFillLayer() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
// Create and style a FillLayer based on information that will come from the Turf calculation
                FillLayer fillLayer = new FillLayer(TURF_CALCULATION_FILL_LAYER_ID,
                        TURF_CALCULATION_FILL_LAYER_GEOJSON_SOURCE_ID);
                fillLayer.setProperties(
                        fillColor(Color.parseColor("#f5425d")),
                        fillOpacity(.7f));
                style.addLayerBelow(fillLayer, CIRCLE_CENTER_LAYER_ID);
            }
        });
    }
    private void drawPolygonCircle(final Point circleCenter) {
        mapbox.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

// Use Turf to calculate the Polygon's coordinates

                Polygon polygonArea = getTurfPolygon(circleCenter, circleRadius, circleSteps, circleUnit);
                GeoJsonSource polygonCircleSource = style.getSourceAs(TURF_CALCULATION_FILL_LAYER_GEOJSON_SOURCE_ID);
                if (polygonCircleSource != null) {
                    polygonCircleSource.setGeoJson(Polygon.fromOuterInner(
                            LineString.fromLngLats(TurfMeta.coordAll(polygonArea, false))));
                    //RDW
                    mapbox.clear();
                    loadPoints();
                }
            }
        });
    }
    /*private void initDistanceUnitSpinner() {
        Spinner spinner = findViewById(R.id.circle_units_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.polygon_circle_transformation_circle_distance_units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }*/
    private void moveCircleCenterMarker(final Point circleCenter) {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
// Use Turf to calculate the Polygon's coordinates
                GeoJsonSource markerSource = style.getSourceAs(CIRCLE_CENTER_SOURCE_ID);
                if (markerSource != null) {
                    markerSource.setGeoJson(circleCenter);
                }
            }
        });
    }
    public void loadMap(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapbox = mapboxMap;
                mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS)
                        .withImage(CIRCLE_CENTER_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.mapbox_marker_icon_default)))
                        .withSource(new GeoJsonSource(CIRCLE_CENTER_SOURCE_ID,
                                Feature.fromGeometry(DOWNTOWN_KATHMANDU)))
                        .withSource(new GeoJsonSource(TURF_CALCULATION_FILL_LAYER_GEOJSON_SOURCE_ID))
                        .withLayer(new SymbolLayer(CIRCLE_CENTER_LAYER_ID,
                                CIRCLE_CENTER_SOURCE_ID).withProperties(
                                iconImage(CIRCLE_CENTER_ICON_ID),
                                iconIgnorePlacement(true),
                                iconAllowOverlap(true),
                                iconOffset(new Float[] {0f, -4f})
                        )), new Style.OnStyleLoaded() {
                    @SuppressLint("StringFormatMatches")
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        Mapa.this.mapboxMap = mapboxMap;
                        symbolManager = new SymbolManager(mapView, Mapa.this.mapboxMap, style);


                        // Set non-data-driven properties.
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconTranslate(new Float[]{-4f, 5f});
                        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
                        initPolygonCircleFillLayer();


                        final SeekBar circleRadiusSeekbar = findViewById(R.id.circle_radius_seekbar);
                        circleRadiusSeekbar.setMax(RADIUS_SEEKBAR_MAX + RADIUS_SEEKBAR_DIFFERENCE);
                        circleRadiusSeekbar.incrementProgressBy(RADIUS_SEEKBAR_DIFFERENCE);
                        circleRadiusSeekbar.setProgress(50);



                        final TextView circleRadiusTextView = findViewById(R.id.circle_radius_textview);
                        circleRadiusTextView.setText(circleRadiusSeekbar.getProgress()+" km");
                        getLocation();

                        /*lastClickPoint = Point.fromLngLat(longitude, latitude);
                        moveCircleCenterMarker(lastClickPoint);
                        drawPolygonCircle(lastClickPoint);*/


                        circleRadiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                adjustRadius(R.string.polygon_circle_transformation_circle_radius, circleRadiusTextView,
                                        seekBar.getProgress(), STEPS_SEEKBAR_DIFFERENCE);


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
// Not needed in this example.
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                adjustRadius(R.string.polygon_circle_transformation_circle_radius, circleRadiusTextView,
                                        seekBar.getProgress(), STEPS_SEEKBAR_DIFFERENCE);
                            }
                        });

                    }
                });
                /*mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Create symbol manager object.
                        symbolManager = new SymbolManager(mapView, mapbox, style);


                        // Set non-data-driven properties.
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconTranslate(new Float[]{-4f, 5f});
                        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        getLocation();

                    }
                });*/

                /*mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(latitude,longitude))
                        .zoom(10)
                        .build());*/
                //Log.d("LOCATION3", "Latitude:" + getLat() + ", Longitude:" + getLong());

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(Mapa.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Mapa.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /*public void onLocationChanged(Location location) {
        Log.d("LOCATION", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }*/
    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit=="K") { dist = dist * 1.609344; }
        if (unit=="N") { dist = dist * 0.8684; }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    public static String getId(View view) {
        if (view.getId() == View.NO_ID) return "no-id";
        else return view.getResources().getResourceName(view.getId());
    }
    public ArrayList<String> popStringArray(String popItem){
        ArrayList<String> tmp = new ArrayList<String>();
        for (int i = 0, j = 0; i < selectedPuntos.size(); i++) {
            if(popItem != selectedPuntos.get(i)){
                tmp.add(selectedPuntos.get(i));
            }
        }
        Log.d("FABBIANI25", tmp+"");
        return tmp;
    };
    public void checkClicked(View v){
        CheckBox checkBox = (CheckBox)v;
        int _id = v.getId();
        Log.d("FABBIANI26", _id+"");
        switch (_id){
            case R.id.cartonCheck:
                if(checkBox.isChecked()){
                    selectedPuntos.add("Cartón y papel");
                }else{
                    selectedPuntos = popStringArray("Cartón y papel");
                }
                break;
            case R.id.pilasCheck:
                if(checkBox.isChecked()){
                    selectedPuntos.add("Pilas");
                }else{
                    selectedPuntos = popStringArray("Pilas");
                }
                break;
            case R.id.plasticosCheck:
                if(checkBox.isChecked()){
                    selectedPuntos.add("Plásticos");
                }else{
                    selectedPuntos = popStringArray("Plásticos");
                }
                break;
            case R.id.aceitesCheck:
                if(checkBox.isChecked()){
                    selectedPuntos.add("Aceites Domiciliarios Usados");
                }else{
                    selectedPuntos = popStringArray("Aceites Domiciliarios Usados");
                }
                break;
            case R.id.vidriosCheck:
                if(checkBox.isChecked()){
                    selectedPuntos.add("Vidrios");
                }else{
                    selectedPuntos = popStringArray("Vidrios");
                }
                break;
            default:
                break;
        }
        mapbox.clear();
        loadPoints();
    }
    private void loadPoints(){
        //Log.d("FABBIANI11", circleRadius+"");
        Double _cradius = Double.parseDouble(circleRadius+"");
        //Log.d("FABBIANI12", circleRadius+"");
        Log.d("FABBIANI27", selectedPuntos+"");
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
                String dir = jsonObject.getString("dir");
                Double _lat = Double.parseDouble(lat);
                Double _lon = Double.parseDouble(lon);
                Double dist = distance(latitude, longitude, _lat, _lon, "k");
                String doubleAsString = String.valueOf(dist);
                int indexOfDecimal = doubleAsString.indexOf(".");
                int point_distance = Integer.parseInt(doubleAsString.substring(0, indexOfDecimal));
                //String _dist = dist+"";
                //String[] split = _dist.split(".");
                //Log.d("FABBIANI7", doubleAsString.substring(0, indexOfDecimal)+"");
                //Integer mdist = (Integer) split[0];
                if(point_distance > circleRadius){
                    //Log.d("FABBIANI6", 1+"");
                    continue;
                }
                //Log.d("FABBIANI666", tipo);
                //Log.d("FABBIANI667", selectedPuntos+"");
                Boolean allowTipo = false;
                for(int p = 0; p < selectedPuntos.size(); p++){

                    if(tipo.equals(selectedPuntos.get(p))){

                        allowTipo = true;
                    }
                }
                if(!allowTipo){
                    continue;
                }
                //Log.d("FABBIANI5", dist+"");
                IconFactory iconFactory = IconFactory.getInstance(Mapa.this);
                Icon icon;
                switch (tipo){
                    case "Pilas":
                        icon = iconFactory.fromResource(R.drawable.flag_red);
                        mapbox.addMarker(new MarkerOptions()
                                .position(new LatLng(_lat, _lon))
                                .title(dir)
                                .icon(icon));
                        break;
                    case "Plásticos":
                        icon = iconFactory.fromResource(R.drawable.flag_blue);
                        mapbox.addMarker(new MarkerOptions()
                                .position(new LatLng(_lat, _lon))
                                .title(dir)
                                .icon(icon));
                        break;
                    case "Aceites Domiciliarios Usados":
                        icon = iconFactory.fromResource(R.drawable.flag_yellow);
                        mapbox.addMarker(new MarkerOptions()
                                .position(new LatLng(_lat, _lon))
                                .title(dir)
                                .icon(icon));
                        break;
                    case "Vidrios":
                        icon = iconFactory.fromResource(R.drawable.flag_green);
                        mapbox.addMarker(new MarkerOptions()
                                .position(new LatLng(_lat, _lon))
                                .title(dir)
                                .icon(icon));
                        break;
                    case "Cartón y Papel":
                        icon = iconFactory.fromResource(R.drawable.flag_orange);
                        mapbox.addMarker(new MarkerOptions()
                                .position(new LatLng(_lat, _lon))
                                .title(dir)
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
        if (ActivityCompat.checkSelfPermission(Mapa.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(Mapa.this, new OnSuccessListener<Location>() {
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
                .zoom(8) // Sets the zoom
                .tilt(20) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder



        if (markerFlag == false) {
            /*symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            SymbolOptions symbolOptions = new SymbolOptions()
                    .withLatLng(new LatLng(latitude, longitude))
                    .withIconSize(1.3f);
            symbolManager.create(symbolOptions);*/
            /*mapbox.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Me"));*/
            lastClickPoint = Point.fromLngLat(longitude, latitude);
            moveCircleCenterMarker(lastClickPoint);
            drawPolygonCircle(lastClickPoint);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
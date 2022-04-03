package com.example.androidgps;

import androidx.fragment.app.FragmentActivity;

import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.androidgps.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gpx.Parser;
import gpx.*;
import pdr.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SensorManager sensorManager;
    private StepDetectionHandler stepDetectionHandler;
    private DeviceAttitudeHandler deviceAttitudeHandler;
    private StepPositionHandler stepPositionHandler;
    private Marker marker;
    private Polyline polyline;
    private List<LatLng> arrayPosition;
    private List<LatLng> arrayPositionSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(getApplication().SENSOR_SERVICE);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

      mMap.setOnMapClickListener(location -> {
        if(marker != null) {
          marker.remove();
        }
        arrayPosition = new ArrayList<>();
        stepPositionHandler = new StepPositionHandler(new LatLng(location.latitude, location.longitude));
        MarkerOptions monMarker = new MarkerOptions().position(stepPositionHandler.getmCurrentLocation()).title("Grenoble");
        marker = mMap.addMarker(monMarker);
        stepDetectionHandler = new StepDetectionHandler(sensorManager);
        stepDetectionHandler.start();
        stepDetectionHandler.setListener(new StepDetectionHandler.ChangeListener() {
          @Override
          public void onChange() {
            if(polyline != null){
              polyline.remove();
            }
            PolylineOptions polylineOptions = new PolylineOptions(). clickable(true);
            stepPositionHandler.computNextStep(0.0008, deviceAttitudeHandler.getYaw());
            arrayPosition.add(stepPositionHandler.getmCurrentLocation());
            for(int i = 0; i < arrayPosition.size(); i++){
              System.out.println("////////// + " + arrayPosition.get(i).toString());
              polylineOptions.add((LatLng) arrayPosition.get(i)).color(Color.RED);
            }
            polyline = mMap.addPolyline(polylineOptions);
            System.out.println("//testAttitude + " + deviceAttitudeHandler.getYaw());
            System.out.println("//test + " + stepDetectionHandler.getTestTempValue());
          }
        });
      });


      deviceAttitudeHandler = new DeviceAttitudeHandler(sensorManager);

      LatLng grenoble = new LatLng(45.18829933050879, 5.719882817439779);
        mMap.addMarker(new MarkerOptions().position(grenoble).title("Grenoble"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(grenoble));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        //polyline
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .add(
                        new LatLng(45.189, 5.704),
                        new LatLng(45.188, 5.725),
                        new LatLng(45.191, 5.733)));

        polyline1.setTag("A");
        polyline1.setColor(0xffFF0000);

        //polyline 2.5
        try {
            InputStream is = getAssets().open("bikeandrun.gpx");
            try {
                GPX gpx = Parser.parse(is);
                PolylineOptions polyoption = new PolylineOptions();
                for (Track trk : gpx.getTracks()) {
                    for (TrackSeg trkseg : trk.getTrackSegs()) {
                          Log.i("seg", "trkseg");
                        for (TrackPoint trkpt : trkseg.getTrackPoints()) {

                            polyoption.add(new LatLng( trkpt.getLatitude() ,trkpt.getLongitude()));
                        }
                    mMap.addPolyline((polyoption));
                    }
                }
            }
            catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {e.printStackTrace();}




    }

}

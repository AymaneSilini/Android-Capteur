package com.example.androidgps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import gpx.Parser;
import gpx.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("test", "test1");

        try {
            InputStream is = getAssets().open("bikeandrun.gpx");
            try {
                GPX gpx = Parser.parse(is);

                for (Track trk : gpx.getTracks()) {
                    Log.i("trk", "trk");
                    for (TrackSeg trkseg : trk.getTrackSegs()) {
                        for (TrackPoint trkpt : trkseg.getTrackPoints()) {
                            Log.i("POINT", Double.toString(trkpt.getLatitude()) + "/" +
                                    Double.toString(trkpt.getLongitude()));
                        }
                    }
                }
            }
            catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {e.printStackTrace();}





    }

    public void showMap(View v){
        startActivity(new Intent(getApplicationContext(), com.example.androidgps.MapsActivity.class));
    }
}
// clé api : AIzaSyCntVccEorDqnUfkU3T4OoFxvSJU8gM1Ho

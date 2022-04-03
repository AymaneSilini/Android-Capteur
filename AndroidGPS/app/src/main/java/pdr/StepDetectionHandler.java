package pdr;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidgps.R;

import java.util.Vector;

public class StepDetectionHandler extends AppCompatActivity  implements SensorEventListener {
  SensorManager sensorManager;
  Sensor accelerometre;
  Float stepSize;
  StepDetectionListener stepDetectionListener;
  private ChangeListener listener;
  Vector<Float> averageCalc = new Vector<>();
  private float testTempValue;


  public interface StepDetectionListener{public void newStepDetected(float stepSize);}
  public void setStepDetectionListener(StepDetectionListener stepDetectionListener) {
    this.stepDetectionListener = stepDetectionListener; }

  public StepDetectionHandler(SensorManager sensorManager) {
    this.sensorManager = sensorManager;
    accelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
  }

  public void start() { sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_UI); }

  public void stop() {sensorManager.unregisterListener(this,accelerometre);  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    if (accelerometre == null) throw new RuntimeException("Sensor is null !!");
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    Log.d("accelerator", "X : " + Float.toString(x) + "\n\n" + "Y : " + Float.toString(y)
      + "\n\n" + "Z " + Float.toString(z));

    if (event.sensor.getType() == accelerometre.getType()){
      y = event.values[1];
      float average = getAverage(y);
      if (average > 1.2) {
        if (listener != null) listener.onChange();
      };
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {}


  public float getAverage( float value ) {
    float tempValue = 0;
    averageCalc.add(value);
    if (averageCalc.size() >= 4) averageCalc.removeElementAt(0);
    for (float lastValue : averageCalc) tempValue += lastValue;
    testTempValue = tempValue/averageCalc.size();
    return tempValue / averageCalc.size();
  }



  public ChangeListener getListener() {
    return listener;
  }

  public void setListener(ChangeListener listener) {
    this.listener = listener;
  }

  public interface ChangeListener {
    void onChange();
  }

  public float getTestTempValue() {
    return testTempValue;
  }




}

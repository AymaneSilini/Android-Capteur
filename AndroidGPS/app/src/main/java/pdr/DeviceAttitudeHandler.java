package pdr;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceAttitudeHandler extends AppCompatActivity implements SensorEventListener {

  private SensorManager sensorManager;
  private Sensor accelerometer;
  private Sensor magnetometer;

  private float[] accelero;
  private float[] geomagnetic;

  private float yaw;
  private float pitch;
  private float roll;

  public static final float TWENTY_FIVE_DEGREE_IN_RADIAN = 0.436332313f;
  public static final float ONE_FIFTY_FIVE_DEGREE_IN_RADIAN = 2.7052603f;

  public DeviceAttitudeHandler(SensorManager sensorManager){
    this.sensorManager = sensorManager;
    accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    magnetometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    start();
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
      accelero = event.values.clone();
    System.out.println("//Accelero + " + accelero);
    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
      geomagnetic = event.values.clone();
    System.out.println("//Geo + " + geomagnetic);
    if (accelero != null && geomagnetic != null) {
      float R[] = new float[9];
      float I[] = new float[9];
      boolean success = SensorManager.getRotationMatrix(R, I, accelero, geomagnetic); // stocke dans R une matrice de vecteur
      if (success) {
        float orientationData[] = new float[3];
        float inclination = (float) Math.acos(R[8]);
        if (inclination < TWENTY_FIVE_DEGREE_IN_RADIAN
          || inclination > ONE_FIFTY_FIVE_DEGREE_IN_RADIAN)
        {
          SensorManager.getOrientation(R, orientationData);
        } else {
          this.sensorManager.remapCoordinateSystem(R,SensorManager.AXIS_X, SensorManager.AXIS_Z,R); // rÃ©organise la matrice de vecteur
          SensorManager.getOrientation(R, orientationData);
        }
        yaw = orientationData[0];
        pitch = orientationData[1];
        roll = orientationData[2];

        System.out.println("//Yaw1 + " + yaw);
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  public double getYaw() {
    System.out.println("//Yaw + " + yaw);
    double degrees = Math.toDegrees(yaw);
    System.out.println("//Yaw2 + " + yaw);
    if(degrees < 0){
      degrees = degrees + 360;
    }
    System.out.println("//Calcul + " + yaw + Math.round(degrees * 1)/1+" : "+pitch+" : "+roll);
    return degrees;
  }

  public void start(){
    this.sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    this.sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
  }

  public void stop(){
    this.sensorManager.unregisterListener(this);
  }
}

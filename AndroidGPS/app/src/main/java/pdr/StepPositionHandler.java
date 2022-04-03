package pdr;

import com.google.android.gms.maps.model.LatLng;

public class StepPositionHandler {
  private LatLng mCurrentLocation;
  private LatLng latLng;

  public StepPositionHandler(LatLng latLng) {
    this.mCurrentLocation = latLng;
  }

  public void computNextStep(double stepSize, double bearin) {
    double bearing = Math.toRadians(bearin);
    double latitude1 = Math.toRadians(mCurrentLocation.latitude);
    double longitude1 = Math.toRadians(mCurrentLocation.longitude);
    double dist = stepSize;
    dist = dist / 6371;

    double latitude2 = Math.asin(Math.sin(latitude1) * Math.cos(dist) +
      Math.cos(latitude1) * Math.sin(dist) * Math.cos(bearing));
    double longitude2 = longitude1 + Math.atan2(Math.sin(bearing) * Math.sin(dist) *
        Math.cos(latitude1),
      Math.cos(dist) - Math.sin(latitude1) *
        Math.sin(latitude2));

    mCurrentLocation = new LatLng(Math.toDegrees(latitude2),Math.toDegrees(longitude2));

  }

  public LatLng getmCurrentLocation() {
    return mCurrentLocation;
  }

  public void setmCurrentLocation(LatLng mCurrentLocation) {
    this.mCurrentLocation = mCurrentLocation;
  }

  public LatLng getLatLng() {
    return latLng;
  }
}

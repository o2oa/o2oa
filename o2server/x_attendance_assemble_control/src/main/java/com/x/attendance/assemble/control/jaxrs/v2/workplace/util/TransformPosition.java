package com.x.attendance.assemble.control.jaxrs.v2.workplace.util;

public class TransformPosition {
  
  private double lng;
  private double lat;

  public TransformPosition() {}

  public TransformPosition(double lng, double lat) {
    this.lng = lng;
    this.lat = lat;
  }


  public double getLng() {
    return lng;
  }
  public void setLng(double lng) {
    this.lng = lng;
  }
  public double getLat() {
    return lat;
  }
  public void setLat(double lat) {
    this.lat = lat;
  }

  
}

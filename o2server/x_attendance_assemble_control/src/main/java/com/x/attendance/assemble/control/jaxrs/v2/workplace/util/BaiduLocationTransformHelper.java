package com.x.attendance.assemble.control.jaxrs.v2.workplace.util;

import java.lang.Math;

public class BaiduLocationTransformHelper {

  public static double pi = 3.1415926535897932384626;
  public static double a = 6378245.0;
  public static double ee = 0.00669342162296594323;

  // 百度坐标转 gps
  public static TransformPosition bd09towgs84(double bdLng, double bdLat) {
    TransformPosition gcj02 = bd09togcj02(bdLng, bdLat);
    return gcj02towgs84(gcj02.getLng(), gcj02.getLat());
  }

  // 百度坐标转火星坐标
  public static TransformPosition bd09togcj02(double bdLng, double bdLat) {
    double xPi = 3.14159265358979324 * 3000.0 / 180.0;
    double x = bdLng - 0.0065;
    double y = bdLat - 0.006;
    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * xPi);
    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * xPi);
    double ggLng = z * Math.cos(theta);
    double ggLat = z * Math.sin(theta);
    return new TransformPosition(ggLng, ggLat);
  }

  // 火星转GPS
  public static TransformPosition gcj02towgs84(double lng, double lat) {
    if (_outOfChina(lng, lat)) {
      return new TransformPosition(lng, lat);
    } else {
      double dlat = _transformlat(lng - 105.0, lat - 35.0);
      double dlng = _transformlng(lng - 105.0, lat - 35.0);
      double radlat = lat / 180.0 * pi;
      double magic = Math.sin(radlat);
      magic = 1 - ee * magic * magic;
      double sqrtmagic = Math.sqrt(magic);
      dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * pi);
      dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * pi);
      double mglat = lat + dlat;
      double mglng = lng + dlng;
      // return [lng * 2 - mglng, lat * 2 - mglat];
      return new TransformPosition(lng * 2 - mglng, lat * 2 - mglat);
    }
  }

  public static boolean _outOfChina(double lng, double lat) {
    return (lng < 72.004 || lng > 137.8347 || ((lat < 0.8293 || lat > 55.8271) || false));
  }

  public static double _transformlat(double lng, double lat) {
    double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
    ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
    ret += (160.0 * Math.sin(lat / 12.0 * pi) + 320 * Math.sin(lat * pi / 30.0)) * 2.0 / 3.0;
    return ret;
  }

  public static double _transformlng(double lng, double lat) {
    double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
    ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lng * pi) + 40.0 * Math.sin(lng / 3.0 * pi)) * 2.0 / 3.0;
    ret += (150.0 * Math.sin(lng / 12.0 * pi) + 300.0 * Math.sin(lng / 30.0 * pi)) * 2.0 / 3.0;
    return ret;
  }

}

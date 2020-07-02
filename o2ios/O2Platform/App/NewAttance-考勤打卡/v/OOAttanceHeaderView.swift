//
//  OOAttanceHeaderView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/15.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class OOAttanceHeaderView: UIView {
    
    var mapView:BMKMapView!
    
    var userLocation:BMKUserLocation!
    
    var locService: BMKLocationManager!
    
    var searchAddress:BMKGeoCodeSearch!
    
    var annotations:[BMKPointAnnotation] = []
    
    var workPlaces:[OOAttandanceWorkPlace]? {
        didSet {
            setAnnotations()
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }
    
    func commonInit() {
        mapView = BMKMapView(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 280))
        
        mapView.showsUserLocation = true
        mapView.isSelectedAnnotationViewFront = true
        mapView.showMapScaleBar = true
        mapView.showMapPoi = true
        mapView.showIndoorMapPoi = true
        mapView.zoomLevel = 19
        //mapView.setCompassImage(UIImage(named: "icon_dingwei2"))
        mapView.logoPosition = BMKLogoPositionRightBottom
        mapView.userTrackingMode = BMKUserTrackingModeNone
        self.backgroundColor = UIColor.white
        self.addSubview(mapView)
        
    }
    
    func startBMKMapViewService(){
        
        mapView.delegate = self
        
        locService =  BMKLocationManager()
        locService.desiredAccuracy = kCLLocationAccuracyBest
        //设置返回位置的坐标系类型
        locService.coordinateType = .BMK09LL
        //设置距离过滤参数
        locService.distanceFilter = kCLDistanceFilterNone;
        //设置预期精度参数
        locService.desiredAccuracy = kCLLocationAccuracyBest;
        //设置应用位置类型
        locService.activityType = .automotiveNavigation
        //设置是否自动停止位置更新
        locService.pausesLocationUpdatesAutomatically = false
        
        locService.delegate = self
        locService.startUpdatingLocation()
      
        searchAddress = BMKGeoCodeSearch()
        searchAddress.delegate = self
      
    }
    
    // MARK:- 计算所有位置是否有一个位置在误差范围内
    func calcErrorRange(_ checkinLocation:CLLocationCoordinate2D) -> Bool {
        guard let myWorkPlaces = workPlaces else {
            return false
        }
        let currentLocation = checkinLocation
        var result = false
        for item in myWorkPlaces {
            let longitude  = Double((item.longitude)!)
            let latitude  = Double((item.latitude)!)
            let eRange = item.errorRange!
            let theLocation = CLLocationCoordinate2DMake(latitude!,longitude!)
            result = BMKCircleContainsCoordinate(theLocation,currentLocation,Double(eRange))
            if result == true {
                break
            }
        }
        return result
    }
    
    
    // MARK:- 设置所有位置标注点
    func setAnnotations() {
        workPlaces?.forEach({ (workPlace) in
            self.setAnnotation(workPlace)
        })
        mapView.addAnnotations(annotations)
    }
    
    private func setAnnotation(_ workPlace:OOAttandanceWorkPlace) {
        let annotation = BMKPointAnnotation()
        let longitude  = Double((workPlace.longitude)!)
        let latitude  = Double((workPlace.latitude)!)
        DDLogDebug("placeAlias=\(workPlace.placeAlias ?? ""),longitude=\(String(describing: longitude)),latitude=\(latitude)")
        annotation.coordinate = CLLocationCoordinate2DMake(latitude!,longitude!);
        annotation.title = workPlace.placeAlias ?? ""
        annotation.subtitle = workPlace.placeName ?? ""
        annotations.append(annotation)
    }
    
    func stopBMKMapViewService() {
        locService.stopUpdatingLocation()
        locService.delegate = nil
        searchAddress.delegate = nil
        mapView.delegate = nil
    }

}

// MARK: - BMKMapViewDelegate
extension OOAttanceHeaderView:BMKMapViewDelegate {
    
    func mapView(_ mapView: BMKMapView!, viewFor annotation: BMKAnnotation!) -> BMKAnnotationView! {
        let reuseIdentifier = "myAnnotaionView"
        if annotation.isKind(of: BMKPointAnnotation.self) {
            var newAnnotationView = mapView.dequeueReusableAnnotationView(withIdentifier: reuseIdentifier)
            if newAnnotationView == nil {
                newAnnotationView = BMKPinAnnotationView(annotation: annotation, reuseIdentifier: reuseIdentifier)
            }
            newAnnotationView?.image = UIImage(named:"icon_dingwei2")
            return newAnnotationView
        }
        return nil
    }

    func mapViewDidFinishLoading(_ mapView: BMKMapView!) {
        DDLogDebug("mapViewDidFinishLoading")
    }
    
    func mapViewDidFinishRendering(_ mapView: BMKMapView!) {
        DDLogDebug("mapViewDidFinishRendering")
    }
}

extension OOAttanceHeaderView: BMKLocationManagerDelegate {
    
    func bmkLocationManager(_ manager: BMKLocationManager, didUpdate location: BMKLocation?, orError error: Error?) {
        if let loc = location?.location {
            DDLogDebug("当前位置,\(loc.coordinate.latitude),\(loc.coordinate.longitude)")
            let user = BMKUserLocation()
            user.location = loc
            mapView.updateLocationData(user)
            mapView.centerCoordinate = CLLocationCoordinate2D(latitude: loc.coordinate.latitude, longitude: loc.coordinate.longitude)
            //搜索到指定的地点
            let re = BMKReverseGeoCodeSearchOption()
            re.location = CLLocationCoordinate2D(latitude: loc.coordinate.latitude, longitude: loc.coordinate.longitude)
            let _ = searchAddress.reverseGeoCode(re)
        }else {
            DDLogError("没有获取到定位信息！！！！！")
        }
    }
    
   
}

extension OOAttanceHeaderView:BMKGeoCodeSearchDelegate {
    
    func onGetReverseGeoCodeResult(_ searcher: BMKGeoCodeSearch?, result: BMKReverseGeoCodeSearchResult?, errorCode error: BMKSearchErrorCode) {
        //发送定位的实时位置及名称信息
        if let location = result?.location, calcErrorRange(location) == true {
            NotificationCenter.post(customeNotification: .location, object: result)
        }else{
              NotificationCenter.post(customeNotification: .location, object: nil)
        }
    }
    
    func onGetGeoCodeResult(_ searcher: BMKGeoCodeSearch!, result: BMKGeoCodeSearchResult!, errorCode error: BMKSearchErrorCode) {
        if Int(error.rawValue) == 0 {
            DDLogDebug("result \(String(describing: result))")
        }else{
            DDLogDebug("result error  errorCode = \(Int(error.rawValue))")
        }
        
    }
}

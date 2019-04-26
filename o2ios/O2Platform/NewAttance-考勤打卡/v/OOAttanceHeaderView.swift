//
//  OOAttanceHeaderView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/15.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOAttanceHeaderView: UIView {
    
    var mapView:BMKMapView!
    
    var userLocation:BMKUserLocation!
    
    var locService:BMKLocationService!
    
    var searchAddress:BMKGeoCodeSearch!
    
    var annotations:[BMKPointAnnotation] = []
    
    var workPlaces:[OOAttandanceWorkPlace]? {
        didSet {
            setAnnotations()
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        //commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        //commonInit()
    }
    
    func startBMKMapViewService(){
        mapView = BMKMapView(frame: CGRect(x: 0, y: 0, w: SCREEN_WIDTH, h: 280))
        mapView.delegate = self
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
        
        
        locService = BMKLocationService()
        locService.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        locService.delegate = self
        locService.startUserLocationService()
      
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
        O2Logger.debug("placeAlias=\(workPlace.placeAlias ?? ""),longitude=\(longitude),latitude=\(latitude)")
        annotation.coordinate = CLLocationCoordinate2DMake(latitude!,longitude!);
        annotation.title = workPlace.placeAlias ?? ""
        annotation.subtitle = workPlace.placeName ?? ""
        annotations.append(annotation)
    }
    
    func stopBMKMapViewService() {
        locService.stopUserLocationService()
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
        O2Logger.debug("mapViewDidFinishLoading")
    }
    
    func mapViewDidFinishRendering(_ mapView: BMKMapView!) {
        O2Logger.debug("mapViewDidFinishRendering")
    }
}

extension OOAttanceHeaderView:BMKLocationServiceDelegate {
    
    
    func didUpdate(_ userLocation: BMKUserLocation!) {
        O2Logger.debug("当前位置,\(userLocation.location.coordinate.latitude),\(userLocation.location.coordinate.longitude)")
        mapView.updateLocationData(userLocation)
        mapView.centerCoordinate = userLocation.location.coordinate
        //搜索到指定的地点
        let re = BMKReverseGeoCodeOption()
        re.reverseGeoPoint = userLocation.location.coordinate
        let _ = searchAddress.reverseGeoCode(re)

    }
}

extension OOAttanceHeaderView:BMKGeoCodeSearchDelegate {
    
    func onGetReverseGeoCodeResult(_ searcher: BMKGeoCodeSearch?, result: BMKReverseGeoCodeResult?, errorCode error: BMKSearchErrorCode) {
        //发送定位的实时位置及名称信息
        if let location = result?.location, calcErrorRange(location) == true {
            NotificationCenter.post(customeNotification: .location, object: result)
        }else{
              NotificationCenter.post(customeNotification: .location, object: nil)
        }
    }
    
    func onGetGeoCodeResult(_ searcher: BMKGeoCodeSearch!, result: BMKGeoCodeResult!, errorCode error: BMKSearchErrorCode) {
        if Int(error.rawValue) == 0 {
            O2Logger.debug("result \(String(describing: result.address))")
        }else{
            O2Logger.debug("result error  errorCode = \(Int(error.rawValue))")
        }
        
    }
}

//
//  IMShowLocationViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/18.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class IMShowLocationViewController: UIViewController {

    private var mapView: BMKMapView!
    private var annotation: BMKPointAnnotation!
    
    var address: String?
    var addressDetail: String?
    var latitude: Double?
    var longitude: Double?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = address
        mapView = BMKMapView(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH ,height: SCREEN_HEIGHT))
        mapView.zoomLevel = 17
        mapView.showMapPoi = true
        mapView.showIndoorMapPoi = true
        self.view.addSubview(mapView)
       
        guard let lat = latitude, let lo = longitude else {
            self.showError(title: "没有传入正确的地址参数！")
            return
        }
        //更新地图位置
        let user = BMKUserLocation()
        let loc = CLLocation(latitude: lat, longitude: lo)
        user.location = loc
        mapView.updateLocationData(user)
        mapView.centerCoordinate = CLLocationCoordinate2D(latitude: loc.coordinate.latitude, longitude: loc.coordinate.longitude)
        //设置位置点
        annotation = BMKPointAnnotation()
        annotation.coordinate = loc.coordinate
        annotation.title = address
        annotation.subtitle = addressDetail
        mapView.addAnnotation(annotation)
        
        
    }
    
 
}

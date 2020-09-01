//
//  IMShowLocationViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/18.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import MapKit
import CocoaLumberjack

class IMShowLocationViewController: UIViewController {
    
    
    // MARK: - 在地图上展现一个位置，这里的经纬度是百度的经纬度
    public static func pushShowLocation(vc: UIViewController, latitude: Double?, longitude: Double?,
                                        address: String?, addressDetail: String?) {
        let map = IMShowLocationViewController()
        map.address = address
        map.addressDetail = addressDetail
        map.latitude = latitude
        map.longitude = longitude
        vc.navigationController?.pushViewController(map, animated: false)
    }
    

    private var mapView: BMKMapView!
    private var annotation: BMKPointAnnotation!
    
    var address: String?
    var addressDetail: String?
    var latitude: Double?
    var longitude: Double?
    
    //换算后的火星坐标 其他地图都用这个坐标
    var gcj02Location: CLLocationCoordinate2D? = nil
    
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
        
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "share"), style: .plain, target: self, action: #selector(openOtherMap))
        
        
        self.bd09Decrypt(CLLocationCoordinate2D(latitude: lat, longitude: lo)) { (ll) in
            self.gcj02Location = ll
        }
    }
    
    
    @objc private func openOtherMap() {
        self.showSheetAction(title: nil, message: nil, actions: [
            UIAlertAction(title: "百度地图", style: .default, handler: { (action) in
                self.openMap(url: self.baiduMapUrl())
            }),
            UIAlertAction(title: "高德地图", style: .default, handler: { (action) in
                self.openMap(url: self.aMapUrl())
            }),
            UIAlertAction(title: "腾讯地图", style: .default, handler: { (action) in
                self.openMap(url: self.tencentMapUrl())
            }),
            UIAlertAction(title: "Apple 地图", style: .default, handler: { (action) in
                self.openAppleMap()
            })
        ])
    }
    
    private func openMap(url: URL?) {
        if let u = url {
            DDLogDebug("打开地图 url：\(u)")
            UIApplication.shared.open(u, options: [:], completionHandler: nil)
        }else {
            DDLogError("没有生成url//。。。。。")
            self.showError(title: "未安装该地图app，无法打开")
        }
    }
    
    private func openAppleMap() {
        if let gcj02 = self.gcj02Location {
            let currentLocation = MKMapItem.forCurrentLocation()
            let toLocation = MKMapItem(placemark: MKPlacemark(coordinate: gcj02))
            toLocation.name = self.address
            MKMapItem.openMaps(with: [currentLocation, toLocation], launchOptions: [
                MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving,
                MKLaunchOptionsShowsTrafficKey: NSNumber(booleanLiteral: true)
            ])
        }else {
            DDLogError("没有转化后的坐标。。。。。。。。")
        }
    }
 
    //百度地图
    private func baiduMapUrl() -> URL? {
        if UIApplication.shared.canOpenURL(URL(string: "baidumap://")!) {
            let url = "baidumap://map/direction?origin=我的位置&destination=latlng:\(latitude ?? 0),\(longitude ?? 0)|name:\(address ?? "")&mode=driving&coord_type=bd09ll&src=net.zoneland.m.o2oa"
            return URL(string: url.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed) ?? "")
        }
         return nil
    }
    
    //高德地图
    private func aMapUrl() -> URL? {
        //&sid=BGVIS1&did=BGVIS2
        if UIApplication.shared.canOpenURL(URL(string: "iosamap://")!) {
            let url = "iosamap://path?sourceApplication=O2OA&dlat=\(self.gcj02Location?.latitude ?? 0)&dlon=\(self.gcj02Location?.longitude ?? 0)&dname=\(self.address ?? "")&dev=0&t=0"
            return URL(string: url.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed) ?? "")
        }
         return nil
    }
    
    //腾讯地图
    private func tencentMapUrl() -> URL? {
        if UIApplication.shared.canOpenURL(URL(string: "qqmap://")!) {
            let url = "qqmap://map/routeplan?type=drive&from=我的位置&to=\(self.address ?? "")&tocoord=\(self.gcj02Location?.latitude ?? 0),\(self.gcj02Location?.longitude ?? 0)&policy=1"
            return URL(string: url.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed) ?? "")
        }
         return nil
    }
    
    // MARK: - 百度BD09坐标转火星坐标
    fileprivate let queue = DispatchQueue(label: "O2.LocationConverter.Converter")
    fileprivate func bd09Decrypt(_ bd09Point:CLLocationCoordinate2D, result:@escaping (_ gcj02Point:CLLocationCoordinate2D) -> Void) {
        self.queue.async {
            let x = bd09Point.longitude - 0.0065
            let y = bd09Point.latitude - 0.006
            let z = sqrt(x * x + y * y) - 0.00002 * sin(y * .pi);
            let theta = atan2(y, x) - 0.000003 * cos(x * .pi);
            let resultPoint = CLLocationCoordinate2DMake(z * sin(theta), z * cos(theta))
            DispatchQueue.main.async {
                result(resultPoint)
            }
        }
    }
    
    
    
}

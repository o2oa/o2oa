//
//  IMLocationChooseController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/18.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

typealias O2LocationChooseCallback = (_ result: O2LocationData) -> Void ///< 定义确认回调

class IMLocationChooseController: UIViewController {
    
    static func openChooseLocation(callback: @escaping O2LocationChooseCallback) -> IMLocationChooseController {
        let vc = IMLocationChooseController()
        vc.callback = callback
        return vc
    }
    
    

    @IBOutlet weak var mapContainerView: UIView!
    @IBOutlet weak var addressLabel: UILabel!

    var callback: O2LocationChooseCallback?

    private var mapView: BMKMapView!
    private var locService: BMKLocationManager!
    private var searchAddress: BMKGeoCodeSearch!
    private var annotation: BMKPointAnnotation!
    private var result: O2LocationData?
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "选择位置"
        self.navigationItem.backBarButtonItem = UIBarButtonItem(image: UIImage(named: "icon_fanhui"), style: .plain, target: nil, action: nil)
        //发送按钮
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "发送", style: .plain, target: self, action: #selector(sendLocation))
        self.showLoading()
        self.addressLabel.text = "点击地图选择位置"
        //初始化地图和定位
        let containerFrame = self.mapContainerView.frame
        mapView = BMKMapView(frame: CGRect(x: 0, y: 0, width: containerFrame.width, height: containerFrame.height))
        mapView.zoomLevel = 17
        mapView.showMapPoi = true
        mapView.showIndoorMapPoi = true
        self.mapContainerView.addSubview(mapView)
        mapView.delegate = self
        locService = BMKLocationManager()
        locService.delegate = self
        locService.startUpdatingLocation()
        searchAddress = BMKGeoCodeSearch()
        searchAddress.delegate = self


    }

    @objc private func sendLocation() {
        guard let r =  self.result else {
            self.showError(title: "请选择一个位置！")
            return
        }
        self.callback?(r)
        self.navigationController?.popViewController(animated: false)
    }

    deinit {
        mapView.delegate = nil
        locService.delegate = nil
        locService.stopUpdatingLocation()
        searchAddress.delegate = nil
    }

}

extension IMLocationChooseController: BMKMapViewDelegate, BMKLocationManagerDelegate, BMKGeoCodeSearchDelegate {
    //定位
    func bmkLocationManager(_ manager: BMKLocationManager, didUpdate location: BMKLocation?, orError error: Error?) {
        if let loc = location?.location {
            DDLogDebug("定位到 当前位置,\(loc.coordinate.latitude),\(loc.coordinate.longitude)")
            let user = BMKUserLocation()
            user.location = loc
            mapView.updateLocationData(user)
            mapView.centerCoordinate = CLLocationCoordinate2D(latitude: loc.coordinate.latitude, longitude: loc.coordinate.longitude)

            //定位完成停止定位
            locService.stopUpdatingLocation()
            self.hideLoading()
        }
    }

    //位置搜索
    func onGetReverseGeoCodeResult(_ searcher: BMKGeoCodeSearch!, result: BMKReverseGeoCodeSearchResult!, errorCode error: BMKSearchErrorCode) {
        DDLogDebug("获取到地址： \(String(describing: result.address))")
        self.addressLabel.text = result.address
        //todo 地址和位置经纬度要保存起来
        if self.result == nil {
            self.result = O2LocationData()
        }
        self.result?.address = result.address
        self.result?.addressDetail = result.sematicDescription
        self.result?.latitude = result.location.latitude
        self.result?.longitude = result.location.longitude
    }

    //地图
    func mapView(_ mapView: BMKMapView!, onClickedMapPoi mapPoi: BMKMapPoi!) {
        if annotation == nil {
            annotation = BMKPointAnnotation()
            annotation.coordinate = mapPoi.pt
            mapView.addAnnotation(annotation)
        } else {
            annotation.coordinate = mapPoi.pt
        }
        //反向查询具体地址名称
        let re = BMKReverseGeoCodeSearchOption()
        re.location = mapPoi.pt
        let flag = searchAddress.reverseGeoCode(re)
        DDLogDebug("根据位置坐标，查询地址信息 \(flag)")
    }

    func mapView(_ mapView: BMKMapView!, onClickedMapBlank coordinate: CLLocationCoordinate2D) {
        //单击
        if annotation == nil {
            annotation = BMKPointAnnotation()
            annotation.coordinate = coordinate
            mapView.addAnnotation(annotation)
        } else {
            annotation.coordinate = coordinate
        }

        //反向查询具体地址名称
        let re = BMKReverseGeoCodeSearchOption()
        re.location = coordinate
        let flag = searchAddress.reverseGeoCode(re)
        DDLogDebug("根据位置坐标，查询地址信息 \(flag)")
    }
}

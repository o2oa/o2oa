//
//  OOAttanceSettingController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/14.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import O2OA_Auth_SDK
import CocoaLumberjack

class OOAttanceSettingController: UIViewController {
    
    var mapView:BMKMapView!
    
    var locService:BMKLocationManager!

    var searchAddress:BMKGeoCodeSearch!
    
    var settingBean:OOAttandanceNewWorkPlace = OOAttandanceNewWorkPlace()
    
    private var annotation:BMKPointAnnotation!
    
    private var isAdmin = false
    
    private var viewModel:OOAttandanceViewModel = {
       return OOAttandanceViewModel()
    }()
    
    private lazy var  dataView:OOAttandanceSettingDataView = {
        let view = Bundle.main.loadNibNamed("OOAttandanceSettingDataView", owner: self
        , options: nil)?.first as! OOAttandanceSettingDataView
        return view
    }()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
//        title = "设置"
//        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(closeWindow))
//        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "地点管理", style: .plain, target: self, action: #selector(navWorkPlaceManager(_:)))
        loadAdmin()
        //增加mapView
        commonMapView()
        commonDataView()
        
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        dataView.isHidden = false
        self.addNotificationObserver(OONotification.newWorkPlace.stringValue, selector: #selector(createNewWorkPlace(_:)))
        self.addKeyboardWillShowNotification()
        self.addKeyboardWillHideNotification()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.removeNotificationObserver(OONotification.newWorkPlace.stringValue)
        self.removeKeyboardWillHideNotification()
        self.removeKeyboardWillShowNotification()
        dataView.isHidden  = true
    }
    
    override func keyboardWillShowWithFrame(_ frame: CGRect) {
        //输入框view向上移动
        print("keyboardWillShowWithFrame = ",frame)
        UIView.animate(withDuration: 0.5) {
            //self.dataView.origin.y = kScreenH - 125 - frame.size.height
            self.dataView.frame = CGRect(x: 0, y: kScreenH - 125 - frame.size.height, width: SCREEN_WIDTH, height: 125)
        }
    }
    
    override func keyboardWillHideWithFrame(_ frame: CGRect) {
         print("keyboardWillHideWithFrame = ",frame)
        UIView.animate(withDuration: 0.5) {
            //self.dataView.origin.y = kScreenH - 125 - 50
            self.dataView.frame = CGRect(x: 0, y: SCREEN_HEIGHT - 125 - 50, width: SCREEN_WIDTH, height: 125)
        }
    }
//

   
    private func loadAdmin() {
        let distName = O2AuthSDK.shared.myInfo()?.distinguishedName ?? ""
        viewModel.getAttendanceAdmin().then { (admins) in
                admins.forEach({ (admin) in
                    if admin.adminName == distName {
                        self.isAdmin = true
                    }
                })
            DDLogDebug("是否是管理员：\(self.isAdmin)")
            }
            .catch { (myerror) in
                DDLogError(myerror.localizedDescription)
        }
    }
    
    @objc private func createNewWorkPlace(_ notification:Notification){
        DDLogDebug("接收到消息。。。。。。。。。。。。。。。。。。。。。。")
        if self.isAdmin == false {
            self.showError(title: "你不是管理员，无操作权限")
            return
        }
        if let obj = notification.object as? (String,String,String) {
            settingBean.placeName = obj.0
            settingBean.placeAlias = obj.1
            settingBean.errorRange = obj.2
        }
        self.showLoading(title: "创建打卡地址...")
        viewModel.postCheckinLocation(settingBean) { (resultType) in
            self.hideLoading()
            switch resultType {
            case .ok(_):
                self.showSuccess(title:  "打卡地址设置成功")
                break
            case .fail(let errorMessage):
                self.showError(title: "打卡地址设置失败\n\(errorMessage)" )
                break
            default:
                break
            }
        }
        
        
    }
    
    @objc private func navWorkPlaceManager(_ sender:Any?){
        if self.isAdmin {
            let destVC = OOAttandanceWorkPlaceController(nibName: "OOAttandanceWorkPlaceController", bundle: nil)
            self.pushVC(destVC)
        }else {
            self.showMessage(msg: "你不是管理员，无操作权限")
        }
    }
    
    func commonMapView() {
        mapView = BMKMapView(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: SCREEN_HEIGHT))
        mapView.zoomLevel = 19
        mapView.showMapPoi = true
        mapView.showIndoorMapPoi = true
        view.addSubview(mapView)
        mapView.delegate = self
        //mapView.showsUserLocation = true
        //locService.desiredAccuracy = 100
        locService = BMKLocationManager()
        locService.delegate = self
        locService.startUpdatingLocation()
        searchAddress = BMKGeoCodeSearch()
        searchAddress.delegate = self
    }
    
    func commonDataView() {
        let window = UIApplication.shared.windows.last
        dataView.frame = CGRect(x: 0, y: SCREEN_HEIGHT - 125 - 50, width: SCREEN_WIDTH, height: 125)
        window?.addSubview(dataView)
    }
    
    
    
    @objc func closeWindow() {
        self.tabBarController?.navigationController?.dismiss(animated: true, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    deinit {
        mapView.delegate = nil
        locService.delegate = nil
        locService.stopUpdatingLocation()
        searchAddress.delegate = nil
    }
    

}

extension OOAttanceSettingController:BMKMapViewDelegate {
    
    
    func mapView(_ mapView: BMKMapView!, onClickedMapBlank coordinate: CLLocationCoordinate2D) {
        mapView.superview?.endEditing(true)
        //单击
        if annotation == nil {
            annotation = BMKPointAnnotation()
            annotation.coordinate = coordinate
            annotation.title = "公司打卡地点";
            mapView.addAnnotation(annotation)
            
        }else{
            annotation.coordinate = coordinate
            annotation.title = "公司打卡地点";
            
        }
       
        //反向查询具体地址名称
        let re = BMKReverseGeoCodeSearchOption()
        re.location = coordinate
        let flag = searchAddress.reverseGeoCode(re)
        DDLogDebug("coordinate searchAddress \(flag)")
    }
    
    
    func mapView(_ mapView: BMKMapView!, onClickedMapPoi mapPoi: BMKMapPoi!) {
        let re = BMKReverseGeoCodeSearchOption()
        let coordinate = mapPoi.pt
        re.location = coordinate
        let flag = searchAddress.reverseGeoCode(re)
        DDLogDebug("mapPoi searchAddress \(flag)")
        
    }
    
    func mapView(_ mapView: BMKMapView!, didSelect view: BMKAnnotationView!) {
        mapView.centerCoordinate = view.annotation.coordinate
    }
}

extension OOAttanceSettingController:BMKLocationManagerDelegate {
    
//    func willStartLocatingUser() {
//        DDLogDebug("willStartLocatingUser")
//        MBProgressHUD_JChat.showMessage(message:"正在定位中，请稍候", toView: self.mapView)
//    }
    func bmkLocationManager(_ manager: BMKLocationManager, didUpdate location: BMKLocation?, orError error: Error?) {
        if let loc = location?.location {
           DDLogDebug("设置 当前位置,\(loc.coordinate.latitude),\(loc.coordinate.longitude)")
           let user = BMKUserLocation()
           user.location = loc
           mapView.updateLocationData(user)
           mapView.centerCoordinate = CLLocationCoordinate2D(latitude: loc.coordinate.latitude, longitude: loc.coordinate.longitude)
            
           //定位完成停止定位
           locService.stopUpdatingLocation()
        }
    }
    
//    func didUpdate(_ userLocation: BMKUserLocation!) {
//        DDLogDebug("当前位置,\(userLocation.location.coordinate.latitude),\(userLocation.location.coordinate.longitude)")
//        mapView.updateLocationData(userLocation)
//        mapView.centerCoordinate = userLocation.location.coordinate
//        //定位完成停止定位
//        locService.stopUpdatingLocation()
//    }
//
//    func didStopLocatingUser() {
//
//        MBProgressHUD_JChat.hide(forView: self.mapView, animated: true)
//    }
}

extension OOAttanceSettingController:BMKGeoCodeSearchDelegate {
    
    func onGetReverseGeoCodeResult(_ searcher: BMKGeoCodeSearch!, result: BMKReverseGeoCodeSearchResult!, errorCode error: BMKSearchErrorCode) {
        dataView.workPlaceNameTextField.text = result.address
        dataView.workAliasNameTextField.text = result.sematicDescription

        //设置settingBean
        settingBean.placeName = result.address
        settingBean.placeAlias = result.sematicDescription
        settingBean.creator = O2AuthSDK.shared.myInfo()?.distinguishedName
        settingBean.longitude = String(result.location.longitude)
        settingBean.latitude = String(result.location.latitude)
        settingBean.errorRange  = "0"
    }
    
}

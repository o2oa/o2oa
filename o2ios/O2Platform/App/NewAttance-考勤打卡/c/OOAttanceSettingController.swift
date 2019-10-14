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
    
    var locService:BMKLocationService!

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
        title = "设置"
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(closeWindow))
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "地点管理", style: .plain, target: self, action: #selector(navWorkPlaceManager(_:)))
        loadAdmin()
        //增加mapView
        commonDataView()
        commonMapView()
        
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
            MBProgressHUD_JChat.show(text: "你不是管理员，无操作权限", view: self.view)
            return
        }
        if let obj = notification.object as? (String,String,String) {
            settingBean.placeName = obj.0
            settingBean.placeAlias = obj.1
            settingBean.errorRange = obj.2
        }
        MBProgressHUD_JChat.showMessage(message: "创建打卡地址...", toView: view)
        viewModel.postCheckinLocation(settingBean) { (resultType) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            switch resultType {
            case .ok(_):
                MBProgressHUD_JChat.show(text: "打卡地址设置成功", view: self.view, 1)
                break
            case .fail(let errorMessage):
                MBProgressHUD_JChat.show(text: "打卡地址设置失败\n\(errorMessage)", view: self.view, 1)
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
            MBProgressHUD_JChat.show(text: "你不是管理员，无操作权限", view: self.view)
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
        locService = BMKLocationService()
        locService.delegate = self
        locService.startUserLocationService()
        searchAddress = BMKGeoCodeSearch()
        searchAddress.delegate = self
    }
    
    func commonDataView() {
        let window = UIApplication.shared.windows[0]
        //let barHeight = self.cyl_tabBarController.tabBarHeight
        dataView.frame = CGRect(x: 0, y: SCREEN_HEIGHT - 125 - 50, width: SCREEN_WIDTH, height: 125)
        //view.insertSubview(dataView, aboveSubview: mapView)
        window.addSubview(dataView)
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
        locService.stopUserLocationService()
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
        let re = BMKReverseGeoCodeOption()
        re.reverseGeoPoint = coordinate
        let flag = searchAddress.reverseGeoCode(re)
        O2Logger.debug("searchAddress \(flag)")
    }
    
    
    func mapView(_ mapView: BMKMapView!, onClickedMapPoi mapPoi: BMKMapPoi!) {
        let re = BMKReverseGeoCodeOption()
        let coordinate = mapPoi.pt
        re.reverseGeoPoint = coordinate
        let flag = searchAddress.reverseGeoCode(re)
        O2Logger.debug("searchAddress \(flag)")
        
    }
    
    func mapView(_ mapView: BMKMapView!, didSelect view: BMKAnnotationView!) {
        mapView.centerCoordinate = view.annotation.coordinate
    }
}

extension OOAttanceSettingController:BMKLocationServiceDelegate {
    
    func willStartLocatingUser() {
        O2Logger.debug("willStartLocatingUser")
        MBProgressHUD_JChat.showMessage(message:"正在定位中，请稍候", toView: self.mapView)
    }
    
    func didUpdate(_ userLocation: BMKUserLocation!) {
        O2Logger.debug("当前位置,\(userLocation.location.coordinate.latitude),\(userLocation.location.coordinate.longitude)")
        mapView.updateLocationData(userLocation)
        mapView.centerCoordinate = userLocation.location.coordinate
        //定位完成停止定位
        locService.stopUserLocationService()
    }
    
    func didStopLocatingUser() {
        O2Logger.debug("didStopLocatingUser")
        MBProgressHUD_JChat.hide(forView: self.mapView, animated: true)
    }
}

extension OOAttanceSettingController:BMKGeoCodeSearchDelegate {
    
    func onGetReverseGeoCodeResult(_ searcher: BMKGeoCodeSearch!, result: BMKReverseGeoCodeResult!, errorCode error: BMKSearchErrorCode) {
        dataView.workPlaceNameTextField.text = result.address
        dataView.workAliasNameTextField.text = result.sematicDescription
        for item in result.poiList {
            let m = item as! BMKPoiInfo
            print(m.name)            ///<POI名称
            print(m.uid)
            print(m.address)      ///<POI地址
            print(m.city)        ///<POI所在城市
            print(m.phone)        ///<POI电话号码
            print(m.postcode)        ///<POI邮编
            print(m.epoitype) ///<POI类型，0:普通点 1:公交站 2:公交线路 3:地铁站 4:地铁线路
            print(m.pt.longitude,m.pt.latitude)    ///<POI坐标
        }
        //设置settingBean
        settingBean.placeName = result.address
        settingBean.placeAlias = result.sematicDescription
        settingBean.creator = O2AuthSDK.shared.myInfo()?.distinguishedName
        settingBean.longitude = String(result.location.longitude)
        settingBean.latitude = String(result.location.latitude)
        settingBean.errorRange  = "0"
    }
    
}

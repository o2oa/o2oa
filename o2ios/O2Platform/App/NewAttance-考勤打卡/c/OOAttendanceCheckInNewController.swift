//
//  OOAttendanceCheckInNewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/7/21.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import O2OA_Auth_SDK

class OOAttendanceCheckInNewController: UIViewController {


    @IBOutlet weak var checkInBtnTimeLabel: UILabel!
    @IBOutlet weak var checkInBtnLable: UILabel!
    @IBOutlet weak var checkInBtnView: UIView!
    @IBOutlet weak var locationCheckImageView: UIImageView!
    @IBOutlet weak var locationLabel: UILabel!
    @IBOutlet weak var schedulesView: UICollectionView!

    fileprivate let itemNumberInRow = 2

    //定位
    private var userLocation: BMKUserLocation!
    private var locService: BMKLocationManager!
    private var searchAddress: BMKGeoCodeSearch!
    private var workPlaces: [OOAttandanceWorkPlace] = []

    //定时器
    private var timer: Timer?
    private lazy var viewModel: OOAttandanceViewModel = {
        return OOAttandanceViewModel()
    }()
    //打卡和班次
    private var schedules: [OOAttandanceMobileScheduleInfo] = []
    private var lastRecord: OOAttandanceMobileDetail? = nil
    private var needCheckIn = false
    private var isInWorkPlace = false
    private var bmkResult: BMKReverseGeoCodeSearchResult? = nil
    //打卡对象
    var checkinForm: OOAttandanceMobileCheckinForm = OOAttandanceMobileCheckinForm()

    override func viewDidLoad() {
        super.viewDidLoad()

        self.schedulesView.delegate = self
        self.schedulesView.dataSource = self
        self.schedulesView.register(UINib(nibName: "OOAttendanceScheduleViewCell", bundle: nil), forCellWithReuseIdentifier: "OOAttendanceScheduleViewCell")
        self.checkInBtnView.addTapGesture { (tap) in
            self.postCheckinButton(nil)
        }

        //初始化定时器
        self.timer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(timeTick), userInfo: nil, repeats: true)
        //获取数据
        self.loadMyRecords()
        //工作地址
        self.loadWorkPlace()
    }

    override func viewWillAppear(_ animated: Bool) {
        self.timer?.fire()
        self.startLocationService()
    }

    override func viewWillDisappear(_ animated: Bool) {
        self.timer?.invalidate()
        self.stopLocationService()
    }

    ///打卡
    private func postCheckinButton(_ scheduleInfo: OOAttandanceMobileScheduleInfo?) {
        if !self.needCheckIn {
            self.showError(title: "当前不需要打卡！")
            return
        }
        if !self.isInWorkPlace {
            self.showError(title: "不在打卡范围内！")
            return
        }


        if let info = scheduleInfo, info.recordId != nil { //更新打卡
            self.showDefaultConfirm(title: "更新打卡", message: "确定要更新这条打卡数据吗？") { (action) in
                self.showLoading()
                self.checkinForm.id = info.recordId
                self.checkinForm.recordAddress = self.bmkResult?.address
                self.checkinForm.desc = self.bmkResult?.sematicDescription
                self.checkinForm.longitude = String(self.bmkResult?.location.longitude ?? 0.0)
                self.checkinForm.latitude = String(self.bmkResult?.location.latitude ?? 0.0)
                self.checkinForm.empNo = O2AuthSDK.shared.myInfo()?.employee
                self.checkinForm.empName = O2AuthSDK.shared.myInfo()?.name
                let currenDate = Date()
                self.checkinForm.recordDateString = currenDate.toString("yyyy-MM-dd")
                self.checkinForm.signTime = currenDate.toString("HH:mm:ss")
                self.checkinForm.optMachineType = UIDevice.deviceModelReadable()
                self.checkinForm.optSystemName = "\(UIDevice.systemName()) \(UIDevice.systemVersion())"
                self.checkinForm.checkin_type = info.checkinType
                self.viewModel.postMyCheckin(self.checkinForm) { (result) in
                    DispatchQueue.main.async {
                        self.hideLoading()
                    }
                    switch result {
                    case .ok(_):
                        self.loadMyRecords()
                        break
                    case .fail(let errorMessage):
                        DDLogError(errorMessage)
                        break
                    default:
                        break
                    }
                }
            }
        } else {
            let reversed = self.schedules.reversed()
            var newList: [OOAttandanceMobileScheduleInfo] = []
            reversed.forEach { (info) in
                if info.checkinStatus == "未打卡" {
                    newList.append(info)
                }
            }
            let checkType = newList.count > 0 ? newList.last!.checkinType : ""
            self.showLoading()
            checkinForm.id = nil
            checkinForm.recordAddress = self.bmkResult?.address
            checkinForm.desc = self.bmkResult?.sematicDescription
            checkinForm.longitude = String(self.bmkResult?.location.longitude ?? 0.0)
            checkinForm.latitude = String(self.bmkResult?.location.latitude ?? 0.0)
            checkinForm.empNo = O2AuthSDK.shared.myInfo()?.employee
            checkinForm.empName = O2AuthSDK.shared.myInfo()?.name
            let currenDate = Date()
            checkinForm.recordDateString = currenDate.toString("yyyy-MM-dd")
            checkinForm.signTime = currenDate.toString("HH:mm:ss")
            checkinForm.optMachineType = UIDevice.deviceModelReadable()
            checkinForm.optSystemName = "\(UIDevice.systemName()) \(UIDevice.systemVersion())"
            checkinForm.checkin_type = checkType
            viewModel.postMyCheckin(checkinForm) { (result) in
                DispatchQueue.main.async {
                    self.hideLoading()
                }
                switch result {
                case .ok(_):
                    self.loadMyRecords()
                    break
                case .fail(let errorMessage):
                    DDLogError(errorMessage)
                    break
                default:
                    break
                }
            }
        }


    }

    ///接收到位置信息
    private func locationReceive(bmkResult: BMKReverseGeoCodeSearchResult, isIn: Bool, workPlace: OOAttandanceWorkPlace?) {
        self.isInWorkPlace = isIn
        self.bmkResult = bmkResult
        if isIn {
            // 打卡按钮启用
            self.locationLabel.text = workPlace?.placeName
            self.locationCheckImageView.image = UIImage(named: "icon__ok2_click")
        } else {
            //打卡按钮禁用
            self.locationLabel.text = bmkResult.address
            self.locationCheckImageView.image = UIImage(named: "icon_delete_1")
        }
    }

    ///定位
    private func startLocationService() {
        locService = BMKLocationManager()
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
    ///结束定位
    private func stopLocationService() {
        locService.stopUpdatingLocation()
        locService.delegate = nil
        searchAddress.delegate = nil
    }

    ///查询工作地址
    private func loadWorkPlace() {
        self.viewModel.getLocationWorkPlace { (myResult) in
            switch myResult {
            case .ok(let result):
                if let model = result as? [OOAttandanceWorkPlace] {
                    DispatchQueue.main.async {
                        self.workPlaces = model
                    }
                }
                break
            case .fail(let s):
                self.showError(title: "错误:\n\(s)")
                break
            default:
                break
            }
        }
    }
    ///获取打卡记录和班次数据
    private func loadMyRecords() {
        self.viewModel.listMyRecords { (result) in
            switch result {
            case .ok(let record):
                let model = record as? OOMyAttandanceRecords
                let records = model?.records ?? []
                self.lastRecord = records.last
                var unCheckNumber = 0
                self.schedules.removeAll()
                model?.scheduleInfos?.forEach({ (f) in
                    let info = OOAttandanceMobileScheduleInfo()
                    info.signTime = f.signTime
                    info.signDate = f.signDate
                    info.checkinType = f.checkinType
                    info.signSeq = f.signSeq
                    let anyRecord = records.first { (detail) -> Bool in
                        detail.checkin_type == f.checkinType
                    }
                    if anyRecord != nil {
                        info.checkinStatus = "已打卡"
                        info.recordId = anyRecord?.id
                        info.checkinTime = anyRecord?.signTime
                        unCheckNumber = 0
                    } else {
                        info.checkinStatus = "未打卡"
                        unCheckNumber += 1
                    }
                    self.schedules.append(info)
                })
                DispatchQueue.main.async {
                    if unCheckNumber > 0 {
                        self.needCheckIn = true
                        self.setCheckInBtnEnableStyle()
                    } else {
                        self.needCheckIn = false
                        self.setCheckInBtnDisableStyle()
                    }
                    self.schedulesView.reloadData()
                }
                break
            case .fail(let err):
                DDLogError(err)
                DispatchQueue.main.async {
                    self.needCheckIn = false
                    self.setCheckInBtnDisableStyle()
                }
                break
            default:
                DDLogError("default ..................")
                break
            }
        }
    }

    private func setCheckInBtnDisableStyle() {
        self.checkInBtnView.backgroundColor = UIColor(hex: "#cccccc")
    }
    private func setCheckInBtnEnableStyle() {
        self.checkInBtnView.backgroundColor = base_color
    }

    ///刷新按钮时间
    @objc private func timeTick() {
        let now = Date().toString("HH:mm:ss")
        self.checkInBtnTimeLabel.text = now
    }

}

extension OOAttendanceCheckInNewController: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.schedules.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        if let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "OOAttendanceScheduleViewCell", for: indexPath) as? OOAttendanceScheduleViewCell {
            let s = self.schedules[indexPath.row]
            let isLastRecord = s.recordId == self.lastRecord?.id
            cell.setData(info: s, isLastRecord: isLastRecord)
            return cell
        }
        return UICollectionViewCell()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let s = self.schedules[indexPath.row]
        if let last = self.lastRecord, last.id == s.recordId {
            self.postCheckinButton(s)
        }
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: ((SCREEN_WIDTH - 52) / 2), height: 64)
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 5.0
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0.0
    }

}



extension OOAttendanceCheckInNewController: BMKLocationManagerDelegate {

    func bmkLocationManager(_ manager: BMKLocationManager, didUpdate location: BMKLocation?, orError error: Error?) {
        if let loc = location?.location {
            DDLogDebug("当前位置,\(loc.coordinate.latitude),\(loc.coordinate.longitude)")
            //搜索到指定的地点
            let re = BMKReverseGeoCodeSearchOption()
            re.location = CLLocationCoordinate2D(latitude: loc.coordinate.latitude, longitude: loc.coordinate.longitude)
            let _ = searchAddress.reverseGeoCode(re)
        } else {
            DDLogError("没有获取到定位信息！！！！！")
        }
    }


}

extension OOAttendanceCheckInNewController: BMKGeoCodeSearchDelegate {

    /// 计算所有位置是否有一个位置在误差范围内
    func calcErrorRange(_ checkinLocation: CLLocationCoordinate2D) -> (Bool, OOAttandanceWorkPlace?) {
        var result = false
        for item in self.workPlaces {
            let longitude = Double((item.longitude)!)
            let latitude = Double((item.latitude)!)
            let eRange = item.errorRange!
            let theLocation = CLLocationCoordinate2DMake(latitude!, longitude!)
            result = BMKCircleContainsCoordinate(theLocation, checkinLocation, Double(eRange))
            if result == true {
                return (true, item)
            }
        }
        return (false, nil)
    }

    func onGetReverseGeoCodeResult(_ searcher: BMKGeoCodeSearch?, result: BMKReverseGeoCodeSearchResult?, errorCode error: BMKSearchErrorCode) {
        //发送定位的实时位置及名称信息
        if let location = result?.location {
            let calResult = calcErrorRange(location)
            self.locationReceive(bmkResult: result!, isIn: calResult.0, workPlace: calResult.1)
        }
    }

    func onGetGeoCodeResult(_ searcher: BMKGeoCodeSearch!, result: BMKGeoCodeSearchResult!, errorCode error: BMKSearchErrorCode) {
        if Int(error.rawValue) == 0 {
            DDLogDebug("result \(String(describing: result))")
        } else {
            DDLogDebug("result error  errorCode = \(Int(error.rawValue))")
        }

    }
}


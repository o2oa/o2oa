//
//  OOAttanceCheckInController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/17.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import O2OA_Auth_SDK
import CocoaLumberjack


class OOAttanceCheckInController: UITableViewController {
    
    private lazy var viewModel:OOAttandanceViewModel = {
       return OOAttandanceViewModel()
    }()
    
    var checkinForm:OOAttandanceMobileCheckinForm = OOAttandanceMobileCheckinForm()
    
    var myButton:UIButton?
    var feature : OOAttandanceFeature?
    
    private lazy var headerView:OOAttanceHeaderView = {
       let view = Bundle.main.loadNibNamed("OOAttanceHeaderView", owner: self, options: nil)?.first as! OOAttanceHeaderView
        view.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 280)
        return view
    }()
    
    private lazy var promptView:OOAttanceCheckinPromptView = {
        let view = Bundle.main.loadNibNamed("OOAttanceCheckinPromptView", owner: self, options: nil)?.first as! OOAttanceCheckinPromptView
        view.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 44)
        return view
    }()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        headerView.startBMKMapViewService()
        NotificationCenter.default.addObserver(self, selector: #selector(locationReceive(_:)), name: OONotification.location.notificationName, object: nil)
        if myButton != nil {
            myButton?.isHidden = false
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        headerView.stopBMKMapViewService()
        NotificationCenter.default.removeObserver(self)
        if myButton != nil {
            myButton?.isHidden = true
        }
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
    }
    

   
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib.init(nibName: "OOAttanceItemCell", bundle: nil), forCellReuseIdentifier: "OOAttanceItemCell")
        
        getCurrentCheckinList()
        getMyRecords()
        self.perform(#selector(createButton), with: nil, afterDelay: 0)
        
        getWorkPlace()
    }
    
    
    //创建打卡按钮
    @objc private func createButton() {
        let window = UIApplication.shared.windows[0]
        myButton = UIButton(type: .custom)
        myButton?.frame = CGRect(x: kScreenW - 90, y: kScreenH - 150, width: 70, height: 70)
        myButton?.setTitle("打卡", for: .normal)
        myButton?.setTitle("打卡", for: .disabled)
        myButton?.titleLabel?.font = UIFont(name: "PingFangSC-Medium", size: 14.0)!
        myButton?.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        myButton?.setBackgroundColor(UIColor.gray, forState: .disabled)
        myButton?.isEnabled = false
        myButton?.layer.cornerRadius = 35
        myButton?.layer.masksToBounds = true
        myButton?.addTarget(self, action: #selector(postCheckinButton(_:)), for: .touchUpInside)
        window.addSubview(myButton!)
    }
    //删除打卡按钮
    private func removeButton() {
        if myButton != nil {
            myButton?.removeFromSuperview()
            myButton = nil
        }
    }
    
    @objc private func locationReceive(_ notification:Notification){
        if  let result = notification.object as?  BMKReverseGeoCodeSearchResult {
            checkinForm.recordAddress = result.address
            checkinForm.desc = result.sematicDescription
            checkinForm.longitude = String(result.location.longitude)
            checkinForm.latitude = String(result.location.latitude)
            checkinForm.empNo = O2AuthSDK.shared.myInfo()?.employee
            checkinForm.empName = O2AuthSDK.shared.myInfo()?.name
            let currenDate = Date()
            checkinForm.recordDateString = currenDate.toString("yyyy-MM-dd")
            checkinForm.signTime = currenDate.toString("HH:mm:ss")
            checkinForm.optMachineType = UIDevice.deviceModelReadable()
            checkinForm.optSystemName = "\(UIDevice.systemName()) \(UIDevice.systemVersion())"
            // 打卡按钮启用
            myButton?.isEnabled = true
            headerView.addSubview(promptView)
            DDLogDebug("checkForm set completed")
        }else{
            //打卡按钮禁用
            myButton?.isEnabled = false
            promptView.removeFromSuperview()
        }
    }
    
    @objc private func postCheckinButton(_ sender:UIButton){
        if self.feature != nil {
            if self.feature?.signSeq ?? -1 < 1 {
                self.showError(title: "当前不需要打卡！")
                return
            }
        }
        
        self.showLoading(title: "打卡中...")
        checkinForm.checkin_type = self.feature?.checkinType ?? ""
        viewModel.postMyCheckin(checkinForm) { (result) in
            self.hideLoading()
            switch result {
            case .ok(_):
                DispatchQueue.main.async {
                    self.showSuccess(title: "打卡成功")
                    self.getCurrentCheckinList()
                    self.getMyRecords()
                }
                break
            case .fail(let errorMessage):
                DispatchQueue.main.async {
                    self.showError(title: "打卡失败,\n\(errorMessage)")
                }
                break
            default:
                break
            }
        }
    }
    
//    @objc private func changePostion(_ pan:UIPanGestureRecognizer){
//
//    }
    
    func getWorkPlace() {
        viewModel.getLocationWorkPlace { (myResult) in
            switch myResult {
            case .ok(let result):
                DDLogDebug("有打卡位置了。。。。。。")
                let model = result as? [OOAttandanceWorkPlace]
                DispatchQueue.main.async {
                   self.headerView.workPlaces = model
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
    
    func getMyRecords() {
        viewModel.listMyRecords { (result) in
            switch result {
            case .ok(let record):
                let model = record as? OOMyAttandanceRecords
                if let feature = model?.feature {
                    self.feature = feature
                }
                break
            case .fail(let err):
                DDLogError(err)
                break
            default:
                break
            }
        }
    }
    
    func getCurrentCheckinList() {
        var model = CommonPageModel()
        model.pageSize = 200
        let bean = OOAttandanceMobileQueryBean()
        
        bean.empName = O2AuthSDK.shared.myInfo()?.distinguishedName
        let currentDate = Date().toString("yyyy-MM-dd")
        bean.startDate = currentDate
        bean.endDate = currentDate
        
        self.showLoading()
        viewModel.getMyCheckinList(model, bean) { (myResult) in
            self.hideLoading()
            switch myResult {
            case .fail(let s):
                self.showError(title: "错误:\n\(s)")
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
                break
            case .reload:
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            default:
                break
            }
        }
    }
    
     

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return viewModel.numberOfSections()
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return viewModel.numberOfRowsInSection(section)
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "OOAttanceItemCell", for: indexPath) as! (OOAttanceItemCell & Configurable)
        let item = viewModel.nodeForIndexPath(indexPath)
        cell.config(withItem: item)
        return cell
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if section == 0 {
            return headerView
        }
        return nil
    }
    
    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        if section == 0 {
            let view =  Bundle.main.loadNibNamed("OOAttanceFooterView", owner: self, options: nil)?.first as! OOAttanceFooterView
            return view
        }
        return nil
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 {
            return 280.0
        }
        return 10
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if section == 0 {
            return 50.0
        }
        return 10
    }
    
    
    deinit {
         DDLogDebug("deinit 这里是checkin controller 。。。。。。。。。")
    }
}

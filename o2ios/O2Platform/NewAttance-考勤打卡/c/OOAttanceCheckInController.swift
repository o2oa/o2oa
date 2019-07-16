//
//  OOAttanceCheckInController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/17.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import EZSwiftExtensions
import O2OA_Auth_SDK


class OOAttanceCheckInController: UITableViewController {
    
    private lazy var viewModel:OOAttandanceViewModel = {
       return OOAttandanceViewModel()
    }()
    
    var checkinForm:OOAttandanceMobileCheckinForm = OOAttandanceMobileCheckinForm()
    
    var myButton:UIButton!
    
    private lazy var headerView:OOAttanceHeaderView = {
       let view = Bundle.main.loadNibNamed("OOAttanceHeaderView", owner: self, options: nil)?.first as! OOAttanceHeaderView
        view.frame = CGRect(x: 0, y: 0, w: SCREEN_WIDTH, h: 280)
        return view
    }()
    
    private lazy var promptView:OOAttanceCheckinPromptView = {
        let view = Bundle.main.loadNibNamed("OOAttanceCheckinPromptView", owner: self, options: nil)?.first as! OOAttanceCheckinPromptView
        view.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 44)
        return view
    }()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        NotificationCenter.default.addObserver(self, selector: #selector(locationReceive(_:)), name: OONotification.location.notificationName, object: nil)
        if myButton != nil {
            myButton.isHidden = false
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        NotificationCenter.default.removeObserver(self)
        if myButton != nil {
            myButton.isHidden = true
        }
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        getWorkPlace()
    }

   
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "打卡"
        headerView.startBMKMapViewService()
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(closeWindow))
        //tableView.tableHeaderView = headerView
        //tableView.contentInset = UIEdgeInsets(top: 230, left: 0, bottom: 0, right: 0)
        //register Cell
        tableView.register(UINib.init(nibName: "OOAttanceItemCell", bundle: nil), forCellReuseIdentifier: "OOAttanceItemCell")
        
        getCurrentCheckinList()
        self.perform(#selector(createButton), with: nil, afterDelay: 0)
    }
    
    @objc func closeWindow() {
        self.tabBarController?.navigationController?.dismiss(animated: true, completion: nil)
    }
    
    @objc private func createButton() {
        let window = UIApplication.shared.windows[0]
        myButton = UIButton(type: .custom)
        myButton.frame = CGRect(x: kScreenW - 90, y: kScreenH - 150, w: 70, h: 70)
        myButton.setTitle("打卡", for: .normal)
        myButton.setTitle("打卡", for: .disabled)
        myButton.titleLabel?.font = UIFont(name: "PingFangSC-Medium", size: 18.0)!
        myButton.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        myButton.setBackgroundColor(UIColor.gray, forState: .disabled)
        myButton.isEnabled = false
        myButton.layer.cornerRadius = 35
        myButton.layer.masksToBounds = true
        myButton.addTarget(self, action: #selector(postCheckinButton(_:)), for: .touchUpInside)
        window.addSubview(myButton)
        let pan = UIPanGestureRecognizer(target: self, action: #selector(changePostion(_:)))
        myButton.addGestureRecognizer(pan)
    }
    
    @objc private func locationReceive(_ notification:Notification){
        if  let result = notification.object as?  BMKReverseGeoCodeResult {
            checkinForm.recordAddress = result.address
            checkinForm.desc = result.sematicDescription
            checkinForm.longitude = String(result.location.longitude)
            checkinForm.latitude = String(result.location.latitude)
            
            checkinForm.empNo = O2AuthSDK.shared.myInfo()?.employee
            checkinForm.empName = O2AuthSDK.shared.myInfo()?.name
            let currenDate = Date()
            checkinForm.recordDateString = currenDate.toString(format: "yyyy-MM-dd")
            checkinForm.signTime = currenDate.toString(format: "HH:mm:ss")
            
            checkinForm.optMachineType = UIDevice.deviceModel()
            checkinForm.optSystemName = "\(UIDevice.systemName()) \(UIDevice.systemVersion())"
            // button enable
            myButton.isEnabled = true
            headerView.addSubview(promptView)
            O2Logger.debug("checkForm set completed")
        }else{
            myButton.isEnabled = false
            promptView.removeSubviews()
        }
    }
    
    @objc private func postCheckinButton(_ sender:UIButton){
        MBProgressHUD_JChat.showMessage(message: "打卡中...", toView: self.view)
        viewModel.postMyCheckin(checkinForm) { (result) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            switch result {
            case .ok(_):
                DispatchQueue.main.async {
                    MBProgressHUD_JChat.show(text:"打卡成功", view: self.view)
                    self.getCurrentCheckinList()
                }
                break
            case .fail(let errorMessage):
                DispatchQueue.main.async {
                    MBProgressHUD_JChat.show(text:"打卡失败,\n\(errorMessage)", view: self.view)
                }
                break
            default:
                break
            }
        }
    }
    
    @objc private func changePostion(_ pan:UIPanGestureRecognizer){
        
    }
    
    func getWorkPlace() {
        viewModel.getLocationWorkPlace { (myResult) in
            switch myResult {
            case .ok(let result):
                let model = result as? [OOAttandanceWorkPlace]
                DispatchQueue.main.async {
                   self.headerView.workPlaces = model
                }
                break
            case .fail(let s):
                MBProgressHUD_JChat.show(text: "错误:\n\(s)", view: self.view, 2)
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
        let currentDate = Date().toString(format: "yyyy-MM-dd")
        bean.startDate = currentDate
        bean.endDate = currentDate
        MBProgressHUD_JChat.showMessage(message: "loading...", toView: self.view)
        viewModel.getMyCheckinList(model, bean) { (myResult) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            switch myResult {
            case .fail(let s):
                MBProgressHUD_JChat.show(text: "错误:\n\(s)", view: self.view, 2)
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
    
    func postCheckIn() {
        
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
         headerView.stopBMKMapViewService()
    }
}

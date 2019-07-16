//
//  OOMeetingInforController.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/4.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit
import EmptyDataSet_Swift
import Promises

private let meetingIdentifier = "OOMeetingInforItemCell"

class OOMeetingInforController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    private var sectionHeight = CGFloat(172.0)
    
    private var hSectionHeight = CGFloat(387.0)
    
    private var sFlag = false
    
    //指定日期的所有会议
    private var theMeetingsByDay:[OOMeetingInfo] = []
    
    private lazy var viewModel:OOMeetingMainViewModel = {
        return OOMeetingMainViewModel()
    }()
    
    private var headerView:OOMeetingInforHeaderView = {
       let view = Bundle.main.loadNibNamed("OOMeetingInforHeaderView", owner: self, options:nil)?.first as! OOMeetingInforHeaderView
        return view
    }()
    
 
    private lazy var createView:O2BBSCreatorView = {
        let view = Bundle.main.loadNibNamed("O2BBSCreatorView", owner: self, options: nil)?.first as! O2BBSCreatorView
        view.iconImage = #imageLiteral(resourceName: "icon_collection_pencil")
        view.delegate = self
        view.frame = CGRect(x: kScreenW -  50 - 25, y: kScreenH -  50 - 25 - 40, width: 50, height: 50)
        return view
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tabBarItem?.selectedImage = O2ThemeManager.image(for: "Icon.icon_huiyi_pro")
        tableView.register(UINib.init(nibName: "OOMeetingInforItemCell", bundle: nil), forCellReuseIdentifier: meetingIdentifier)
        headerView.delegate = self
        //headerView.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 172)
        //tableView.tableHeaderView = headerView
        tableView.emptyDataSetSource = self
        tableView.emptyDataSetDelegate = self
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadData()
        })
        loadData()
    }
    
    func loadData() {
        MBProgressHUD_JChat.showMessage(message: "loading...", toView: view)
        all(viewModel.getMeetingsByYearAndMonth(Date()),viewModel.getMeetingByTheDay(Date())).then { (result) in
            DispatchQueue.main.async {
                self.headerView.eventsByDate = result.0 as? [String : [OOMeetingInfo]]
                self.viewModel.theMeetingsByDay.removeAll()
                self.viewModel.theMeetingsByDay.append(contentsOf: result.1)
                self.tableView.reloadData()
               
            }
            
            }.always {
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if self.tableView.mj_header.isRefreshing() {
                    self.tableView.mj_header.endRefreshing()
                }
                //self.tableView.reloadData()
            }.catch { (myerror) in
                let customError = myerror as! OOAppError
                MBProgressHUD_JChat.show(text: customError.failureReason ?? "", view: self.view)
        }
    }
    
    
    func loadCurrentMonthCalendar(_ theDate:Date?){
        MBProgressHUD_JChat.showMessage(message: "loading...", toView: view)
        viewModel.getMeetingsByYearAndMonth(theDate ?? Date()).then { (resultDict) in
            self.headerView.eventsByDate = resultDict as? [String : [OOMeetingInfo]]
            }.always {
                  MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            }.catch { (myerror) in
                let customError = myerror as! OOAppError
                MBProgressHUD_JChat.show(text: customError.failureReason ?? "", view: self.view)
        }
    }
    
    func loadtheDayMeetingInfo(_ theDate:Date?){
        MBProgressHUD_JChat.showMessage(message: "loading...", toView: view)
        viewModel.getMeetingByTheDay(theDate ?? Date()).then { (infos) in
            self.viewModel.theMeetingsByDay.removeAll()
            self.viewModel.theMeetingsByDay.append(contentsOf: infos)
            self.tableView.reloadData()
            }.always {
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            }.catch { (myerror) in
                let customError = myerror as! OOAppError
                MBProgressHUD_JChat.show(text: customError.failureReason ?? "", view: self.view)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        UIApplication.shared.windows.first?.addSubview(createView)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        createView.removeFromSuperview()
    }
    
}

// MARK:- EmptyDataSetSource,EmptyDataSetDelegate
extension OOMeetingInforController:EmptyDataSetSource,EmptyDataSetDelegate{
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let text = "您当前还没有会议"
        let titleAttributes = [NSAttributedString.Key.foregroundColor:UIColor(hex:"#CCCCCC"),NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 18)!]
        return  NSMutableAttributedString(string: text, attributes: titleAttributes)
    }
    
    
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        return #imageLiteral(resourceName: "icon_wuhuiyi")
    }
    
    func backgroundColor(forEmptyDataSet scrollView: UIScrollView) -> UIColor? {
        return UIColor(hex:"#F5F5F5")
    }
    
    
    func emptyDataSetShouldDisplay(_ scrollView: UIScrollView) -> Bool {
        return true
    }
}

extension OOMeetingInforController:O2BBSCreatorViewDelegate{
    func creatorViewClicked(_ view: O2BBSCreatorView) {
        print("Creat Meeting")
        self.performSegue(withIdentifier: "showCreateMeetingSgue", sender: nil)
    }
}

// MARK:- OOMeetingInforHeaderViewDelegate
extension OOMeetingInforController:OOMeetingInforHeaderViewDelegate{
    func selectedTheDay(_ theDay: Date?) {
       loadtheDayMeetingInfo(theDay)
    }
    
    func selectedTheMonth(_ theMonth: Date?) {
        loadCurrentMonthCalendar(theMonth)
    }
}

// MARK:- UITableViewDataSource,UITableViewDelegate
extension OOMeetingInforController:UITableViewDataSource,UITableViewDelegate {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSections()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSection(section)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: meetingIdentifier, for: indexPath)
        let uCell = cell as! OOMeetingInforItemCell
        uCell.viewModel = viewModel
        let item = viewModel.nodeForIndexPath(indexPath)
        uCell.config(withItem: item)
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 116.0
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return  headerView.calendarManager?.settings.weekModeEnabled == false ?  hSectionHeight : sectionHeight
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return headerView
    }
}

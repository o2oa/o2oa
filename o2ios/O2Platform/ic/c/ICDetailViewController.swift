//
//  ICDetailViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/25.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import SwiftyJSON

import CocoaLumberjack
import O2OA_Auth_SDK

class ICDetailViewController: UIViewController {
    
    
    @IBOutlet weak var tableView: ZLBaseTableView!
    
    var currentTime:ICTimeComponent?
    
    var detailDatas:[AttendanceDetailEntry] = []
    
    var selectedDetailDatas:[AttendanceDetailEntry] = []
    
    var segmentedControl:SegmentedControl?{
        didSet {
            tabIndex = (segmentedControl?.selectedIndex)!
        }
    }
    
    var filterPredicate = NSPredicate()
    
    var tabIndex:Int = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        self.tableView.emptyTitle = "您没有考勤信息"
        self.tableView.dataSource = self
        self.tableView.delegate = self
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadICDetailData()
        })
        self.loadICDetailData()
    }
    
    func setupUI(){
        self.initSegmentedControl()
    }
    
    func initSegmentedControl(){
        //申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
        let titleStrings = ["全部","未申诉","申诉中","未通过","通过"]
        let titles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: UIColor.black]
            var titles = [NSAttributedString]()
            for titleString in titleStrings {
                let title = NSAttributedString(string: titleString, attributes: attributes)
                titles.append(title)
            }
            return titles
        }()
        let selectedTitles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: base_color]
            var selectedTitles = [NSAttributedString]()
            for titleString in titleStrings {
                let selectedTitle = NSAttributedString(string: titleString, attributes: attributes)
                selectedTitles.append(selectedTitle)
            }
            return selectedTitles
        }()
        self.segmentedControl = SegmentedControl.initWithTitles(titles, selectedTitles: selectedTitles)
        self.segmentedControl!.delegate = self
        self.segmentedControl!.backgroundColor = toolbar_background_color
        self.segmentedControl!.autoresizingMask = [.flexibleRightMargin, .flexibleWidth]
        self.segmentedControl!.selectionIndicatorStyle = .bottom
        self.segmentedControl!.selectionIndicatorColor = base_color
        self.segmentedControl!.selectionIndicatorHeight = 3
        self.segmentedControl!.segmentWidth = SCREEN_WIDTH / CGFloat(titleStrings.count)
        self.segmentedControl!.frame.origin.y = 0
        self.segmentedControl!.frame.size = CGSize(width: UIScreen.main.bounds.width, height: 40)
        view.insertSubview(self.segmentedControl!, belowSubview: navigationController!.navigationBar)
        self.tableView.contentInset = UIEdgeInsets(top: 40, left: 0, bottom: 0, right: 0)
    }
    
    func loadICDetailData(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(icContext.icContextKey, query: icContext.detailMonthPieChartQuery, parameter: nil)
        let filter = AttendanceDetailWrapInFilter()
        filter.cycleYear = currentTime?.year
        filter.cycleMonth = currentTime?.month
        filter.order = "asc"
        filter.q_empName = O2AuthSDK.shared.myInfo()?.name
        self.detailDatas.removeAll()
        Alamofire.request(url!, method: .put, parameters: filter.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    let dataJSON = JSON(val)["data"]
                    let detailArray = Mapper<AttendanceDetailData>().mapArray(JSONString: dataJSON.description)
                    if detailArray != nil {
                        let entrys:[AttendanceDetailEntry] = (detailArray?.map({ (element) -> AttendanceDetailEntry in
                            return AttendanceDetailEntry.generateDetailEntry(detailData: element)
                        }))!
                        self.detailDatas.append(contentsOf: entrys)
                        self.tabIndexFilter(tabIndex:self.tabIndex)
                    }
                }else{
                    DDLogError(JSON(val).description)
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
            if self.tableView.mj_header.isRefreshing() {
                self.tableView.mj_header.endRefreshing()
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showAppealDetailSegue"{
            let destVC = segue.destination as! ICAppealFormViewController
            destVC.detailData = sender as? AttendanceDetailData
        }else if segue.identifier == "showDetailPopoverSegue" {
            let navVC = segue.destination as! ZLNavigationController
            let destVC  = navVC.topViewController as! ICDetailDisplayViewController
            destVC.detailData = sender as? AttendanceDetailData
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    @IBAction func unWindRefreshSegueForFrom(_ segue:UIStoryboardSegue) {
        self.tableView.mj_header.beginRefreshing()
    }
    
    
    
    
   
}

extension ICDetailViewController:SegmentedControlDelegate{
    func segmentedControl(_ segmentedControl: SegmentedControl, didSelectIndex selectedIndex: Int) {
        DDLogDebug("selectedIndex = \(selectedIndex)")
        tabIndex = selectedIndex
        tabIndexFilter(tabIndex: selectedIndex)
       
    }
    func tabIndexFilter(tabIndex selectedIndex:Int){
        switch selectedIndex {
        case 0:
            filterDetailDatas(appealStatus: Int.max)
        case 1:
            filterDetailDatas(appealStatus: 0)
        case 2:
            filterDetailDatas(appealStatus: 1)
        case 3:
            filterDetailDatas(appealStatus: -1)
        case 4:
            filterDetailDatas(appealStatus: 9)
        default:
            DDLogDebug("selectedIndex = \(selectedIndex)")
        }
    }
    
    private func filterDetailDatas(appealStatus:Int,isAppeal:Bool=true){
       self.selectedDetailDatas.removeAll()
        let result = self.detailDatas.filter { (entry) -> Bool in
            if appealStatus == Int.max {
                return true
            }else{
                if entry.appealStatus == appealStatus && entry.isAppeal == isAppeal {
                    return true
                }else{
                    return false
                }
            }
        }
        self.selectedDetailDatas.append(contentsOf: result)
        self.tableView.reloadData()
    }
}



extension ICDetailViewController:AppealDetailCellDelegate{
    func appealDetailAction(_ cell: AppealDetailCell) {
        DDLogDebug(cell.entry?.detailObj?.id ?? "id error")
        let detailObj = cell.entry?.detailObj
        self.performSegue(withIdentifier: "showAppealDetailSegue", sender: detailObj)
    }
}

extension ICDetailViewController:UITableViewDelegate,UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.selectedDetailDatas.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:AppealDetailCell = tableView.dequeueReusableCell(withIdentifier: "appealDetailCell", for: indexPath) as! AppealDetailCell
        cell.entry = self.selectedDetailDatas[indexPath.row]
        cell.delegate = self
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        DDLogDebug("cell clicked")
//        if tabIndex != Int.max {
//            let detailEntry:AttendanceDetailEntry = self.selectedDetailDatas[indexPath.row]
//            self.performSegue(withIdentifier: "showDetailPopoverSegue", sender: detailEntry.detailObj)
//        }
    }
}

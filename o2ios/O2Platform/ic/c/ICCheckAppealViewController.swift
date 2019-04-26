//
//  ICCheckAppealViewController.swift
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

import BWSwipeRevealCell
import CocoaLumberjack
import O2OA_Auth_SDK

class ICCheckAppealViewController: UIViewController {
    
    @IBOutlet weak var tableView: ZLBaseTableView!
    
    var currentTime:ICTimeComponent?
    
    var checkEntrys:[AttendanceCheckEntry] = []
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.emptyTitle = "没有待审批申诉"
        self.tableView.dataSource = self
        self.tableView.delegate  = self
        //self.tableView.setEditing(true, animated: true)
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadAppealCheckData()
        })
        self.loadAppealCheckData()
    }
    
    func loadAppealCheckData(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(icContext.icContextKey, query: icContext.attendanceCheckListQuery, parameter: ["##id##":"(0)" as AnyObject,"##count##":"2000" as AnyObject])
        let filter = AttendanceAppealApprovalWrapInFilter()
        filter.status = "0"
        filter.yearString = currentTime?.year
        filter.processPerson1 = O2AuthSDK.shared.myInfo()?.name
        self.checkEntrys.removeAll()
        Alamofire.request(url!, method: .put, parameters: filter.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            //debugPrint(response)
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    let checkDatas = Mapper<AttendanceAppealInfoData>().mapArray(JSONString: JSON(val)["data"].description)
                    let entrys:[AttendanceCheckEntry] = (checkDatas?.map({ (e) -> AttendanceCheckEntry in
                        return AttendanceCheckEntry.genernateEntry(infoData: e)
                    }))!
                    self.checkEntrys.append(contentsOf: entrys)
                    self.tableView.reloadData()
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

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}

extension ICCheckAppealViewController:UITableViewDataSource,UITableViewDelegate{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.checkEntrys.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:CheckAppealCell = tableView.dequeueReusableCell(withIdentifier: "checkAppealCell", for: indexPath) as! CheckAppealCell
        let entry = self.checkEntrys[indexPath.row]
        cell.entry = entry
        
        let bsCell:BWSwipeRevealCell = cell as BWSwipeRevealCell
        bsCell.bgViewLeftImage = UIImage(named:"Done")!.withRenderingMode(.alwaysTemplate)
        bsCell.bgViewLeftColor = UIColor.green
        bsCell.bgViewRightImage = UIImage(named:"Delete")!.withRenderingMode(.alwaysTemplate)
        bsCell.bgViewRightColor = UIColor.red
        bsCell.type = .slidingDoor
        bsCell.delegate = self
        return cell
    }
    
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
}

extension ICCheckAppealViewController:BWSwipeRevealCellDelegate{
    func swipeCellActivatedAction(_ cell: BWSwipeCell, isActionLeft: Bool) {
        let indexPath = tableView.indexPath(for: cell)!
        let entry = self.checkEntrys[indexPath.row]
        if isActionLeft == true {
            self.acceptAppealCheck((entry.appealObj!), indexPath: indexPath)
        }else{
            self.rejectAppealCheck((entry.appealObj!), indexPath: indexPath)
        }
    }
    
    func acceptAppealCheck(_ appealData:AttendanceAppealInfoData,indexPath:IndexPath){
        DDLogDebug("acceptAppealCheck")
        appealData.status = 1 //同意
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(icContext.icContextKey, query: icContext.attendanceCheckSubmitQuery, parameter: ["##id##":appealData.id! as AnyObject])
        Alamofire.request(url!, method: .put, parameters: appealData.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                     self.tableViewCellUpdate(indexPath)
                }else{
                    DDLogError(JSON(val).description)
                    self.showError(title: "提交失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "提交失败")
            }
        }
        
    }
    
    
    func rejectAppealCheck(_ appealData:AttendanceAppealInfoData,indexPath:IndexPath){
        DDLogDebug("rejectAppealCheck")
        appealData.status = -1 //不同意
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(icContext.icContextKey, query: icContext.attendanceCheckSubmitQuery, parameter: ["##id##":appealData.id! as AnyObject])
        Alamofire.request(url!, method: .put, parameters: appealData.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    self.tableViewCellUpdate(indexPath)
                }else{
                    DDLogError(JSON(val).description)
                    self.showError(title: "提交失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "提交失败")
            }
            }
            
        }
    
    func tableViewCellUpdate(_ indexPath:IndexPath){
        //let meeting = self.meetings[indexPath.row]
        self.checkEntrys.remove(at:indexPath.row)
        tableView.beginUpdates()
        tableView.deleteRows(at: [indexPath], with: .left)
        tableView.endUpdates()
    }
    
    
}

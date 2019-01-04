//
//  MeetingAcceptViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/24.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import AlamofireObjectMapper
import AlamofireImage
import Alamofire
import SwiftyJSON
import ObjectMapper
import BWSwipeRevealCell
import CocoaLumberjack


class MeetingAcceptViewController: UIViewController {
    
    @IBOutlet weak var acceptTableView: ZLBaseTableView!
    
    var meetings:[Meeting] = []
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.acceptTableView.delegate = self
        self.acceptTableView.dataSource = self
        self.acceptTableView.emptyTitle = "没有需要你确认的会议"
        self.loadAcceptMeetings()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func loadAcceptMeetings(){
        ProgressHUD.show("加载中...", interaction: false)
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.meetingListAcceptQuery, parameter: nil)
        self.meetings.removeAll()
        Alamofire.request(url!,method:.get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseArray(queue: nil, keyPath: "data", context: nil, completionHandler: { (response:DataResponse<[Meeting]>) in
            switch response.result {
            case .success(let meetings):
                self.meetings.append(contentsOf: meetings)
                ProgressHUD.showSuccess("加载完成")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                ProgressHUD.showError("加载失败")
            }
            self.acceptTableView.reloadData()
            
        })
    }
    
    func viewWithImageName(_ imageName: String) -> UIView {
        let image = UIImage(named: imageName)
        let imageView = UIImageView(image: image)
        imageView.contentMode = .center
        return imageView
    }
    
    
    
    func tableViewCellUpdate(_ indexPath:IndexPath){
        //let meeting = self.meetings[indexPath.row]
        self.meetings.remove(at: (indexPath as NSIndexPath).row)
        acceptTableView.beginUpdates()
        acceptTableView.deleteRows(at: [indexPath], with: .left)
        acceptTableView.endUpdates()
    }
    
    
    func acceptMeeting(_ meetingId:String,indexPath:IndexPath){
        ProgressHUD.show("提交中...")
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.meetingItemAcceptIdQuery, parameter: ["##id##":meetingId as AnyObject])
        Alamofire.request(url!).responseJSON {
            response in
            switch response.result {
            case .success:
                self.tableViewCellUpdate(indexPath)
                ProgressHUD.showSuccess("提交成功")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                ProgressHUD.showError("提交失败")
                
            }
        }
    }
    
    func rejectMeeting(_ meetingId:String,indexPath:IndexPath){
         let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.meetingItemRejectIdQuery, parameter: ["##id##":meetingId as AnyObject])
        Alamofire.request(url!).responseJSON { response in
            switch response.result {
            case .success:
                self.tableViewCellUpdate(indexPath)
                ProgressHUD.showSuccess("提交成功")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                ProgressHUD.showError("提交失败")
            }
        }
    }
    
}



extension MeetingAcceptViewController:UITableViewDataSource,UITableViewDelegate{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.meetings.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "MeetingAcceptItemCell", for: indexPath) as! MeetingAcceptItemCell
        let meeting = self.meetings[(indexPath as NSIndexPath).row]
         cell.meeting = meeting
        
        let swipeCell:BWSwipeRevealCell = cell as BWSwipeRevealCell
        
        swipeCell.bgViewLeftImage = UIImage(named:"Done")!.withRenderingMode(.alwaysTemplate)
        swipeCell.bgViewLeftColor = UIColor.green
        
        swipeCell.bgViewRightImage = UIImage(named:"Delete")!.withRenderingMode(.alwaysTemplate)
        swipeCell.bgViewRightColor = UIColor.red
        
        swipeCell.type = .slidingDoor

        
        swipeCell.delegate = self

        return cell
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return UIView()
    }
    
    
    
    
}

extension MeetingAcceptViewController:BWSwipeRevealCellDelegate{
    @nonobjc func swipeCellWillRelease(_ cell: BWSwipeCell) {
//        print("Swipe Cell Will Release")
//        if cell.state != .Normal && cell.type != .SlidingDoor {
//            let indexPath: NSIndexPath = acceptTableView.indexPathForCell(cell)!
//            //self.removeObjectAtIndexPath(indexPath)
//        }
    }
    
    @nonobjc func swipeCellActivatedAction(_ cell: BWSwipeCell, isActionLeft: Bool) {
        print("Swipe Cell Activated Action isActionLeft = \(isActionLeft)")
        let indexPath = acceptTableView.indexPath(for: cell)!
        let meeting = self.meetings[(indexPath as NSIndexPath).row]
        if isActionLeft == true {
            self.acceptMeeting(meeting.id!,indexPath: indexPath)
        }else{
            self.rejectMeeting(meeting.id!,indexPath: indexPath)
        }
        
        //self.removeObjectAtIndexPath(indexPath)
    }
    
}



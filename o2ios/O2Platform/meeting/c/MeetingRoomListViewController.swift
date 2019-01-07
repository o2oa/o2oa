//
//  MeetingRoomListViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/25.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack

protocol MeetingRoomPassValueDelegate {
    func selectRoom(_ room:Room)
}


class MeetingRoomListViewController: UIViewController {
    
    
    var startTime:String?
    
    var completeTime:String?
    
    var builds:[Build] = []
    
    var selectRoom = Room()
    
    @IBOutlet weak var roomListTableView: UITableView!
    
    var delegate:MeetingRoomPassValueDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.roomListTableView.delegate  = self
        
        self.roomListTableView.dataSource = self
        
        self.loadBuilds()
    
        // Do any additional setup after loading the view.
    }
    
    func loadBuilds(){
        let url = getLoadBuildsUrl()
        self.builds.removeAll()
        ProgressHUD.show("加载中",interaction: false)
        Alamofire.request(url).responseArray(queue: nil, keyPath: "data", context: nil, completionHandler: { (response:DataResponse<[Build]>) in
            switch response.result {
            case .success(let builds):
                self.builds.append(contentsOf: builds)
                ProgressHUD.showSuccess("加载成功")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                ProgressHUD.showError("加截失败")
            }
            self.roomListTableView.reloadData()
        })
    }
    
    fileprivate func getLoadBuildsUrl()->String{
        var url = ""
        if startTime != nil && completeTime != nil {
           url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.buildListStartAndCompletedQuery, parameter: ["##start##":startTime! as AnyObject,"##completed##":completeTime! as AnyObject])!
        }else{
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.buildListQuery, parameter: nil)!
        }
        return url
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func btnCloseWindow(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true) {
            //self.delegate?.selectRoom(self.selectRoom)
        }
    }
    

}

extension MeetingRoomListViewController:UITableViewDelegate{
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let sRoom = self.builds[(indexPath as NSIndexPath).section].roomList![(indexPath as NSIndexPath).row]
        self.selectRoom = sRoom
        self.dismiss(animated: true) {
            self.delegate?.selectRoom(self.selectRoom)
        }
    }
    
}

extension MeetingRoomListViewController:UITableViewDataSource{
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.builds.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return (self.builds[section].roomList!).count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MeetingRoomItemCell", for: indexPath) as! MeetingRoomItemCell
        let room = self.builds[(indexPath as NSIndexPath).section].roomList![(indexPath as NSIndexPath).row]
        cell.meetingRoom = room
        return cell
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        let build = self.builds[section]
        return build.name
    }
    
}

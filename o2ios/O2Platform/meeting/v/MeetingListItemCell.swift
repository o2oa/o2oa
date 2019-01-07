//
//  MeetingListItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/22.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireObjectMapper
import CocoaLumberjack


class MeetingListItemCell: UITableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var locationLabel: UILabel!
    
    @IBOutlet weak var timeLabel: UILabel!
    
    var meeting:Meeting? {
        didSet {
            self.titleLabel.text = meeting?.subject
            let startDate =  SharedDateUtil.dateFromString(string: (meeting?.startTime)!,withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime)
            let completeDate = SharedDateUtil.dateFromString(string: (meeting?.completedTime)!,withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime)
            self.timeLabel.text = "\(SharedDateUtil.year(date: startDate))-\(SharedDateUtil.month(date: startDate))-\(SharedDateUtil.day(date: startDate))(\(SharedDateUtil.hour(date: startDate)):\(SharedDateUtil.minute(date: startDate))-\(SharedDateUtil.hour(date:completeDate)):\(SharedDateUtil.minute(date: completeDate)))"
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.roomItemIdQuery, parameter: ["##id##":(meeting?.room)! as AnyObject])
            self.loadRoom(url!)
          
        }
    }
    
    func loadRoom(_ url:String){
        Alamofire.request(url).responseObject(keyPath:"data") { (response:DataResponse<Room>) in
            switch response.result {
            case .success(let room):
                DDLogDebug(room.name!)
                self.locationLabel.text = room.name
                self.layoutIfNeeded()
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        }
    }
    

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
   
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

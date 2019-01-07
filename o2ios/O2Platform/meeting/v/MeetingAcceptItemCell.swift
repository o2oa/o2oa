//
//  MeetingAcceptItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/24.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import BWSwipeRevealCell
import CocoaLumberjack

class MeetingAcceptItemCell:BWSwipeRevealCell {
    
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var personLabel: UILabel!
    
    @IBOutlet weak var locationLabel: UILabel!
    
    @IBOutlet weak var timeLabel: UILabel!
    
    
    var meeting:Meeting?{
        didSet {
            self.titleLabel.text = meeting?.subject
            
            self.personLabel.text = meeting?.invitePersonList?.joined(separator: ",")
            
            let startDate =  SharedDateUtil.dateFromString(string: (meeting?.startTime)!,withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime)
            let completeDate = SharedDateUtil.dateFromString(string: (meeting?.completedTime)!,withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime)
            self.timeLabel.text = "\(SharedDateUtil.year(date: startDate))-\(SharedDateUtil.month(date: startDate))-\(SharedDateUtil.day(date: startDate))(\(SharedDateUtil.hour(date: startDate)):\(SharedDateUtil.minute(date: startDate))-\(SharedDateUtil.hour(date: completeDate)):\(SharedDateUtil.minute(date: completeDate)))"
            
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.roomItemIdQuery, parameter: ["##id##":(meeting?.room)! as AnyObject])
            self.loadRoom(url!)
        }
    }
    
    func loadRoom(_ url:String){
        Alamofire.request(url).responseObject(queue: nil, keyPath: "data", mapToObject: nil, context: nil, completionHandler: { (response:DataResponse<Room>) in
            switch response.result {
            case .success(let room):
                DDLogDebug(room.name!)
                self.locationLabel.text = room.name
                self.layoutIfNeeded()
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        })
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
}

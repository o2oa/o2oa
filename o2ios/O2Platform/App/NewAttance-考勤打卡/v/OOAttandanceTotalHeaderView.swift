//
//  OOAttandanceTotalHeaderView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/24.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOAttandanceTotalHeaderView: UIView,Configurable {
    
    
    @IBOutlet weak var checkinDateButton: UIButton!
    
    @IBOutlet weak var checkinStartTimeLabel: UILabel!
    
    @IBOutlet weak var checkinEndTimeLabel: UILabel!
    
    @IBOutlet weak var normalLabel: UILabel!
    
    @IBOutlet weak var lateLabel: UILabel!
    
    @IBOutlet weak var leaveLabel: UILabel!
    
    @IBOutlet weak var abnormalLabel: UILabel!
    
    var requestBean:OOAttandanceCycleDetail = OOAttandanceCycleDetail() {
        didSet {
            let buttonText = "\(requestBean.cycleYear ?? "")-\(requestBean.cycleMonth ?? "")"
            checkinDateButton.setTitle(buttonText, for: .normal)
            checkinStartTimeLabel.text = requestBean.cycleStartDate
            checkinEndTimeLabel.text = requestBean.cycleEndDate
        }
    }
    
    override func awakeFromNib() {
        
        // button标题的偏移量
        self.checkinDateButton.titleEdgeInsets = UIEdgeInsets(top: 0,left: -((self.checkinDateButton.imageView?.bounds.size.width)! + 2), bottom: 0, right: (self.checkinDateButton.imageView?.bounds.size.width)!);
        // button图片的偏移量
        self.checkinDateButton.imageEdgeInsets = UIEdgeInsets(top: 0, left: (self.checkinDateButton.titleLabel?.bounds.size.width)!, bottom: 0, right: -((self.checkinDateButton.titleLabel?.bounds.size.width)!));
 
        
        normalLabel.layer.cornerRadius = 20
        normalLabel.layer.masksToBounds = true
        
        lateLabel.layer.cornerRadius = 20
        lateLabel.layer.masksToBounds = true
        
        leaveLabel.layer.cornerRadius = 20
        leaveLabel.layer.masksToBounds = true
        
        abnormalLabel.layer.cornerRadius = 20
        abnormalLabel.layer.masksToBounds = true
    }
    
    func config(withItem item: Any?) {
        guard let model = item as? OOAttandanceAnalyze else {
            return
        }
        
        normalLabel.text = String(model.onDutyTimes!)
        lateLabel.text = String(model.lateTimes!)
        leaveLabel.text = String(model.leaveEarlyTimes!)
        abnormalLabel.text = String(model.onSelfHolidayCount!)
        
    }
    
    
    @IBAction func selectTheDateAction(_ sender: Any) {
        NotificationCenter.post(customeNotification: .staticsTotal)
    }
    
    

}

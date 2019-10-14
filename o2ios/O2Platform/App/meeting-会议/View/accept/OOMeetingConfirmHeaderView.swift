//
//  OOMeetingConfirmHeaderView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/22.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit
import BetterSegmentedControl

protocol OOMeetingConfirmHeaderViewDelegate {
    func confirmHeaderView(_ segmentedControlIndex: Int)
}

class OOMeetingConfirmHeaderView: UIView {
    
    @IBOutlet weak var control1: BetterSegmentedControl!
    
    var delegate:OOMeetingConfirmHeaderViewDelegate?
    
    override func awakeFromNib() {
        control1.bounds  = CGRect(x: 0, y: 0, width: kScreenW, height: 40)
        control1.segments = LabelSegment.segments(withTitles: ["收到的邀请","发出的邀请"])
        
        
//        control1.normalFont = UIFont(name: "PingFangSC-Regular", size: 16.0)!
//        control1.selectedFont = UIFont(name: "PingFangSC-Regular", size: 16.0)!
//        control1.titles = ["收到的邀请","发出的邀请"]
////        control1.autoresizingMask = [.flexibleWidth,.flexibleHeight]
//        control1.titleFont = UIFont(name: "PingFangSC-Regular", size: 16.0)!
//        control1.selectedTitleFont = UIFont(name: "PingFangSC-Regular", size: 16.0)!
    }
    
    
    @IBAction func betterSegmentedControlClick(_ sender: BetterSegmentedControl) {
        guard let block = delegate else {
            return
        }
        block.confirmHeaderView(sender.index)
    }
    
}

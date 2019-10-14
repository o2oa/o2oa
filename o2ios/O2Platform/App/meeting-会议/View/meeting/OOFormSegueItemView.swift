//
//  OOFormSegueItemView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/26.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOFormSegueItemView: OOFormBaseView,OOFormConfigEnable {
    
    @IBOutlet weak var titleNameLabel: UILabel!
    
    @IBOutlet weak var showValueLabel: UILabel!
    
    var backCallAction:((_ sender:Any?) -> Void)?
    
    // 选择会议室
    @IBAction func chooseRoomAction(_ sender: UITapGestureRecognizer) {
        guard  let block = backCallAction else {
            return
        }
        block(sender)
    }
    override func awakeFromNib() {
        showValueLabel.isHidden = true
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    func configItem(_ model: OOFormBaseModel) {
        self.model = model
        titleNameLabel.text = self.model?.titleName
    }
    
    // 选择会议室
    @IBAction func performVC(_ sender: Any) {
        guard  let block = backCallAction else {
            return
        }
        block(sender)
    }
    
    func setBackValueUpdate(_ room:OOMeetingRoomInfo){
        self.model?.callbackValue = room
        self.showValueLabel.text = room.name
        self.showValueLabel.isHidden = false
    }
    
    
    
}


//
//  NewMainItemTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/12.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class NewMainItemTableViewCell: UITableViewCell {
    
    @IBOutlet weak var categoryNameLabel: UILabel!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var timeLabel: UILabel!
    
    var model:NSObject! {
        didSet{
            if model.isKind(of: CMS_PublishInfo.self){
                let m = model as! CMS_PublishInfo
                self.categoryNameLabel.text = "【\(m.categoryName!)】"
                self.titleLabel.text = m.title
                self.timeLabel.text = m.publishTime?.split(separator: " ").first?.description
            }else if(model.isKind(of: TodoTask.self)){
                let m = model as! TodoTask
                self.categoryNameLabel.text = "【\(m.applicationName!)】"
                var title = ""
                if  m.title == nil || m.title?.isEmpty == true {
                    title = "无标题"
                }else {
                    title = m.title!
                }
                self.titleLabel.text = title
                self.timeLabel.text = m.updateTime?.split(separator:" ").first?.description
                
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

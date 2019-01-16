//
//  SettingHomeCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/6.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class SettingHomeCell: UITableViewCell {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var statusLabel: UILabel!
    
    
    var cellModel:SettingHomeCellModel?{
        didSet {
            //设置
            self.iconImageView.image = UIImage(named: (cellModel?.iconName)!)
            self.titleLabel.text = cellModel?.title
            
            if let text = cellModel?.status {
                self.statusLabel.text = text
            }else{
                self.statusLabel.text = ""
            }
        }
    }
    
//    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
//        super.init(style: style, reuseIdentifier: reuseIdentifier)
//    }
//    
//    required init?(coder aDecoder: NSCoder) {
//        fatalError("init(coder:) has not been implemented")
//    }

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    
    
    

}

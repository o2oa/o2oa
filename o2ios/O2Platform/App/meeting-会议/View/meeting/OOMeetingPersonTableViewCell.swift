//
//  OOMeetingPersonTableViewCell.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/31.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOMeetingPersonTableViewCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var mobileLabel: UILabel!
    
    @IBOutlet weak var deptLabel: UILabel!
    
    @IBOutlet weak var activityContainerView: UIVisualEffectView!
    
    var viewModel:OOMeetingCreateViewModel?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.iconImageView.layer.cornerRadius = 20
        self.iconImageView.layer.masksToBounds = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let p = item as? OOPersonModel else {
            return
        }
        nameLabel.text = p.name
        mobileLabel.text = p.mobile ?? "未填写手机号"
        viewModel?.getIconOfPerson(p, compeletionBlock: { (iconImage, errMSg) in
            self.activityContainerView.alpha = 0
            if  let errMSG = errMSg {
                if p.genderType == "f" {
                    self.iconImageView.image = #imageLiteral(resourceName: "icon_men")
                }else if p.genderType == "m"{
                    self.iconImageView.image = #imageLiteral(resourceName: "icon_women")
                }else {
                    self.iconImageView.image = iconImage
                }
                
                print(errMSG)
            }else{
                self.iconImageView.image = iconImage
            }
        })
       
        
    }
    
}



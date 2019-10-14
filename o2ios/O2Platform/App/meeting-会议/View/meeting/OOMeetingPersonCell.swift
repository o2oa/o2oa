//
//  OOMeetingPersonCell.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/29.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOMeetingPersonCell: UICollectionViewCell,Configurable {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    
    @IBOutlet weak var activityContainerView: UIVisualEffectView!
    
    @IBOutlet weak var deleteButton: UIButton!
    
    var viewModel:OOMeetingCreateViewModel?
    
    private var model:OOPersonModel?
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    
//    @IBAction func delectPersonAction(_ sender: UIButton) {
//        print("delectPersonAction")
//        //发送需要删除的p
//
//    }
    
    func config(withItem item: Any?) {
        guard let p = item as? OOPersonModel else {
            return
        }
        nameLabel.text = p.name
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

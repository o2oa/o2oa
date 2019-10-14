//
//  OOContactPersonCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/24.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

import SDWebImage

class OOContactPersonCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var mobileLabel: UILabel!
    
    var viewModel:OOPersonListViewModel?
    
  
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius = 20
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let person = item as? OOPersonModel else {
            return
        }
        self.nameLabel.text = person.name
        self.mobileLabel.text = person.mobile
        viewModel?.getIconOfPerson(person, compeletionBlock: { (iconImage, errMsg) in
            
            if errMsg == nil {
                self.iconImageView.image = iconImage
            }else{
                print(errMsg)
            }
            
        })
        
        
    }
    
}

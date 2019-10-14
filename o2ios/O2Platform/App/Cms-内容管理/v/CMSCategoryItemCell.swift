//
//  CMSCategoryItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit


class CMSCategoryItemCell: UITableViewCell {
    
    @IBOutlet weak var appIconImageView: UIImageView!
    
    @IBOutlet weak var appNameLabel: UILabel!
    
    @IBOutlet weak var arrowImageView: UIImageView!
    
    
    var cmsData:CMSData? {
        didSet {
            if  let appIcon = cmsData?.appIcon {
                appIconImageView.image = UIImage.base64ToImage(appIcon, defaultImage: UIImage(named:"icon_cms_application_default")!)
            }else{
                appIconImageView.image = UIImage(named:"icon_cms_application_default")!
            }
            appNameLabel.text = cmsData?.appName
            
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        self.appIconImageView.layer.cornerRadius = 5
        self.appIconImageView.layer.masksToBounds = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

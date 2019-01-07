//
//  AppCategoryHeaderView.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import SDWebImage

class AppCategoryHeaderView: UICollectionReusableView {
    
    @IBOutlet weak var appIconImageView: UIImageView!
    
    @IBOutlet weak var appTitleLabel: UILabel!
    
    var app:Application? {
        didSet {
            self.appTitleLabel.text = app?.name
            if let icon = app?.icon {
            self.appIconImageView.image = UIImage.sd_image(with: Data(base64Encoded:icon, options:NSData.Base64DecodingOptions.ignoreUnknownCharacters))
            }else{
                self.appIconImageView.image = UIImage(named: "apps_selected")
            }
        }
    }
    
    
    override func awakeFromNib() {
        self.appTitleLabel.textColor = UIColor.white
    }
    
    
    
}

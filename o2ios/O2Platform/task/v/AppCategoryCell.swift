//
//  AppCategoryCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import SDWebImage

class AppCategoryCell: UICollectionViewCell {
    
    @IBOutlet weak var moduleIconImageView: UIImageView!
    
    @IBOutlet weak var moduleTitleLabel: UILabel!
    
    var appProcess:AppProcess? {
        didSet {
            if let icon = appProcess?.icon {
                self.moduleIconImageView.image = UIImage.sd_image(with: Data(base64Encoded: icon, options:NSData.Base64DecodingOptions.ignoreUnknownCharacters))
            }else{
                self.moduleIconImageView.image = UIImage(named: "todoList")
            }
            self.moduleTitleLabel.text  = appProcess?.name
        }
    }
    
}

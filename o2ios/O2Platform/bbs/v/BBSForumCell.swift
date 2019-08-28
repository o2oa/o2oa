//
//  BBSForumCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/3.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class BBSForumCell: UICollectionViewCell {
    
    @IBOutlet weak var bbsSectionTitleLabel: UILabel!
    
    @IBOutlet weak var bbsSectionIconImageView: UIImageView!
    
    var bbsSectionData:BBSectionListData? {
        didSet {
            self.bbsSectionTitleLabel.text = bbsSectionData?.sectionName
            if bbsSectionData?.icon != nil {
                self.bbsSectionIconImageView.image =  UIImage.sd_image(with: Data(base64Encoded: (bbsSectionData?.icon)!, options:NSData.Base64DecodingOptions.ignoreUnknownCharacters))
            }else {
                self.bbsSectionIconImageView.image = UIImage(named: "icon_forum_default")
            }
            
//            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(BBSContext.bbsContextKey, query: BBSContext.bbsSectionIconQuery, parameter: ["##id##":bbsSectionData?.id as AnyObject], generateTime: false)
//            let url = URL(string: urlstr!)
//            self.bbsSectionIconImageView.hnk_setImageFromURL(url!)
        }
    }
    
    override func awakeFromNib() {
        self.bbsSectionIconImageView.layer.cornerRadius = 5
        self.bbsSectionIconImageView.layer.masksToBounds = true
        self.bbsSectionIconImageView.clipsToBounds = true
    }
    
    
    
}

//
//  MainPublishHeaderView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/7.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class MainPublishHeaderView: UICollectionReusableView {
    
    @IBOutlet weak var headerImageView: UIImageView!
    
    override func awakeFromNib() {
        headerImageView.frame = CGRect(x: 0, y: 0, width: 65, height: 100)
        
    }
    
        
}

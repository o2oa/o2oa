//
//  BBSHeaderCollectionReusableView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/4.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class BBSHeaderCollectionReusableView: UICollectionReusableView {
    
    @IBOutlet weak var headerIconImageView: UIImageView!
    
    @IBOutlet weak var forumTitleLabel: UILabel!
    
    var bbsForumData:BBSForumListData? {
        didSet {
            self.forumTitleLabel.text = bbsForumData?.forumName
       }
        
    }
    
    override func awakeFromNib() {
        
    }
        
}

//
//  MainPublishFooterView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/7.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

protocol MainPublishHeaderViewDelegate {
    func showMorePublishInfo()
}

class MainPublishFooterView: UICollectionReusableView {
        
    @IBOutlet weak var moreButton: UIButton!
    
     var delegate:MainPublishHeaderViewDelegate?
    
    @IBAction func showMorePublishInfo(_ sender: UIButton) {
        print("showMorePublishInfo")
        NotificationCenter.default.post(name: NSNotification.Name("SHOW_MORE_PUBLISH"), object: nil)
//        if let d = delegate {
//           d.showMorePublishInfo()
//        }
    }
    
}

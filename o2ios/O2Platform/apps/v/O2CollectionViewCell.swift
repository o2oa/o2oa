//
//  O2CollectionViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class O2CollectionViewCell: UICollectionViewCell {
    
    override func awakeFromNib() {
        self.backgroundColor = UIColor.white
    }
    
    
    @IBOutlet weak var appIconImageView: UIImageView!
    
    
    @IBOutlet weak var appTitle: UILabel!
    
    
    func initImg(app:O2App){
        
        let storeBoard = app.storyBoard
        if storeBoard == "webview" {
            guard let iconUrl = AppDelegate.o2Collect.generateURLWithAppContextKey(ApplicationContext.applicationContextKey2, query: ApplicationContext.applicationIconQuery, parameter: ["##applicationId##":app.appId! as AnyObject]) else {
                DDLogError("没有获取到icon的url。。。。。。")
                return
            }
            
            let url = URL(string: iconUrl)
            let size = self.appIconImageView.bounds.size
            if size.width == 0 {
                self.appIconImageView.bounds.size = CGSize(width: 38, height: 38)
            }
            self.appIconImageView.image = UIImage(named: app.normalIcon!)
            self.appIconImageView.highlightedImage = UIImage(named: app.normalIcon!)
            let cache = Shared.imageCache
            let format = HanekeGlobals.UIKit.formatWithSize(CGSize(width: 38, height: 38), scaleMode: .AspectFill)
            let formatName = format.name
            cache.addFormat(format)
            let fetcher = NetworkFetcher<UIImage>(URL: url!)
            cache.fetch(fetcher: fetcher, formatName: formatName).onSuccess { image in
                self.appIconImageView.image = image
                self.appIconImageView.highlightedImage = image
            }
        }else {
            self.appIconImageView.image = UIImage(named: app.normalIcon!)
            self.appIconImageView.highlightedImage = UIImage(named: app.normalIcon!)
        }
        
    }
    
}

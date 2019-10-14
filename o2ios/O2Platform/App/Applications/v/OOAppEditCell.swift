//
//  OOAppEditCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/10.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class OOAppEditCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var appIcon: UIImageView!
    
    @IBOutlet weak var appNameLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let app = item as? O2App else {
            return
        }
        //名字
        appNameLabel.text = app.title
        
        initImg(app: app)
    }
    
    private func initImg(app:O2App){
        let storeBoard = app.storyBoard
        if storeBoard == "webview" {
            guard let iconUrl = AppDelegate.o2Collect.generateURLWithAppContextKey(ApplicationContext.applicationContextKey2, query: ApplicationContext.applicationIconQuery, parameter: ["##applicationId##":app.appId! as AnyObject]) else {
                DDLogError("没有获取到icon的url。。。。。。")
                return
            }
            
            let url = URL(string: iconUrl)
            let size = self.appIcon.bounds.size
            if size.width == 0 {
                self.appIcon.bounds.size = CGSize(width: 38, height: 38)
            }
            self.appIcon.image = UIImage(named: app.normalIcon!)
            self.appIcon.highlightedImage = UIImage(named: app.normalIcon!)
            self.appIcon.hnk_setImageFromURL(url!)
//            let cache = Shared.imageCache
//            let format = HanekeGlobals.UIKit.formatWithSize(CGSize(width: 38, height: 38), scaleMode: .AspectFill)
//            let formatName = format.name
//            cache.addFormat(format)
//            let fetcher = NetworkFetcher<UIImage>(URL: url!)
//            cache.fetch(fetcher: fetcher, formatName: formatName).onSuccess { image in
//                self.appIcon.image = image
//                self.appIcon.highlightedImage = image
//            }
        }else {
            self.appIcon.image = UIImage(named: app.normalIcon!)
            self.appIcon.highlightedImage = UIImage(named: app.normalIcon!)
        }
        
    }

}

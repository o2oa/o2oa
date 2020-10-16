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
    
    private var nowData:O2App?
    
    override func prepareForReuse() {
        super.prepareForReuse()
        self.appIconImageView.image = nil
    }
    
    func setAppData(app: O2App) {
        self.nowData = app
        if let storeBoard = app.storyBoard, storeBoard == "webview" {
            if let iconUrl = AppDelegate.o2Collect.generateURLWithAppContextKey(ApplicationContext.applicationContextKey2, query: ApplicationContext.applicationIconQuery, parameter: ["##applicationId##":app.appId! as AnyObject])  {
                let url = URL(string: iconUrl)
                let size = self.appIconImageView.bounds.size
                if size.width == 0 {
                    self.appIconImageView.bounds.size = CGSize(width: 38, height: 38)
                }
                self.appIconImageView.image = UIImage(named: app.normalIcon!)
                self.appIconImageView.highlightedImage = UIImage(named: app.normalIcon!)
                self.appIconImageView.hnk_setImageFromURL(url!, placeholder: UIImage(named: app.normalIcon!), format: nil, failure: { (err) in
                    self.appIconImageView.image = UIImage(named: app.normalIcon!)
                }) { image in
                    if self.nowData?.appId == app.appId {
                        self.appIconImageView.image = image
                        
                    }
                }
            } else{
                self.appIconImageView.image = UIImage(named: app.normalIcon!)
                self.appIconImageView.highlightedImage = UIImage(named: app.selectedIcon!)
            }
            
        } else{
            self.appIconImageView.image = UIImage(named: app.normalIcon!)
            self.appIconImageView.highlightedImage = UIImage(named: app.selectedIcon!)
        }
        self.appTitle.text = app.title
    }
   
    
    
}

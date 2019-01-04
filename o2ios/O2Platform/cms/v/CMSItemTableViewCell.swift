//
//  CMSItemTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class CMSItemTableViewCell: UITableViewCell {
    
    @IBOutlet weak var itemIconImageView: UIImageView!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var itemTimeLabel: UILabel!
    
    var itemData:CMSCategoryItemData? {
        didSet {
            //图像
            //let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.personIconByNameQuery, parameter: ["##name##":itemData?.creatorPerson as AnyObject])
            //self.itemIconImageView.af_setImage(withURL: URL(string: url!)!)
            //self.categoryLabel.text =  "【\((itemData?.categoryName!)!)】"
            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":itemData?.creatorPerson as AnyObject], generateTime: false)
            let url = URL(string: urlstr!)
            self.itemIconImageView.hnk_setImageFromURL(url!)
            self.titleLabel.text = itemData?.title
            self.itemTimeLabel.text = itemData?.updateTime?.split(" ")[0]
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        self.itemIconImageView.layer.cornerRadius = 15
        self.itemIconImageView.layer.masksToBounds = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

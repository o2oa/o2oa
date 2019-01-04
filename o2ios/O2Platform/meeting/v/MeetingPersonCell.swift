//
//  MeetingPersonCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/30.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class MeetingPersonCell: UITableViewCell {
    
    @IBOutlet weak var personIconImageView: UIImageView!
    
    @IBOutlet weak var personNameLabel: UILabel!
    
    var person:PersonV2?{
        didSet {
            self.personNameLabel.text = person?.name
            //self.personIconImageView.image = UIImage.base64ToImage(person?.icon != nil ? (person?.icon)! : "")
            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":person?.unique as AnyObject], generateTime: false)
            let url = URL(string: urlstr!)
            self.personIconImageView.hnk_setImageFromURL(url!, placeholder: UIImage(named: "personDefaultIcon"), format: nil, failure: nil, success: nil)
        
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        self.personIconImageView.layer.masksToBounds = true
        self.personIconImageView.layer.cornerRadius = 15
        self.personIconImageView.clipsToBounds = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

//
//  SubjectTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/4.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import SDWebImage
import Alamofire
import ObjectMapper
import CocoaLumberjack

class SubjectTableViewCell: UITableViewCell {
    
    @IBOutlet weak var subjectPersonIconImageView: UIImageView!
    
    @IBOutlet weak var topSubjectImageView: UIImageView!
    
    @IBOutlet weak var subjectTitleLabel: UILabel!

    @IBOutlet weak var subjectNameLabel: UILabel!
    
    @IBOutlet weak var subjectPubDateLabel: UILabel!
    
    @IBOutlet weak var subjectViewNumberLabel: UILabel!
    
    @IBOutlet weak var subjectReplyNumberLabel: UILabel!
    
    var bbsSubjectData:BBSSubjectData? {
        didSet {
            self.topSubjectImageView.isHidden = !(bbsSubjectData?.isTopSubject)!
            self.subjectTitleLabel.text = bbsSubjectData?.title
            self.subjectNameLabel.text = (bbsSubjectData?.creatorName?.contains("@"))! ? bbsSubjectData?.creatorName?.split("@")[0] : bbsSubjectData?.creatorName
            self.subjectPubDateLabel.text = bbsSubjectData?.createTime
            self.subjectViewNumberLabel.text = bbsSubjectData?.viewTotal?.toString
            self.subjectReplyNumberLabel.text = bbsSubjectData?.replyTotal?.toString
            //let urlString = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.personIconByNameQuery, parameter: ["##name##":bbsSubjectData?.creatorName as AnyObject])
            //let url = URL(string: urlString!)
            
            //self.subjectPersonIconImageView.af_setImage(withURL: url!)
            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":bbsSubjectData?.creatorName as AnyObject], generateTime: false)
            let url = URL(string: urlstr!)
            self.subjectPersonIconImageView.hnk_setImageFromURL(url!)
        }
    }
    

    override func awakeFromNib() {
        super.awakeFromNib()
        //图像变圆形
        self.subjectPersonIconImageView.layer.cornerRadius = 20
        self.subjectPersonIconImageView.layer.masksToBounds = true
        self.subjectPersonIconImageView.clipsToBounds = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

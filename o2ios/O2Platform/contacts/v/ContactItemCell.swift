//
//  ContactItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/12.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper

class ContactItemCell: UITableViewCell {
    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var headBarView: UITextView!
    @IBOutlet weak var iconTagLabel: UILabel!
    
    var cellViewModel:CellViewModel? {
        didSet {
            configCellViewModel()
        }
    }
    
    override func awakeFromNib() {
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    private func configCompany(){
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.image = O2ThemeManager.image(for: "Icon.icon_company")
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
    }
    
    private func configDepart(){
        nameLabel.text = cellViewModel?.name
        iconTagLabel.isHidden  = false
        iconImageView.image = O2ThemeManager.image(for: "Icon.pic_oval")
        iconImageView.frame = CGRect(x: 18, y: 5, width: 40, height: 40)
        iconImageView.layer.masksToBounds = true
        iconImageView.layer.cornerRadius = 40 / 2.0
        if let orgName = cellViewModel?.name,orgName != "" {
            iconTagLabel.text = NSString(string: orgName).substring(to: 1)
            iconTagLabel.center = iconImageView.center
        }
        nameLabel.font = UIFont(name: "PingFang SC", size: 14)
        nameLabel.frame.x = 66
    }
    
    private func configGroup(){
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.image = O2ThemeManager.image(for: "Icon.icon_group")
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
    }
    
    private func configPerson(_ p:PersonV2){
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
        let person  = p
        let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":person.id as AnyObject], generateTime: false)
        let url = URL(string: urlstr!)
        self.iconImageView.hnk_setImageFromURL(url!)
    }
    
    private func configIdentity(_ i:IdentityV2){
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
        let identity  = i
        let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":identity.person as AnyObject], generateTime: false)
        let url = URL(string: urlstr!)
        self.iconImageView.hnk_setImageFromURL(url!)
    }
    
    private func configTitle(_ t:HeadTitle){
        let title = t
        if title.isBar {
            headBarView.textContainer.maximumNumberOfLines = 1
            headBarView.textContainer.lineBreakMode = .byTruncatingTail
            headBarView.text = ""
            if let bars = title.barText {
                bars.forEachEnumerated { (index, bar) in
                    if bars.count == (index+1) {
                        self.headBarView.appendLinkString(string: bar.name ?? "")
                    }else{
                        self.headBarView.appendLinkString(string: (bar.name ?? "") + " > ", withURLString: "reloadto:\(bar.id ?? "")")
                    }
                }
            }
        }else{
            nameLabel.text = cellViewModel?.name
            iconTagLabel.isHidden = true
            iconImageView.image = UIImage(named: title.icon!)
            iconImageView.frame = CGRect(x: 8, y: 10, width: 30, height: 30)
            iconImageView.layer.masksToBounds = true
            iconImageView.layer.cornerRadius = 15.0
            nameLabel.font = UIFont(name: "PingFang SC", size: 16.0)
            nameLabel.frame.x = 66
            
        }
    }
    
    
    
    func configCellViewModel()  {
        let dataType = cellViewModel?.dataType
        switch dataType! {
        case .company(_):
            configCompany()
            break
        case .depart(_):
            configDepart()
            break
        case .group(_):
            configGroup()
            break
        case .person(let p):
            configPerson(p as! PersonV2)
            break
        case .identity(let i):
            configIdentity(i as! IdentityV2)
        case .title(let t):
            configTitle(t as! HeadTitle)
            break
        default:
            break
        }
    }

}

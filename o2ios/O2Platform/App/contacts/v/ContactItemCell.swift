//
//  ContactItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/12.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

protocol ContactItemCellBreadcrumbClickDelegate {
    func breadcrumbTap(name: String, distinguished: String)
}

class ContactItemCell: UITableViewCell {
    
    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var iconTagLabel: UILabel!
    // 面包屑导航栏
    @IBOutlet weak var headBarScrollView: UIScrollView!
    
    var delegate: ContactItemCellBreadcrumbClickDelegate?
    
    var cellViewModel:CellViewModel? {
        didSet {
            self.configCellViewModel()
        }
    }
    
    override func awakeFromNib() {
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func configCellViewModel()  {
        let dataType = cellViewModel?.dataType
        switch dataType! {
        case .company(_):
            self.configCompany()
            break
        case .depart(_):
            self.configDepart()
            break
        case .group(_):
            self.configGroup()
            break
        case .person(let p):
            self.configPerson(p as! PersonV2)
            break
        case .identity(let i):
            self.configIdentity(i as! IdentityV2)
        case .title(let t):
            self.configTitle(t as! HeadTitle)
            break
        }
    }
    
    private func configCompany(){
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.image = O2ThemeManager.image(for: "Icon.icon_company")
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
    }
    
    private func configDepart() {
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden  = false
        self.iconImageView.image = O2ThemeManager.image(for: "Icon.pic_oval")
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius = 40 / 2.0
        if let orgName = cellViewModel?.name,orgName != "" {
            self.iconTagLabel.text = NSString(string: orgName).substring(to: 1)
            self.iconTagLabel.center = iconImageView.center
        }
        self.nameLabel.font = UIFont(name: "PingFang SC", size: 14)
        
    }
    private func configGroup() {
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.image = O2ThemeManager.image(for: "Icon.icon_group")
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
    }
    
    private func configPerson(_ p: PersonV2) {
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
        let person = p
        let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":person.id as AnyObject], generateTime: false)
        let url = URL(string: urlstr!)
        self.iconImageView.hnk_setImageFromURL(url!)
    }
    
    private func configIdentity(_ id: IdentityV2) {
        self.nameLabel.text = cellViewModel?.name
        self.iconTagLabel.isHidden = true
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  self.iconImageView.width / 2.0
        let identity  = id
        let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":identity.person as AnyObject], generateTime: false)
        let url = URL(string: urlstr!)
        self.iconImageView.hnk_setImageFromURL(url!)
    }
    
    private func configTitle(_ t: HeadTitle) {
        let title = t
        if title.isBar {
            self.headBarScrollView.removeSubviews()
            var oX = CGFloat(4.0)
            if let bars = title.barText {
                bars.forEachEnumerated { (index, bar) in
                    var name: String
                    var textColor:UIColor
                    if bars.count == (index+1) {
                        name = bar.name ?? ""
                        textColor = base_color
                    }else {
                        name = bar.name ?? ""
                        name = name + " > "
                        textColor = UIColor(hex:"#333333")
                    }
                    let firstSize = name.getSize(with: 15)
                    let oY = (self.headBarScrollView.bounds.height - firstSize.height) / 2
                    let firstLabel = UILabel(frame: CGRect(x: CGFloat(oX), y: oY, width: firstSize.width, height: firstSize.height))
                    firstLabel.textAlignment = .left
                    let textAttributes = [NSAttributedString.Key.foregroundColor: textColor,NSAttributedString.Key.font:UIFont(name:"PingFangSC-Regular",size:15)!]
                    firstLabel.attributedText = NSMutableAttributedString(string: name, attributes: textAttributes)
                    firstLabel.sizeToFit()
                    oX += firstSize.width
                    self.headBarScrollView.addSubview(firstLabel)
                    firstLabel.addTapGesture(action: { (rec) in
                        if bars.count != (index+1) {
                            self.delegate?.breadcrumbTap(name: bar.name ?? "", distinguished: bar.distinguishedName ?? "")
                        }
                    })
                    
                }
            }
        }else{
            nameLabel.text = cellViewModel?.name
            iconTagLabel.isHidden = true
            iconImageView.image = UIImage(named: title.icon!)
//            iconImageView.frame = CGRect(x: 8, y: 10, width: 30, height: 30)
            iconImageView.layer.masksToBounds = true
            iconImageView.layer.cornerRadius = 15.0
            nameLabel.font = UIFont(name: "PingFang SC", size: 16.0)
//            nameLabel.frame.x = 66
            
        }
    }
    
}

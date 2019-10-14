//
//  UnitPickerTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/13.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

protocol UnitPickerNextBtnDelegate {
    func next(unitName: String?, unitDistinguishedName: String?)
}

class UnitPickerTableViewCell: UITableViewCell {
    
    var delegate: UnitPickerNextBtnDelegate?

    lazy public var trueImage: UIImage = {
        return UIImage(named: "selected")!
    }()
    
    lazy public var falseImage: UIImage = {
        return UIImage(named: "unselected")!
    }()
    
    @IBAction func clickNextBtn(_ sender: UIButton) {
        if delegate != nil {
            delegate?.next(unitName: unitInfo?.name, unitDistinguishedName: unitInfo?.distinguishedName)
        }
    }
    @IBOutlet weak var nextLevelBtn: UIButton!
    @IBOutlet weak var unitIconLabel: UILabel!
    @IBOutlet weak var unitNameLabel: UILabel!
    @IBOutlet weak var checkImageView: UIImageView!
    @IBOutlet weak var unitIconBgImageView: UIImageView!
    
    
    private var unitInfo: OOUnitModel?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.checkImageView.image = falseImage
        self.unitIconBgImageView.image = O2ThemeManager.image(for: "Icon.pic_oval")
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func loadUnitInfo(info: OOUnitModel, checked: Bool) {
        self.unitInfo = info
        if let name = info.name {
            unitIconLabel.text = NSString(string: name).substring(to: 1)
        }
        self.unitIconLabel.isHidden = false
        self.checkImageView.isHidden = false
        self.nextLevelBtn.isHidden = false
        self.unitIconBgImageView.layer.masksToBounds = true
        self.unitIconBgImageView.layer.cornerRadius =  self.unitIconBgImageView.width / 2.0
        self.unitIconBgImageView.image = O2ThemeManager.image(for: "Icon.pic_oval")
        self.unitNameLabel.text = info.name
        if checked {
            self.checkImageView.image = trueImage
        }else {
            self.checkImageView.image = falseImage
        }
    }
    
    func loadUnitNotCheck(info: OOUnitModel) {
        self.unitInfo = info
        if let name = info.name {
            unitIconLabel.text = NSString(string: name).substring(to: 1)
        }
        self.unitIconBgImageView.layer.masksToBounds = true
        self.unitIconBgImageView.layer.cornerRadius =  self.unitIconBgImageView.width / 2.0
        self.unitIconBgImageView.image = O2ThemeManager.image(for: "Icon.pic_oval")
        self.unitNameLabel.text = info.name
        self.checkImageView.isHidden = true
        self.unitIconLabel.isHidden = false
        self.nextLevelBtn.isHidden = false
    }
    
    func loadIdentity(identity: OOIdentityModel, checked: Bool) {
        self.checkImageView.isHidden = false
        self.unitIconLabel.isHidden = true
        self.nextLevelBtn.isHidden = true
        self.unitNameLabel.text = identity.name
        self.unitIconBgImageView.layer.masksToBounds = true
        self.unitIconBgImageView.layer.cornerRadius =  self.unitIconBgImageView.width / 2.0
        let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":identity.person as AnyObject], generateTime: false)
        let url = URL(string: urlstr!)
        self.unitIconBgImageView.hnk_setImageFromURL(url!)
        if checked {
            self.checkImageView.image = trueImage
        }else {
            self.checkImageView.image = falseImage
        }
    }

}

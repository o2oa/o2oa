//
//  OOCLDCell.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/20.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import Hue
import SDWebImage

class OOCDLCell: UITableViewCell,Configurable {
    
    
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var subTitleLabel: UILabel!
    
    var viewModel:OOListUnitViewModel?
    
    private var iconURL:URL?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.iconImageView.image = #imageLiteral(resourceName: "icon_moren_72")
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius = 20
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

    func config(withItem item: Any?) {
        let model =  item as! NSObject
        if model.isKind(of: OOUnitModel.self) {
            let uModel = item as! OOUnitModel
            let first = uModel.name![0..<1]
            if let iconImage = firstWordImage(first) {
                self.iconImageView.image = iconImage
            }
            let number = uModel.subDirectUnitCount ?? 0
            if number > 0 {
                self.titleLabel.text = "\(uModel.name!)(\(number.description))"
            }else{
                self.titleLabel.text = uModel.name!
            }
            if let desc = uModel.desc {
                self.subTitleLabel.text = desc.count > 0 ? desc : "此组织没有描述信息"
            }else{
                self.subTitleLabel.text = "此组织没有描述信息"
            }
            
        }else if(model.isKind(of: OOGroupModel.self)){
            let gModel = item as! OOGroupModel
            let number = gModel.groupList?.count ?? 0
            //title
            if number > 0 {
                self.titleLabel.text = "\(gModel.name!)(\(number.description))"
            }else{
                self.titleLabel.text = gModel.name!
            }
            //subtitle
            if let desc = gModel.desc {
                self.subTitleLabel.text = desc.count > 0 ? desc : "此群组没有描述信息"
            }else{
                self.subTitleLabel.text = "此群组没有描述信息"
            }
        }else if(model.isKind(of: OOPersonModel.self)){
            let  pModel = model as! OOPersonModel
//            self.iconImageView.sd_setIndicatorStyle(.gray)
//            self.iconImageView.sd_setShowActivityIndicatorView(true)
            viewModel?.getIconOfPerson(pModel, compeletionBlock: { (iconImage, errMSg) in
                //self.iconImageView.sd_setShowActivityIndicatorView(false)
                self.iconImageView.image = iconImage
                
            })
            self.titleLabel.text = pModel.name
            self.subTitleLabel.text = pModel.mobile
        }
        
    }
    
    private func firstWordImage(_ word:String) -> UIImage? {
        let size = CGSize(width: 40, height: 40)
        let textColor = UIColor.white
        let backColor = UIColor(hex: "#FB4747")
        let font = UIFont(name: "PingFangSC-Regular", size: 18)
        return word.getTextImage(size, textColor: textColor, backColor: backColor, textFont: font!)
    }

}

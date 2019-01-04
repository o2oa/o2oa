//
//  OOLinkManCell.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/20.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOLinkManCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var subTitleLabel: UILabel!
    
    @IBOutlet weak var arrowTitleLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.iconImageView.image = #imageLiteral(resourceName: "icon_？")
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius = 20
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        let model = item as! NSObject
        if model.isKind(of: OOPersonModel.self){
            let  pModel = model as! OOPersonModel
            self.titleLabel.text = pModel.name
            self.subTitleLabel.text = pModel.mobile
        }
    }

}

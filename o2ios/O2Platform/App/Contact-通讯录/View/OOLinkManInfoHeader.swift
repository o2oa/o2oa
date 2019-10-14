//
//  OOLinkManInfoHeader.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/23.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOLinkManInfoHeader: UIView {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var manButton: UIButton!
    
    @IBOutlet weak var womenButton: UIButton!
    
    @IBOutlet weak var oftenLinkManButton: UIButton!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func awakeFromNib() {
        commonSetupNib()
    }
    
    private func commonSetupNib(){
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius = 37.5
        
        self.manButton.isUserInteractionEnabled = false
        self.womenButton.isUserInteractionEnabled = false
    }
    
    func configHeaderOfPerson(_ viewModel:OOLinkManViewModel,_ person:OOPersonModel){
        self.nameLabel.text = person.name
        let gender = person.genderType ?? "u"
        if gender == "m" {
            self.manButton.isSelected = true
            self.womenButton.isSelected = !self.manButton.isSelected
        }else if gender == "f" {
            self.womenButton.isSelected = true
            self.manButton.isSelected = !self.womenButton.isSelected
        }
        
        viewModel.getIconOfPerson(person) { (image, errMSg) in
            self.iconImageView.image = image
            
        }
        
    }
    
    
    
}

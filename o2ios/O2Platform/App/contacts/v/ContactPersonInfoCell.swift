//
//  ContactPersonInfoCell.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/11.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class ContactPersonInfoCell: UITableViewCell {

    @IBOutlet weak var nameLab: UILabel!
    @IBOutlet weak var valueLab: UILabel!
    @IBOutlet weak var eventBut: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.eventBut.theme_setImage(ThemeImagePicker(keyPath:"Icon.Shapes"), forState: .normal)
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

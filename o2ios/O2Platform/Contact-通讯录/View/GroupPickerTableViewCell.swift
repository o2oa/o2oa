//
//  GroupPickerTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/15.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class GroupPickerTableViewCell: UITableViewCell {
    lazy public var trueImage: UIImage = {
        return UIImage(named: "selected")!
    }()
    
    lazy public var falseImage: UIImage = {
        return UIImage(named: "unselected")!
    }()
    @IBOutlet weak var checkImageView: UIImageView!
    @IBOutlet weak var groupIconImageView: UIImageView!
    @IBOutlet weak var groupNameLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.checkImageView.image = falseImage
        self.groupIconImageView.image = O2ThemeManager.image(for: "Icon.icon_group")
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func loadGroupInfo(info: OOGroupModel, checked: Bool) {
        self.groupNameLabel.text = info.name
        if checked {
            self.checkImageView.image = trueImage
        }else {
            self.checkImageView.image = falseImage
        }
    }

    
    
}

//
//  PersonPickerTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/15.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class PersonPickerTableViewCell: UITableViewCell {
    lazy public var trueImage: UIImage = {
        return UIImage(named: "selected")!
    }()
    
    lazy public var falseImage: UIImage = {
        return UIImage(named: "unselected")!
    }()
    
    @IBOutlet weak var personIconImageView: UIImageView!
    @IBOutlet weak var checkImageView: UIImageView!
    @IBOutlet weak var personNameLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.checkImageView.image = falseImage
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func loadPersonInfo(info: OOPersonModel, checked: Bool) {
        self.personNameLabel.text = info.name
        if checked {
            self.checkImageView.image = trueImage
        }else {
            self.checkImageView.image = falseImage
        }
    }

}

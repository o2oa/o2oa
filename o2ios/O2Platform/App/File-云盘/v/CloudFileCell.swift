//
//  CloudFileCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class CloudFileCell: UITableViewCell {
    @IBOutlet weak var fileImage: UIImageView!
    @IBOutlet weak var fileNameLabel: UILabel!
    @IBOutlet weak var fileUpdateTimeLabel: UILabel!
    @IBOutlet weak var fileSizeLabel: UILabel!
    @IBOutlet weak var checkBoxImage: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

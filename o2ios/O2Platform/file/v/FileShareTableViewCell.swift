//
//  FileShareTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class FileShareTableViewCell: UITableViewCell {
    
    @IBOutlet weak var personIconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    var fileShare:FileShare? {
        didSet {
            self.personIconImageView.image = UIImage(named: "friends_icon")
            self.nameLabel.text = "\(fileShare!.name!)(\(fileShare!.count!))"
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

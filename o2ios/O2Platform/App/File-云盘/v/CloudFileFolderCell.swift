//
//  CloudFileFolderCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class CloudFileFolderCell: UITableViewCell {

    @IBOutlet weak var folderUpdateTimeLabel: UILabel!
    @IBOutlet weak var folderNameLabel: UILabel!
    @IBOutlet weak var checkBoxImage: UIImageView!
    
    
    var checkDelegate: CloudFileCheckDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func setData(folder: OOFolder) {
        self.folderNameLabel.text = folder.name ?? ""
        self.folderUpdateTimeLabel.text = folder.updateTime ?? ""
        if self.checkDelegate != nil {
            self.checkBoxImage.addTapGesture { (tap) in
                self.checkDelegate?.checkItem(folder)
            }
        }
    }

}

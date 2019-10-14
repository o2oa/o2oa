//
//  FileFolderItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/19.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

protocol FileFolderCellPassValueDelegate {
    func selectedCellPassValue(_ cell:FileFolderItemCell,f:OOFile)
}

class FileFolderItemCell: UITableViewCell {
    
    @IBOutlet weak var actionButton: UIButton!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    var file:OOFile? {
        didSet {
            self.nameLabel.text = file?.name!
        }
    }
    
    var delegate:FileFolderCellPassValueDelegate?

    override func awakeFromNib() {
        super.awakeFromNib()
        actionButton.setImage(UIImage(named: "unselected"), for: UIControl.State())
        actionButton.setImage(UIImage(named: "unselected"), for: .highlighted)
        actionButton.setImage(UIImage(named: "selected"), for: .selected)
//        actionButton.setImage(UIImage(named: "selected"), forState: .Selected | .Highlighted)
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func selectedFolderCell(_ sender: UIButton) {
        DDLogDebug("action Button Clicked")
        sender.isSelected = !sender.isSelected
        if sender.isSelected {
            //选中传值到controller
            delegate?.selectedCellPassValue(self, f: file!)
        }
    }
    

}

//
//  ZoneFileListCell.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/15.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

protocol ZoneFileListCellDelegate {
    func cellClick(_ model:ZonePickerModel)
}

class ZoneFileListCell: UITableViewCell {
    
    @IBOutlet weak var fileIconImageView: UIImageView!
    
    @IBOutlet weak var fileTitleLabel: UILabel!
    
    var pickerModel:ZonePickerModel! {
        didSet {
            self.fileTitleLabel.text = pickerModel.name
            let name = pickerModel.name
            let nameArray = name?.split(".")
            if nameArray != nil, let ext = nameArray?.last {
                if let img = UIImage(named: "file_\(ext)_icon") {
                    self.fileIconImageView.image = img
                }else{
                    self.fileIconImageView.image = #imageLiteral(resourceName: "file_unknown_icon")
                }
            }else{
                self.fileIconImageView.image = #imageLiteral(resourceName: "file_unknown_icon")
            }
        }
    }
    
    var delegate:ZoneFileListCellDelegate!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func fileCellClick(_ sender: UIButton) {
        print("fileCellClick")
        if let d = delegate {
            d.cellClick(self.pickerModel)
        }
    }
    
    
}

//
//  CFFolderTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/17.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class CFFolderTableViewCell: UITableViewCell {
    
    
    @IBOutlet weak var checkBoxButton: UIButton!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var folderNameLabel: UILabel!
    //点击checkbox
    @IBAction func clickCheckAction(_ sender: UIButton) {
        DDLogDebug("点击。。。。。。。。CFFolderTableViewCell")
        if self.showCheckBox {
            if self.folder != nil {
                DDLogDebug("enter click folder")
                self.clickdelegate?.clickFolder(self.folder!)
            }
        }
    }
    var folder: OOFolder?
    var clickdelegate: CloudFileCheckClickDelegate?
    var showCheckBox = true
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
   
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    //添加数据
    func setData(folder: OOFolder, checked: Bool, isShowCheck: Bool = true) {
        self.folder = folder
        self.folderNameLabel.text = folder.name ?? ""
        self.timeLabel.text = folder.updateTime ?? ""
        self.showCheckBox = isShowCheck
        if self.showCheckBox {
            self.checkBoxButton.isHidden = false
            if checked {
                self.checkBoxButton.setImage(UIImage(named: "icon__ok2_click"), for: .normal)
            }else {
                self.checkBoxButton.setImage(UIImage(named: "icon_circle"), for: .normal)
            }
        }else {
            self.checkBoxButton.isHidden = true
        }
    }
}

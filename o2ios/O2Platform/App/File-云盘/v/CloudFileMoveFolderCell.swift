//
//  CloudFileMoveFolderCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/25.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

protocol CloudFileMoveChooseDelegate {
    func choose(folder: OOFolder)
}

class CloudFileMoveFolderCell: UITableViewCell {
    
    @IBOutlet weak var chooseBtn: UIButton!
    
    @IBOutlet weak var folderNameLabel: UILabel!
    @IBOutlet weak var folderTimeLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        self.chooseBtn.setTitleColor(base_color, for: .normal)
    }
    @IBAction func chooseAction(_ sender: UIButton) {
        if self.folder != nil {
            self.delegate?.choose(folder: self.folder!)
        }
    }
    
    
    var folder: OOFolder?
    var delegate: CloudFileMoveChooseDelegate?
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    //添加数据
    func setData(folder: OOFolder) {
        self.folder = folder
        self.folderNameLabel.text = folder.name ?? ""
        self.folderTimeLabel.text = folder.updateTime ?? ""
    }
    
}

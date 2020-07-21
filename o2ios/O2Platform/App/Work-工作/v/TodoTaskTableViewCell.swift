//
//  TodoTaskTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/3.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
class TodoTaskTableViewCell: UITableViewCell {
    
    
    
    @IBOutlet weak var todoIconImageView: UIImageView!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var statusLabel: UILabel!
    
    @IBOutlet weak var timeLabel: UILabel!
    

    

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setData(cellModel:TodoCellModel<TodoTask>) {
        if let applicationId = cellModel.sourceObj?.application {
            ImageUtil.shared.getProcessApplicationIcon(id: applicationId)
                .then { (image)  in
                    self.todoIconImageView?.image = image
            }.catch { (err) in
                self.todoIconImageView?.image = UIImage(named: "todo_8")
            }
        }
        self.titleLabel.text = "[\((cellModel.applicationName)!)] \((cellModel.title)!)"
        self.statusLabel.text = cellModel.status
        self.timeLabel.text = cellModel.time
    }

}

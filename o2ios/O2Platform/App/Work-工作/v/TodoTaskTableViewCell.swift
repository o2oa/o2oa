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
    
    var cellModel:TodoCellModel<TodoTask>?{
        didSet {
            let i = 1 + arc4random() % 10
            self.imageView?.image = UIImage.scaleTo(image: UIImage(named: "todo_\(i)")!, w: 20.0, h: 20.0)
            self.titleLabel.text = "[\((cellModel!.applicationName)!)] \((cellModel!.title)!)"
            self.statusLabel.text = cellModel?.status
            self.timeLabel.text = cellModel?.time
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

//
//  MainTodoTaskCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/27.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class MainTodoTaskCell: UITableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!
    
    
    var todoTask:TodoTask?{
        didSet {
            
            titleLabel.text = "[\((todoTask!.applicationName)!)] \((todoTask!.title)!)"
            
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

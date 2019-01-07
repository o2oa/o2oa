//
//  TodoedActionCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/15.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

protocol TodoedActionCellDelegate {
    func open(_ actionModel:TodoedActionModel)
}

class TodoedActionCell: UITableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var actionButton: UIButton!
    
    var delegate:TodoedActionCellDelegate?
    
    var actionModel:TodoedActionModel?{
        didSet {
            self.titleLabel.text = actionModel?.destText
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        self.actionButton.layer.masksToBounds = true
        self.actionButton.layer.cornerRadius = 5
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func openTodoedDoc(_ sender: UIButton) {
        self.delegate?.open(self.actionModel!)
    }
    

}

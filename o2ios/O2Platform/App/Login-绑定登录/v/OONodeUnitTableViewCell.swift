//
//  OONodeUnitTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/8.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import O2OA_Auth_SDK

class OONodeUnitTableViewCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nodeNameLabel: UILabel!
    
    @IBOutlet weak var selectImageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        //fatalError("init(coder:) has not been implemented")
    }
    
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let node = item as? O2BindUnitModel else {
            return
        }
        self.nodeNameLabel.text = node.name
        
    }
}

//
//  NewMainAppCollectionViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/12.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class NewMainAppCollectionViewCell: UICollectionViewCell {
    
    @IBOutlet weak var appIconImageView: UIImageView!
    
    @IBOutlet weak var appNameLabel: UILabel!
    
    var app:O2App! {
        didSet{
            self.appIconImageView.image = UIImage(named: app.normalIcon!)
            self.appNameLabel.text = app.title
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
}

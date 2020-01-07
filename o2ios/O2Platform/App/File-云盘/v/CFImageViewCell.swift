//
//  CFImageViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/28.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class CFImageViewCell: UICollectionViewCell {
 
    
    @IBOutlet weak var cloudImageView: UIImageView!
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    func setData(urlString: String) {
        let url = URL(string: urlString)!
        self.cloudImageView.hnk_setImageFromURL(url)
    }
}

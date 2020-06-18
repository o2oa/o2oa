//
//  IMLocationView.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/18.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class IMLocationView: UIView {
    
    static let IMLocationViewWidth: CGFloat = 175
    static let IMLocationViewHeight: CGFloat = 100
    
    @IBOutlet weak var locationLabel: UILabel!
    
    
    override func awakeFromNib() {
    }
    
    func setLocationAddress(address: String) {
        self.locationLabel.text = address
    }
    
}

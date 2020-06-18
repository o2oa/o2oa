//
//  IMAudioView.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/17.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class IMAudioView: UIView {
    
    static let IMAudioView_width: CGFloat = 92
    static let IMAudioView_height: CGFloat = 36
    
    @IBOutlet weak var playImageView: UIImageView!
    @IBOutlet weak var durationLabel: UILabel!
    
    
    override func awakeFromNib() { }
    
    
    func setDuration(duration: String) {
        self.durationLabel.text = "\(duration)\""
    }
}

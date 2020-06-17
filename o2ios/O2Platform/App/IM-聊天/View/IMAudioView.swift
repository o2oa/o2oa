//
//  IMAudioView.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/17.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class IMAudioView: UIView {
    
    static let IMAudioView_width: CGFloat = 76
    static let IMAudioView_height: CGFloat = 36
    
    @IBOutlet weak var playImageView: UIImageView!
    @IBOutlet weak var durationLabel: UILabel!
    
    private var playUrl: String? = nil
    
    override func awakeFromNib() {
        
    }
    
    func setPlayUrl(url: String?) {
        self.playUrl = url
    }
    func setDuration(duration: String) {
        self.durationLabel.text = "\(duration)\""
    }
}

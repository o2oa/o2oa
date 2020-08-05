//
//  IdentitySelectView.swift
//  O2Platform
//
//  Created by FancyLou on 2020/8/5.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class IdentitySelectView: UIView {

    @IBOutlet weak var identityLabel: UILabel!
    
    @IBOutlet weak var identityUnitLabel: UILabel!
    
    @IBOutlet weak var selectedImageView: UIImageView!
    
    var selected = false
    var id : IdentityV2? = nil
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    
    func setUp(identity: IdentityV2) {
        self.id = identity
        self.identityLabel.text = "\(identity.name ?? "")(\(identity.unitName ?? ""))"
        self.identityLabel.textColor = text_primary_color
        self.identityUnitLabel.text = "\(identity.unitLevelName ?? "")"
        self.identityUnitLabel.textColor = text_normal_color
        self.backgroundColor = .white
    }
    
    func selectedIdentity() {
        self.selected = true
        self.identityLabel.textColor = base_color
        self.selectedImageView.isHidden = false
    }
    
    func deSelectedIdentity() {
        self.selected = false
        self.identityLabel.textColor = text_primary_color
        self.selectedImageView.isHidden = true
    }
}

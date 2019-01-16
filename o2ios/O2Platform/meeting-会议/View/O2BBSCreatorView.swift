//
//  O2BBSCreatorView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/3.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

protocol O2BBSCreatorViewDelegate {
    func creatorViewClicked(_ view:O2BBSCreatorView)
}

class O2BBSCreatorView: UIView {
    
    private var _iconImage:UIImage?
    
    var iconImage:UIImage? {
        get {
            return _iconImage
        }
        set(newImage){
            _iconImage = newImage
            if _iconImage != nil {
                self.actionButton.setImage(_iconImage, for: .normal)
            }
        }
    }
    @IBOutlet weak var actionButton: UIButton!
    
    @IBOutlet weak var creatorView: UIView!
    
    
    var delegate:O2BBSCreatorViewDelegate?
    
    override func awakeFromNib() {
        actionButton.layer.cornerRadius = 25
        actionButton.layer.masksToBounds = true
        creatorView.layer.cornerRadius = 25
        creatorView.layer.masksToBounds = true
        self.layer.cornerRadius = 25
        self.layer.masksToBounds = true
    }
    
    
    @IBAction func creatorViewClicked(_ sender: UIButton) {
        guard let _ = delegate else {
            return
        }
        self.delegate?.creatorViewClicked(self)
    }
    
}

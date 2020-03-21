//
//  JCUpdateMemberCell.swift
//  JChat
//
//  Created by deng on 2017/5/11.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCUpdateMemberCell: UICollectionViewCell {
    var avator: UIImage? {
        get {
            return avatorView.image
        }
        set {
            avatorView.image = newValue
        }
    }
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    private var avatorView: UIImageView = UIImageView()
    private lazy var defaultUserIcon = UIImage.loadImage("com_icon_user_36")
    
    private func _init() {
        
        avatorView.image = defaultUserIcon

        addSubview(avatorView)
        
        addConstraint(_JCLayoutConstraintMake(avatorView, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(avatorView, .width, .equal, nil, .notAnAttribute, 36))
        addConstraint(_JCLayoutConstraintMake(avatorView, .height, .equal, nil, .notAnAttribute, 36))
        addConstraint(_JCLayoutConstraintMake(avatorView, .centerX, .equal, contentView, .centerX))
        
    }
    
    func bindDate(user: JMSGUser) {
        user.thumbAvatarData { (data, id, error) in
            if let data = data {
                let image = UIImage(data: data)
                self.avatorView.image = image
            } else {
                self.avatorView.image = self.defaultUserIcon
            }
        }
    }
}

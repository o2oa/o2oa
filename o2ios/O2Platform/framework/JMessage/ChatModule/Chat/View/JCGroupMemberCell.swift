//
//  JCGroupMemberCell.swift
//  JChat
//
//  Created by deng on 2017/5/10.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCGroupMemberCell: UICollectionViewCell {
    
    var avator: UIImage? {
        get {
            return avatorView.image
        }
        set {
            nickname.text = ""
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
    private var nickname: UILabel = UILabel()
    private lazy var userDefaultIcon = UIImage.loadImage("com_icon_user_50")
    
    private func _init() {
        
        nickname.font = UIFont.systemFont(ofSize: 12)
        nickname.textAlignment = .center
        
        addSubview(avatorView)
        addSubview(nickname)
        
        addConstraint(_JCLayoutConstraintMake(avatorView, .centerY, .equal, contentView, .centerY, -10))
        addConstraint(_JCLayoutConstraintMake(avatorView, .width, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(avatorView, .height, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(avatorView, .centerX, .equal, contentView, .centerX))
        
        addConstraint(_JCLayoutConstraintMake(nickname, .centerX, .equal, contentView, .centerX))
        addConstraint(_JCLayoutConstraintMake(nickname, .width, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(nickname, .height, .equal, nil, .notAnAttribute, 15))
        addConstraint(_JCLayoutConstraintMake(nickname, .top, .equal, avatorView, .bottom, 5))
        
    }
    
    func bindDate(user: JMSGUser) {
        nickname.text = user.displayName()
        let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":user.username as AnyObject])
        let url = URL(string: urlstr!)
        let bound = self.avatorView.bounds
        if bound.width <= 0 || bound.height <= 0 {
            self.avatorView.bounds = CGRect(x: 0, y: 0, width: 50, height: 50)
        }
        self.avatorView.hnk_setImageFromURL(url!)
        
//        user.thumbAvatarData { (data, id, error) in
//            if let data = data {
//                let image = UIImage(data: data)
//                self.avatorView.image = image
//            } else {
//                self.avatorView.image = self.userDefaultIcon
//            }
//        }
    }
    
}

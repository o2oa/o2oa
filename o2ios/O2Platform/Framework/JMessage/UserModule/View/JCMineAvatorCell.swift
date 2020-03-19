//
//  JCMineAvatorCell.swift
//  JChat
//
//  Created by deng on 2017/3/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCMineAvatorCell: UITableViewCell {

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        _init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        _init()
    }
    
    func baindDate(user: JMSGUser) {
        nickname.text = user.displayName()
        signature.text = user.signature
        user.thumbAvatarData { (data, username, error) in
            if let imageData = data {
                let image = UIImage(data: imageData)
                self.iconView.image = image
            } else {
                self.iconView.image = UIImage.loadImage("com_icon_user_65")
            }
        }
    }
    
    private lazy var iconView: UIImageView = {
        let iconView = UIImageView()
        iconView.contentMode = .scaleAspectFill
        iconView.clipsToBounds = true
        return iconView
    }()
    private lazy var signature: UILabel = {
        let signature = UILabel()
        signature.font = UIFont.systemFont(ofSize: 14)
        signature.textColor = UIColor(netHex: 0x999999)
        signature.backgroundColor = .white
        signature.layer.masksToBounds = true
        return signature
    }()
    private lazy var nickname: UILabel = {
        let nickname = UILabel()
        nickname.textColor = UIColor(netHex: 0x2c2c2c)
        nickname.font = UIFont.systemFont(ofSize: 16)
        nickname.backgroundColor = .white
        nickname.layer.masksToBounds = true
        return nickname
    }()
    
    //MARK: - private func
    private func _init() {
        contentView.addSubview(iconView)
        contentView.addSubview(signature)
        contentView.addSubview(nickname)
        
        addConstraint(_JCLayoutConstraintMake(iconView, .top, .equal, contentView, .top, 10))
        addConstraint(_JCLayoutConstraintMake(iconView, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(iconView, .width, .equal, nil, .notAnAttribute, 65))
        addConstraint(_JCLayoutConstraintMake(iconView, .height, .equal, nil, .notAnAttribute, 65))
        
        addConstraint(_JCLayoutConstraintMake(nickname, .top, .equal, contentView, .top, 21.5))
        addConstraint(_JCLayoutConstraintMake(nickname, .left, .equal, iconView, .right, 11))
        addConstraint(_JCLayoutConstraintMake(nickname, .right, .equal, contentView, .right))
        addConstraint(_JCLayoutConstraintMake(nickname, .height, .equal, nil, .notAnAttribute, 22.5))
        
        addConstraint(_JCLayoutConstraintMake(signature, .top, .equal, nickname, .bottom, 2.5))
        addConstraint(_JCLayoutConstraintMake(signature, .left, .equal, nickname, .left))
        addConstraint(_JCLayoutConstraintMake(signature, .right, .equal, nickname, .right))
        addConstraint(_JCLayoutConstraintMake(signature, .height, .equal, nil, .notAnAttribute, 20))
    }

}

//
//  JCMyAvatorCell.swift
//  JChat
//
//  Created by deng on 2017/3/30.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCMyAvatorCell: UITableViewCell {
    
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

    private lazy var avatorView: UIImageView = {
        let avatorView = UIImageView()
        avatorView.contentMode = .scaleAspectFill
        avatorView.clipsToBounds = true
        return avatorView
    }()
    private lazy var nameLabel: UILabel = {
        let nameLabel = UILabel()
        nameLabel.textAlignment = .center
        nameLabel.font = UIFont.systemFont(ofSize: 14)
        nameLabel.textColor = UIColor(netHex: 0x999999)
        nameLabel.backgroundColor = .white
        nameLabel.layer.masksToBounds = true
        return nameLabel
    }()
    
    private lazy var defaultAvator = UIImage.loadImage("com_icon_user_80")
    
    //MARK: - private func 
    private func _init() {
        contentView.addSubview(avatorView)
        contentView.addSubview(nameLabel)
        
        addConstraint(_JCLayoutConstraintMake(avatorView, .top, .equal, contentView, .top, 25))
        addConstraint(_JCLayoutConstraintMake(avatorView, .centerX, .equal, contentView, .centerX))
        addConstraint(_JCLayoutConstraintMake(avatorView, .width, .equal, nil, .notAnAttribute, 80))
        addConstraint(_JCLayoutConstraintMake(avatorView, .height, .equal, nil, .notAnAttribute, 80))
        
        addConstraint(_JCLayoutConstraintMake(nameLabel, .top, .equal, avatorView, .bottom, 9))
        addConstraint(_JCLayoutConstraintMake(nameLabel, .right, .equal, contentView, .right))
        addConstraint(_JCLayoutConstraintMake(nameLabel, .left, .equal, contentView, .left))
        addConstraint(_JCLayoutConstraintMake(nameLabel, .height, .equal, nil, .notAnAttribute, 14))
    }
    
    func bindData(user: JMSGUser) {
        nameLabel.text =  "用户名：" + user.username
        user.thumbAvatarData { (data, username, error) in
            if let imageData = data {
                let image = UIImage(data: imageData)
                self.avatorView.image = image
            } else {
                self.avatorView.image = self.defaultAvator
            }
        }
    }

}

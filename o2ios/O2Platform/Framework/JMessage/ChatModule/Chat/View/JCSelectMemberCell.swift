//
//  JCSelectMemberCell.swift
//  JChat
//
//  Created by deng on 2017/5/11.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCSelectMemberCell: UITableViewCell {

    var icon: UIImage? {
        get {
            return avatorView.image
        }
        set {
            avatorView.image = newValue
        }
    }
    
    var selectIcon: UIImage? {
        get {
            return selectIconView.image
        }
        set {
            selectIconView.image = newValue
        }
    }
    
    var title: String? {
        get {
            return usernameLabel.text
        }
        set {
            usernameLabel.text = newValue
        }
    }
    
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
    
    private lazy var avatorView: UIImageView = UIImageView()
    private lazy var usernameLabel: UILabel = UILabel()
    private lazy var selectIconView: UIImageView = UIImageView()
    
    public func bindDate(_ user : JMSGUser) {
        title = user.displayName()
        icon = UIImage.loadImage("com_icon_user_36")
        user.thumbAvatarData({ (data, name, error) in
            if data != nil {
                let image = UIImage(data: data!)
                self.icon = image
            }
        })
    }
    
    //MARK: - private func
    private func _init() {
        
        let image = UIImage.loadImage("com_icon_unselect")
        
        selectIconView.image = image
        
        usernameLabel.textColor = UIColor(netHex: 0x2c2c2c)
        usernameLabel.font = UIFont.systemFont(ofSize: 14)
        
        contentView.addSubview(selectIconView)
        contentView.addSubview(avatorView)
        contentView.addSubview(usernameLabel)
        
        addConstraint(_JCLayoutConstraintMake(selectIconView, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(selectIconView, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(selectIconView, .width, .equal, nil, .notAnAttribute, 20))
        addConstraint(_JCLayoutConstraintMake(selectIconView, .height, .equal, nil, .notAnAttribute, 20))
        
        addConstraint(_JCLayoutConstraintMake(avatorView, .left, .equal, selectIconView, .right, 15))
        addConstraint(_JCLayoutConstraintMake(avatorView, .top, .equal, contentView, .top, 9.5))
        addConstraint(_JCLayoutConstraintMake(avatorView, .width, .equal, nil, .notAnAttribute, 36))
        addConstraint(_JCLayoutConstraintMake(avatorView, .height, .equal, nil, .notAnAttribute, 36))
        
        addConstraint(_JCLayoutConstraintMake(usernameLabel, .left, .equal, avatorView, .right, 11))
        addConstraint(_JCLayoutConstraintMake(usernameLabel, .top, .equal, contentView, .top, 19.5))
        addConstraint(_JCLayoutConstraintMake(usernameLabel, .right, .equal, contentView, .right, -15))
        addConstraint(_JCLayoutConstraintMake(usernameLabel, .height, .equal, nil, .notAnAttribute, 16))
        
    }

}

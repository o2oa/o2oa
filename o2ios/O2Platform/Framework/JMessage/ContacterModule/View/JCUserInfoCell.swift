//
//  JCMyInfoCell.swift
//  JChat
//
//  Created by deng on 2017/3/30.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCUserInfoCell: JCTableViewCell {
    
    var icon: UIImage? {
        get {
            return iconView.image
        }
        set {
            iconView.image = newValue
        }
    }
    
    var title: String? {
        get {
            return titleLabel.text
        }
        set {
            titleLabel.text = newValue
        }
    }
    
    var detail: String? {
        get {
            return detailLabel.text
        }
        set {
            detailLabel.text = newValue
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
    
    private lazy var iconView: UIImageView = UIImageView()
    private lazy var titleLabel: UILabel = UILabel()
    private lazy var detailLabel: UILabel = UILabel()
    
    private func _init() {
        titleLabel.font = UIFont.systemFont(ofSize: 16)
        detailLabel.textAlignment = .right
        detailLabel.textColor = UIColor(netHex: 0x999999)
        detailLabel.font = UIFont.systemFont(ofSize: 14)
     
        addSubview(iconView)
        addSubview(titleLabel)
        addSubview(detailLabel)
        
        addConstraint(_JCLayoutConstraintMake(iconView, .top, .equal, contentView, .top, 13.5))
        addConstraint(_JCLayoutConstraintMake(iconView, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(iconView, .width, .equal, nil, .notAnAttribute, 18))
        addConstraint(_JCLayoutConstraintMake(iconView, .height, .equal, nil, .notAnAttribute, 18))
        
        addConstraint(_JCLayoutConstraintMake(titleLabel, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .left, .equal, iconView, .right, 10))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .width, .equal, nil, .notAnAttribute, 100))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .height, .equal, nil, .notAnAttribute, 22.5))
        
        addConstraint(_JCLayoutConstraintMake(detailLabel, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(detailLabel, .left, .equal, titleLabel, .right))
        addConstraint(_JCLayoutConstraintMake(detailLabel, .right, .equal, contentView, .right, -16))
        addConstraint(_JCLayoutConstraintMake(detailLabel, .height, .equal, contentView, .height))
    }

}

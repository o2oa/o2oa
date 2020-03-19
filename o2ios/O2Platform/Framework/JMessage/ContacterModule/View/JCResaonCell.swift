//
//  JCResaonCell.swift
//  JChat
//
//  Created by deng on 2017/5/25.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCResaonCell: JCTableViewCell {
    
    var resaon: String? {
        get {
            return titleLabel.text
        }
        set {
            titleLabel.text = newValue
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

    private lazy var titleLabel: UILabel = UILabel()
    
    private func _init() {
        titleLabel.font = UIFont.systemFont(ofSize: 14)
        titleLabel.textColor = UIColor(netHex: 0x999999)
    
        addSubview(titleLabel)

        addConstraint(_JCLayoutConstraintMake(titleLabel, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .right, .equal, contentView, .right, -15))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .height, .equal, nil, .notAnAttribute, 20))
    }
    
}

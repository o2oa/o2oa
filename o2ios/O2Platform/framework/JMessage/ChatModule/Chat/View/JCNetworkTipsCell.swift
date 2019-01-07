//
//  JCNetworkTipsCell.swift
//  JChat
//
//  Created by deng on 2017/6/12.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCNetworkTipsCell: UITableViewCell {

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
    
    private lazy var statueView: UIImageView = {
        let statueView = UIImageView()
        statueView.image = UIImage.loadImage("com_icon_send_error")
        return statueView
    }()
    private lazy var tipsLabel: UILabel = {
        let tipsLabel = UILabel()
        tipsLabel.text = "当前网络不可用，请检查您的网络设置"
        tipsLabel.font = UIFont.systemFont(ofSize: 14)
        return tipsLabel
    }()
    
    //MARK: - private func
    private func _init() {
        backgroundColor = UIColor(netHex: 0xFFDFE0)

        contentView.addSubview(statueView)
        contentView.addSubview(tipsLabel)
        
        addConstraint(_JCLayoutConstraintMake(tipsLabel, .left, .equal, statueView, .right, 11.5))
        addConstraint(_JCLayoutConstraintMake(tipsLabel, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(tipsLabel, .right, .equal, contentView, .right))
        addConstraint(_JCLayoutConstraintMake(tipsLabel, .height, .equal, contentView, .height))

        addConstraint(_JCLayoutConstraintMake(statueView, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(statueView, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(statueView, .height, .equal, nil, .notAnAttribute, 21))
        addConstraint(_JCLayoutConstraintMake(statueView, .width, .equal, nil, .notAnAttribute, 21))
    }
}

//
//  JCSingleSettingCell.swift
//  JChat
//
//  Created by deng on 2017/4/27.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@objc public protocol JCSingleSettingCellDelegate: NSObjectProtocol {
    @objc optional func singleSettingCell(clickAddButton button: UIButton)
    @objc optional func singleSettingCell(clickAvatorButton button: UIButton)
}

class JCSingleSettingCell: UITableViewCell {

    weak var delegate: JCSingleSettingCellDelegate?

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        _init()
    }
    
    private lazy var avatorButton: UIButton = {
        let avatorButton = UIButton()
        avatorButton.setBackgroundImage(UIImage.loadImage("com_icon_user_50"), for: .normal)
        avatorButton.addTarget(self, action: #selector(_clickAvator), for: .touchUpInside)
        return avatorButton
    }()
    private lazy var addButton: UIButton = {
        let addButton = UIButton()
        addButton.setBackgroundImage(UIImage.loadImage("com_icon_single_add"), for: .normal)
        addButton.setBackgroundImage(UIImage.loadImage("com_icon_single_add_per"), for: .highlighted)
        addButton.addTarget(self, action: #selector(_clickAdd), for: .touchUpInside)
        return addButton
    }()
    private lazy var nickname: UILabel = {
        let nickname = UILabel()
        nickname.font = UIFont.systemFont(ofSize: 12)
        nickname.textAlignment = .center
        nickname.textColor = UIColor(netHex: 0x2C2C2C)
        return nickname
    }()
    
    func bindData(_ user: JMSGUser) {
        nickname.text = user.displayName()
        user.thumbAvatarData { (data, id, error) in
            if data != nil {
                let image = UIImage(data: data!)
                self.avatorButton.setBackgroundImage(image, for: .normal)
            }
        }
    }
    
    private func _init() {
        contentView.addSubview(avatorButton)
        //contentView.addSubview(addButton)
        contentView.addSubview(nickname)
        
        addConstraint(_JCLayoutConstraintMake(avatorButton, .left, .equal, contentView, .left, 20))
        addConstraint(_JCLayoutConstraintMake(avatorButton, .width, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(avatorButton, .height, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(avatorButton, .top, .equal, contentView, .top, 16.5))
        
        addConstraint(_JCLayoutConstraintMake(nickname, .left, .equal, avatorButton, .left))
        addConstraint(_JCLayoutConstraintMake(nickname, .width, .equal, avatorButton, .width))
        addConstraint(_JCLayoutConstraintMake(nickname, .height, .equal, nil, .notAnAttribute, 16.5))
        addConstraint(_JCLayoutConstraintMake(nickname, .top, .equal, avatorButton, .bottom, 3))
        
//        addConstraint(_JCLayoutConstraintMake(addButton, .left, .equal, avatorButton, .right, 20))
//        addConstraint(_JCLayoutConstraintMake(addButton, .width, .equal, nil, .notAnAttribute, 50))
//        addConstraint(_JCLayoutConstraintMake(addButton, .height, .equal, nil, .notAnAttribute, 50))
//        addConstraint(_JCLayoutConstraintMake(addButton, .top, .equal, contentView, .top, 16.5))
    }
    
    @objc func _clickAvator() {
        delegate?.singleSettingCell?(clickAvatorButton: avatorButton)
    }
    
    @objc func _clickAdd() {
        delegate?.singleSettingCell?(clickAddButton: addButton)
    }

}

//
//  JCUserAvatorCell.swift
//  JChat
//
//  Created by deng on 2017/5/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

@objc public protocol JCUserAvatorCellDelegate: NSObjectProtocol {
    @objc optional func tapAvator(_ image: UIImage?)
}

class JCUserAvatorCell: JCTableViewCell {
    
    weak var delegate: JCUserAvatorCellDelegate?

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
    private lazy var nameLabel: UILabel = UILabel()
    private lazy var signatureLabel: UILabel = UILabel()

    private lazy var defaultAvator = UIImage.loadImage("com_icon_user_80")
    
    //MARK: - private func
    private func _init() {
        
        let tapGR = UITapGestureRecognizer(target: self, action: #selector(_tapHandler))
        avatorView.addGestureRecognizer(tapGR)
        avatorView.isUserInteractionEnabled = true
        avatorView.image = defaultAvator
        avatorView.contentMode = .scaleAspectFill
        avatorView.clipsToBounds = true
        
        nameLabel.textAlignment = .center
        nameLabel.font = UIFont.systemFont(ofSize: 16)
        nameLabel.textColor = UIColor(netHex: 0x2C2C2C)
        
        signatureLabel.textAlignment = .center
        signatureLabel.font = UIFont.systemFont(ofSize: 13)
        signatureLabel.textColor = UIColor(netHex: 0x999999)
        signatureLabel.numberOfLines = 0

        contentView.addSubview(avatorView)
        contentView.addSubview(nameLabel)
        contentView.addSubview(signatureLabel)
        
        addConstraint(_JCLayoutConstraintMake(avatorView, .top, .equal, contentView, .top, 25))
        addConstraint(_JCLayoutConstraintMake(avatorView, .centerX, .equal, contentView, .centerX))
        addConstraint(_JCLayoutConstraintMake(avatorView, .width, .equal, nil, .notAnAttribute, 80))
        addConstraint(_JCLayoutConstraintMake(avatorView, .height, .equal, nil, .notAnAttribute, 80))
        
        addConstraint(_JCLayoutConstraintMake(nameLabel, .top, .equal, avatorView, .bottom, 6.5))
        addConstraint(_JCLayoutConstraintMake(nameLabel, .right, .equal, contentView, .right, -15))
        addConstraint(_JCLayoutConstraintMake(nameLabel, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(nameLabel, .height, .equal, nil, .notAnAttribute, 22.5))
        
        addConstraint(_JCLayoutConstraintMake(signatureLabel, .top, .equal, nameLabel, .bottom, 3))
        addConstraint(_JCLayoutConstraintMake(signatureLabel, .centerX, .equal, contentView, .centerX))
        addConstraint(_JCLayoutConstraintMake(signatureLabel, .height, .equal, nil, .notAnAttribute, 37))
        addConstraint(_JCLayoutConstraintMake(signatureLabel, .width, .equal, nil, .notAnAttribute, 210))
    }
    
    func bindData(user: JMSGUser) {
        nameLabel.text =  user.displayName()
        signatureLabel.text = user.signature
        avatorView.image = defaultAvator
        user.largeAvatarData { (data, username, error) in
            guard let imageData = data else {
                return
            }
            let image = UIImage(data: imageData)
            self.avatorView.image = image
        }
    }
    
    func _tapHandler(sender:UITapGestureRecognizer) {
        delegate?.tapAvator?(self.avatorView.image)
    }

}

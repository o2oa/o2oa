//
//  GroupAvatorCell.swift
//  JChat
//
//  Created by 邓永豪 on 2017/9/19.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class GroupAvatorCell: JCTableViewCell {

    var title: String {
        get {
            return self.titleLabel.text!
        }
        set {
            return self.titleLabel.text  = newValue
        }
    }

    var avator: UIImage? {
        get {
            return avatorView.image
        }
        set {
            avatorView.image = newValue
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
    private lazy var avatorView: UIImageView =  UIImageView()

    func bindData(_ group: JMSGGroup) {
        group.thumbAvatarData { (data, id , error) in
            if let data = data {
                let image = UIImage(data: data)
                self.avatorView.image = image
            }
        }
    }

    //MARK: - private func
    private func _init() {
        titleLabel.textAlignment = .left
        titleLabel.font = UIFont.systemFont(ofSize: 16)

        avatorView.contentMode = .scaleAspectFill
        avatorView.image = UIImage.loadImage("com_icon_group_36")
        avatorView.clipsToBounds = true

        contentView.addSubview(avatorView)
        contentView.addSubview(titleLabel)

        addConstraint(_JCLayoutConstraintMake(titleLabel, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .right, .equal, contentView, .centerX))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .height, .equal, nil, .notAnAttribute, 22.5))

        addConstraint(_JCLayoutConstraintMake(avatorView, .right, .equal, contentView, .right))
        addConstraint(_JCLayoutConstraintMake(avatorView, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(avatorView, .height, .equal, nil, .notAnAttribute, 36))
        addConstraint(_JCLayoutConstraintMake(avatorView, .width, .equal, nil, .notAnAttribute, 36))
    }
}

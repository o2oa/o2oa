//
//  JCMineInfoCell.swift
//  JChat
//
//  Created by deng on 2017/3/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@objc public protocol JCMineInfoCellDelegate: NSObjectProtocol {
    @objc optional func mineInfoCell(clickSwitchButton button: UISwitch, indexPath: IndexPath?)
}

class JCMineInfoCell: JCTableViewCell {
    
    open weak var delegate: JCMineInfoCellDelegate?
    var indexPate: IndexPath?
    
    var title: String {
        get {
            return titleLabel.text ?? ""
        }
        set {
            return titleLabel.text  = newValue
        }
    }
    
    var detail: String? {
        get {
            return detailLabel.text
        }
        set {
            detailLabel.isHidden = false
            detailLabel.text = newValue
        }
    }
    
    var isShowSwitch: Bool {
        get {
            return !switchButton.isHidden
        }
        set {
            switchButton.isHidden = !newValue
        }
    }
    
    var isSwitchOn: Bool {
        get {
            return switchButton.isOn
        }
        set {
            switchButton.isOn = newValue
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
    
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.textAlignment = .left
        titleLabel.font = UIFont.systemFont(ofSize: 16)
        titleLabel.backgroundColor = .white
        titleLabel.layer.masksToBounds = true
        return titleLabel
    }()
    private lazy var switchButton: UISwitch =  {
        let switchButton = UISwitch()
        switchButton.isHidden = true
        switchButton.addTarget(self, action: #selector(clickSwitch(_:)), for: .valueChanged)
        return switchButton
    }()
    private lazy var detailLabel: UILabel = {
        let detailLabel = UILabel()
        detailLabel.textAlignment = .right
        detailLabel.font = UIFont.systemFont(ofSize: 14)
        detailLabel.isHidden = true
        detailLabel.textColor = UIColor(netHex: 0x999999)
        detailLabel.backgroundColor = .white
        detailLabel.layer.masksToBounds = true
        return detailLabel
    }()
    
    //MARK: - private func
    private func _init() {
        contentView.addSubview(switchButton)
        contentView.addSubview(titleLabel)
        contentView.addSubview(detailLabel)

        addConstraint(_JCLayoutConstraintMake(titleLabel, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .right, .equal, contentView, .centerX))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .height, .equal, nil, .notAnAttribute, 22.5))
        
        addConstraint(_JCLayoutConstraintMake(detailLabel, .centerY, .equal, contentView, .centerY))
        addConstraint(_JCLayoutConstraintMake(detailLabel, .left, .equal, titleLabel, .right))
        addConstraint(_JCLayoutConstraintMake(detailLabel, .right, .equal, contentView, .right))
        addConstraint(_JCLayoutConstraintMake(detailLabel, .height, .equal, contentView, .height))

        addConstraint(_JCLayoutConstraintMake(switchButton, .right, .equal, contentView, .right, -15))
        addConstraint(_JCLayoutConstraintMake(switchButton, .centerY, .equal, contentView, .centerY))
    }
    
    func clickSwitch(_ sender: UISwitch) {
        delegate?.mineInfoCell?(clickSwitchButton: sender, indexPath: indexPate)
    }

}

//
//  JCDoubleButtonCell.swift
//  JChat
//
//  Created by deng on 2017/5/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@objc public protocol JCDoubleButtonCellDelegate: NSObjectProtocol {
    @objc optional func doubleButtonCell(clickLeftButton button: UIButton)
    @objc optional func doubleButtonCell(clickRightButton button: UIButton)
}

class JCDoubleButtonCell: UITableViewCell {

    open weak var delegate: JCDoubleButtonCellDelegate?
    
    var leftButtonTitle: String {
        get {
            return leftButton.titleLabel?.text ?? ""
        }
        set {
            leftButton.setTitle(newValue, for: .normal)
        }
    }
    
    var leftButtonColor: UIColor {
        get {
            return color
        }
        set {
            color = newValue
            leftButton.backgroundColor = newValue
        }
    }
    
    var rightButtonTitle: String {
        get {
            return rightButton.titleLabel?.text ?? ""
        }
        set {
            rightButton.setTitle(newValue, for: .normal)
        }
    }
    
    var rightButtonColor: UIColor {
        get {
            return color
        }
        set {
            color = newValue
            rightButton.backgroundColor = newValue
        }
    }
   
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        _init()
    }
    
    private var color: UIColor = O2ThemeManager.color(for: "Base.base_color")!
    private lazy var leftButton: UIButton = UIButton()
    private lazy var rightButton: UIButton = UIButton()
    
    //MARK: - private func
    private func _init() {
        backgroundColor = .clear
        leftButton.addTarget(self, action: #selector(_clickLeftButton(_:)), for: .touchUpInside)
        leftButton.setTitle("加好友", for: .normal)
        leftButton.setTitleColor(.black, for: .normal)
        leftButton.backgroundColor = .white
        leftButton.layer.cornerRadius = 3.0
        leftButton.layer.masksToBounds = true
        contentView.addSubview(leftButton)
        
        rightButton.addTarget(self, action: #selector(_clickRightButton(_:)), for: .touchUpInside)
        rightButton.setTitle("发送消息", for: .normal)
        rightButton.layer.cornerRadius = 3.0
        rightButton.layer.masksToBounds = true
        rightButton.backgroundColor = color
        contentView.addSubview(rightButton)
        
        addConstraint(_JCLayoutConstraintMake(leftButton, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(leftButton, .right, .equal, contentView, .centerX, -12.5))
        addConstraint(_JCLayoutConstraintMake(leftButton, .top, .equal, contentView, .top))
        addConstraint(_JCLayoutConstraintMake(leftButton, .bottom, .equal, contentView, .bottom))
        
        addConstraint(_JCLayoutConstraintMake(rightButton, .left, .equal, contentView, .centerX, 12.5))
        addConstraint(_JCLayoutConstraintMake(rightButton, .right, .equal, contentView, .right, -15))
        addConstraint(_JCLayoutConstraintMake(rightButton, .top, .equal, contentView, .top))
        addConstraint(_JCLayoutConstraintMake(rightButton, .bottom, .equal, contentView, .bottom))
    }
    
    //MARK: - click func
    @objc func _clickLeftButton(_ sender: UIButton) {
        delegate?.doubleButtonCell?(clickLeftButton: sender)
    }
    
     @objc func _clickRightButton(_ sender: UIButton) {
        delegate?.doubleButtonCell?(clickRightButton: sender)
    }

}

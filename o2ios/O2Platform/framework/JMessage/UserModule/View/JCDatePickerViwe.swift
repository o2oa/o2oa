//
//  JCDatePickerViwe.swift
//  YHRSS
//
//  Created by deng on 2017/3/23.
//  Copyright © 2017年 dengyonghao. All rights reserved.
//

import UIKit

@objc public protocol JCDatePickerViweDelegate: NSObjectProtocol {
    @objc optional func datePicker(finish finishButton: UIButton, date: Date)
    @objc optional func datePicker(cancel cancelButton: UIButton, date: Date)
}

class JCDatePickerViwe: UIView {
    
    open weak var delegate: JCDatePickerViweDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private lazy var cancelButton: UIButton = {
        var cancelButton = UIButton()
        cancelButton.layer.borderWidth = 0.5
        cancelButton.layer.borderColor = UIColor.gray.cgColor
        cancelButton.setTitle("取消", for: .normal)
        cancelButton.setTitleColor(.black, for: .normal)
        cancelButton.addTarget(self, action: #selector(_cancel(_:)), for: .touchUpInside)
        return cancelButton
    }()
    private lazy var finishButton: UIButton = {
        var finishButton = UIButton()
        finishButton.layer.borderWidth = 0.5
        finishButton.layer.borderColor = UIColor.gray.cgColor
        finishButton.setTitle("完成", for: .normal)
        finishButton.setTitleColor(.black, for: .normal)
        finishButton.addTarget(self, action: #selector(_finish(_:)), for: .touchUpInside)
        return finishButton
    }()
    private lazy var datePicker: UIDatePicker = {
        var datePicker = UIDatePicker()
        datePicker.calendar = Calendar.current
        datePicker.datePickerMode = .date
        datePicker.locale = Locale(identifier: "zh_CN")
        return datePicker
    }()
    
    //MARK: - private func 
    private func _init() {
        // 216 + 40
        backgroundColor = .white

        addSubview(finishButton)
        addSubview(cancelButton)
        addSubview(datePicker)
        
        addConstraint(_JCLayoutConstraintMake(cancelButton, .left, .equal, self, .left))
        addConstraint(_JCLayoutConstraintMake(cancelButton, .right, .equal, self, .centerX))
        addConstraint(_JCLayoutConstraintMake(cancelButton, .top, .equal, self, .top))
        addConstraint(_JCLayoutConstraintMake(cancelButton, .height, .equal, nil, .notAnAttribute, 40))
        
        addConstraint(_JCLayoutConstraintMake(finishButton, .left, .equal, self, .centerX))
        addConstraint(_JCLayoutConstraintMake(finishButton, .right, .equal, self, .right))
        addConstraint(_JCLayoutConstraintMake(finishButton, .top, .equal, self, .top))
        addConstraint(_JCLayoutConstraintMake(finishButton, .height, .equal, nil, .notAnAttribute, 40))
        
        addConstraint(_JCLayoutConstraintMake(datePicker, .left, .equal, self, .left, 8))
        addConstraint(_JCLayoutConstraintMake(datePicker, .right, .equal, self, .right, -8))
        addConstraint(_JCLayoutConstraintMake(datePicker, .top, .equal, finishButton, .bottom))
        addConstraint(_JCLayoutConstraintMake(datePicker, .height, .equal, nil, .notAnAttribute, 216))
    }
    
    //MARK: - click event
    func _cancel(_ sender: UIButton) {
        delegate?.datePicker?(cancel: sender, date: datePicker.date)
    }
    
    func _finish(_ sender: UIButton) {
        delegate?.datePicker?(finish: sender, date: datePicker.date)
    }

}

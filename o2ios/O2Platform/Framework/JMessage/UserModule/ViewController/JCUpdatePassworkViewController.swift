//
//  JCUpdatePassworkViewController.swift
//  JChat
//
//  Created by deng on 2017/3/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCUpdatePassworkViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    private lazy var oldPasswordTextField: UITextField = {
        var textField = UITextField()
        textField.placeholder = "请输入原始密码"
        textField.isSecureTextEntry = true
        textField.font = UIFont.systemFont(ofSize: 16)
        return textField
    }()
    private lazy var newPasswordTextField: UITextField = {
        var textField = UITextField()
        textField.placeholder = "请设置登录密码"
        textField.isSecureTextEntry = true
        textField.font = UIFont.systemFont(ofSize: 16)
        return textField
    }()
    private lazy var checkPasswordTextField: UITextField = {
        var textField = UITextField()
        textField.placeholder = "请再次输入"
        textField.isSecureTextEntry = true
        textField.font = UIFont.systemFont(ofSize: 16)
        return textField
    }()
    private lazy var updateButton: UIButton = {
        var updateButton = UIButton()
        updateButton.setTitle("确认", for: .normal)
        updateButton.layer.cornerRadius = 3.0
        updateButton.layer.masksToBounds = true
        updateButton.addTarget(self, action: #selector(_updatePasswork), for: .touchUpInside)
        return updateButton
    }()
    
    private lazy var oldPasswordLabel: UILabel = {
        var label = UILabel()
        label.text = "原始密码"
        label.font = UIFont.systemFont(ofSize: 16)
        return label
    }()
    private lazy var newPasswordLabel: UILabel = {
        var label = UILabel()
        label.text = "新密码"
        label.font = UIFont.systemFont(ofSize: 16)
        return label
    }()
    private lazy var checkPasswordLabel: UILabel = {
        var label = UILabel()
        label.text = "确认密码"
        label.font = UIFont.systemFont(ofSize: 16)
        return label
    }()
    
    private lazy var line1: UILabel = {
        var label = UILabel()
        label.backgroundColor = UIColor(netHex: 0xd9d9d9)
        return label
    }()
    private lazy var line2: UILabel = {
        var label = UILabel()
        label.backgroundColor = UIColor(netHex: 0xd9d9d9)
        return label
    }()
    
    private lazy var bgView: UIView = UIView()
    
    //MARK: - private func 
    private func _init() {
        self.title = "修改密码"
        view.backgroundColor = UIColor(netHex: 0xe8edf3)

        updateButton.setBackgroundImage(UIImage.createImage(color: UIColor(netHex: 0x2dd0cf), size: CGSize(width: view.width - 30, height: 40)), for: .normal)
        
        bgView.backgroundColor = .white
        view.addSubview(bgView)
        bgView.addSubview(oldPasswordLabel)
        bgView.addSubview(newPasswordLabel)
        bgView.addSubview(checkPasswordLabel)
        bgView.addSubview(oldPasswordTextField)
        bgView.addSubview(newPasswordTextField)
        bgView.addSubview(checkPasswordTextField)
        bgView.addSubview(line1)
        bgView.addSubview(line2)
        view.addSubview(updateButton)
        
        view.addConstraint(_JCLayoutConstraintMake(bgView, .left, .equal, view, .left))
        view.addConstraint(_JCLayoutConstraintMake(bgView, .right, .equal, view, .right))
        if isIPhoneX {
            view.addConstraint(_JCLayoutConstraintMake(bgView, .top, .equal, view, .top, 88))
        } else {
            view.addConstraint(_JCLayoutConstraintMake(bgView, .top, .equal, view, .top, 64))
        }
        view.addConstraint(_JCLayoutConstraintMake(bgView, .height, .equal, nil, .notAnAttribute, 135))
        
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordLabel, .left, .equal, bgView, .left, 15))
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordLabel, .top, .equal, bgView, .top, 11.5))
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordLabel, .width, .equal, nil, .notAnAttribute, 75))
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordLabel, .height, .equal, nil, .notAnAttribute, 22.5))
        
        bgView.addConstraint(_JCLayoutConstraintMake(line1, .left, .equal, oldPasswordLabel, .left))
        bgView.addConstraint(_JCLayoutConstraintMake(line1, .top, .equal, bgView, .top, 44.5))
        bgView.addConstraint(_JCLayoutConstraintMake(line1, .right, .equal, bgView, .right, -15))
        bgView.addConstraint(_JCLayoutConstraintMake(line1, .height, .equal, nil, .notAnAttribute, 0.5))
        
        bgView.addConstraint(_JCLayoutConstraintMake(line2, .left, .equal, line1, .left))
        bgView.addConstraint(_JCLayoutConstraintMake(line2, .top, .equal, line1, .top, 45))
        bgView.addConstraint(_JCLayoutConstraintMake(line2, .right, .equal, line1, .right))
        bgView.addConstraint(_JCLayoutConstraintMake(line2, .height, .equal, nil, .notAnAttribute, 0.5))
        
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordLabel, .left, .equal, oldPasswordLabel, .left))
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordLabel, .top, .equal, line1, .bottom, 11.5))
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordLabel, .width, .equal, nil, .notAnAttribute, 75))
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordLabel, .height, .equal, nil, .notAnAttribute, 22.5))
        
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordLabel, .left, .equal, oldPasswordLabel, .left))
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordLabel, .top, .equal, line2, .bottom, 11.5))
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordLabel, .width, .equal, nil, .notAnAttribute, 75))
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordLabel, .height, .equal, nil, .notAnAttribute, 22.5))
        
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordTextField, .left, .equal, oldPasswordLabel, .right, 20))
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordTextField, .top, .equal, bgView, .top, 11.5))
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordTextField, .right, .equal, bgView, .right, -15))
        bgView.addConstraint(_JCLayoutConstraintMake(oldPasswordTextField, .height, .equal, nil, .notAnAttribute, 22.5))
        
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordTextField, .left, .equal, oldPasswordTextField, .left))
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordTextField, .top, .equal, line1, .bottom, 11.5))
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordTextField, .right, .equal, oldPasswordTextField, .right))
        bgView.addConstraint(_JCLayoutConstraintMake(newPasswordTextField, .height, .equal, oldPasswordTextField, .height))
        
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordTextField, .left, .equal, oldPasswordTextField, .left))
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordTextField, .top, .equal, line2, .bottom, 11.5))
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordTextField, .right, .equal, oldPasswordTextField, .right))
        bgView.addConstraint(_JCLayoutConstraintMake(checkPasswordTextField, .height, .equal, oldPasswordTextField, .height))
        
        view.addConstraint(_JCLayoutConstraintMake(updateButton, .left, .equal, view, .left, 15))
        view.addConstraint(_JCLayoutConstraintMake(updateButton, .right, .equal, view, .right, -15))
        view.addConstraint(_JCLayoutConstraintMake(updateButton, .top, .equal, bgView, .bottom, 15))
        view.addConstraint(_JCLayoutConstraintMake(updateButton, .height, .equal, nil, .notAnAttribute, 40))
    }
    
    //MARK: - click event
    func _updatePasswork() {
        view.endEditing(true)
        let oldPassword = oldPasswordTextField.text!
        let newPassword = newPasswordTextField.text!
        let checkPassword = checkPasswordTextField.text!
        if oldPassword.isEmpty || newPassword.isEmpty || checkPassword.isEmpty {
            MBProgressHUD_JChat.show(text: "所有信息不能为空", view: view)
            return
        }
        if newPassword != checkPassword {
            MBProgressHUD_JChat.show(text: "新密码和确认密码不一致", view: view)
            return
        }
        
        MBProgressHUD_JChat.showMessage(message: "修改中", toView: view)
        JMSGUser.updateMyPassword(withNewPassword: newPassword, oldPassword: oldPassword) { (result, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                self.navigationController?.popViewController(animated: true)
            } else {
                MBProgressHUD_JChat.show(text: "更新失败", view: self.view)
            }
        }
    }
}

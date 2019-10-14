//
//  JCRegisterViewController.swift
//  JChat
//
//  Created by deng on 2017/2/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCRegisterViewController: UIViewController {
    
    //MARK: - life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        UIApplication.shared.setStatusBarStyle(.default, animated: false)
        navigationController?.setNavigationBarHidden(true, animated: false)
        _updateRegisterButton()
    }

    //MARK: - property
    fileprivate lazy var headerView: UIView = {
        let view = UIView(frame: CGRect(x: 0, y: -64, width: self.view.width, height: 64))
        view.backgroundColor = UIColor(netHex: 0x2DD0CF)
        let title = UILabel(frame: CGRect(x: self.view.centerX - 10, y: 20, width: 200, height: 44))
        title.font = UIFont.systemFont(ofSize: 18)
        title.textColor = .white
        title.text = "JChat"
        view.addSubview(title)
        
        var rightButton = UIButton(frame: CGRect(x: view.width - 50 - 15, y: 20 + 7, width: 50, height: 30))
        rightButton.setTitle("去登录", for: .normal)
        rightButton.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        rightButton.addTarget(self, action: #selector(_clickLoginButton), for: .touchUpInside)
        view.addSubview(rightButton)
        return view
    }()
    
    private lazy var passwordTextField: UITextField = {
        var textField = UITextField()
        textField.addTarget(self, action: #selector(textFieldDidChanged(_ :)), for: .editingChanged)
        textField.clearButtonMode = .whileEditing
        textField.tag = 1002
        textField.delegate = self
        textField.placeholder = "请输入密码"
        textField.isSecureTextEntry = true
        textField.font = UIFont.systemFont(ofSize: 16)
        let x = 38 + 18 + 15
        let y = 108 + 80 + 60 + 27 + 30
        let w = Int(self.view.width - 76 - 33)
        let h = 40
        textField.frame = CGRect(x: x, y: x, width: w, height: h)
        return textField
    }()
    
    private lazy var userNameTextField: UITextField = {
        var textField = UITextField()
        textField.addTarget(self, action: #selector(textFieldDidChanged(_ :)), for: .editingChanged)
        textField.clearButtonMode = .whileEditing
        textField.tag = 1001
        textField.delegate = self
        textField.placeholder = "请输入用户名"
        textField.font = UIFont.systemFont(ofSize: 16)
        textField.frame = CGRect(x: 38 + 18 + 15, y: 108 + 80 + 60, width: self.view.width - 76 - 33, height: 40)
        return textField
    }()
    
    fileprivate lazy var avatorView: UIImageView = {
        var avatorView = UIImageView()
        avatorView.frame = CGRect(x: self.view.centerX - 40, y: 108, width: 80, height: 80)
        avatorView.image = UIImage.loadImage("com_icon_80")
        return avatorView
    }()
    
    private lazy var loginButton: UIButton = {
        var button = UIButton()
        button.frame = CGRect(x: self.view.centerX + 12, y: self.view.height - 42, width: 50, height: 16.5)
        button.setTitle("立即登录", for: .normal)
        button.titleLabel?.font = UIFont.systemFont(ofSize: 12)
        button.setTitleColor(UIColor(netHex: 0x2DD0CF), for: .normal)
        button.addTarget(self, action: #selector(_clickLoginButton), for: .touchUpInside)
        return button
    }()
    
    private lazy var registerButton: UIButton = {
        var button = UIButton()
        button.backgroundColor = UIColor(netHex: 0x2DD0CF)
        button.frame = CGRect(x: 38, y: 108 + 185 + 80, width: self.view.width - 76, height: 40)
        button.setTitle("注册", for: .normal)
        button.layer.cornerRadius = 3.0
        button.layer.masksToBounds = true
        button.addTarget(self, action: #selector(_userRegister), for: .touchUpInside)
        return button
    }()
    
    private lazy var tipsLabel: UILabel = {
        var label = UILabel()
        label.frame = CGRect(x: self.view.centerX - 62, y: self.view.height - 42, width: 74, height: 16.5)
        label.text = "已注册账号？"
        label.textColor = UIColor(netHex: 0x999999)
        label.font = UIFont.systemFont(ofSize: 12)
        return label
    }()
    
    fileprivate lazy var passwordIcon: UIImageView = {
        let imageView = UIImageView()
        imageView.frame = CGRect(x: 38, y: 108 + 80 + 60 + 27 + 30 + 11 , width: 18, height: 18)
        imageView.image = UIImage.loadImage("com_icon_password")
        return imageView
    }()
    
    fileprivate lazy var usernameIcon: UIImageView = {
        let imageView = UIImageView()
        imageView.frame = CGRect(x: 38, y: 108 + 80 + 60 + 11 , width: 18, height: 18)
        imageView.image = UIImage.loadImage("com_icon_user_18")
        return imageView
    }()
    
    fileprivate lazy var usernameLine: UILabel = {
        var line = UILabel()
        line.backgroundColor = UIColor(netHex: 0x2DD0CF)
        line.alpha = 0.4
        line.frame = CGRect(x: 38, y: self.userNameTextField.y + 40, width: self.view.width - 76, height: 1)
        return line
    }()
    
    fileprivate lazy var passwordLine: UILabel = {
        var line = UILabel()
        line.backgroundColor = UIColor(netHex: 0x2DD0CF)
        line.alpha = 0.4
        line.frame = CGRect(x: 38, y: self.passwordTextField.y + 40, width: self.view.width - 76, height: 1)
        return line
    }()
    
    fileprivate lazy var bgView: UIView = UIView(frame: self.view.frame)
    
    //MARK: - private mothed
    private func _init() {
        self.title = "JChat"
        view.backgroundColor = .white
        automaticallyAdjustsScrollViewInsets = false
        
        view.addSubview(bgView)
        view.addSubview(headerView)
        bgView.addSubview(avatorView)
        bgView.addSubview(tipsLabel)
        bgView.addSubview(userNameTextField)
        bgView.addSubview(passwordTextField)
        bgView.addSubview(loginButton)
        bgView.addSubview(registerButton)
        bgView.addSubview(usernameIcon)
        bgView.addSubview(passwordIcon)
        bgView.addSubview(usernameLine)
        bgView.addSubview(passwordLine)
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(_tapView))
        bgView.addGestureRecognizer(tap)
    }
    
    @objc func textFieldDidChanged(_ textField: UITextField) {
        _updateRegisterButton()
    }
    
    @objc func _tapView() {
        view.endEditing(true)
    }
    
    //MARK: - click event
    @objc func _userRegister() {
        userNameTextField.resignFirstResponder()
        passwordTextField.resignFirstResponder()
        let username = userNameTextField.text ?? ""
        let password = passwordTextField.text ?? ""
        
        let validateUsername = UserDefaultValidationService.sharedValidationService.validateUsername(username)
        if !(validateUsername == .ok) {
            MBProgressHUD_JChat.show(text: validateUsername.description, view: view)
            return
        }
        
        let validatePassword = UserDefaultValidationService.sharedValidationService.validatePassword(password)
        if !(validatePassword == .ok) {
            MBProgressHUD_JChat.show(text: validatePassword.description, view: view)
            return
        }
        
        MBProgressHUD_JChat.showMessage(message: "注册中", toView: view)

        JMSGUser.register(withUsername: username, password: password) { (result, error) in
            let _ = DispatchQueue.main.async {
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    let vc = JCRegisterInfoViewController()
                    vc.username = username
                    vc.password = password
                    self.navigationController?.pushViewController(vc, animated: true)
                } else {
                    MBProgressHUD_JChat.show(text: String.errorAlert(error! as NSError), view: self.view)
                }
            }
        }
    }
    
    @objc func _clickLoginButton() {
        navigationController?.popViewController(animated: true)
    }
    
    @objc func _updateRegisterButton() {
        if (userNameTextField.text?.isEmpty)! || (passwordTextField.text?.isEmpty)! {
            registerButton.isEnabled = false
            registerButton.alpha = 0.7
        } else {
            registerButton.isEnabled = true
            registerButton.alpha = 1.0
        }
    }
    
}

extension JCRegisterViewController: UITextFieldDelegate {
    func textFieldDidBeginEditing(_ textField: UITextField) {
        _updateRegisterButton()
        if textField.tag == 1001 {
            usernameLine.alpha = 1.0
            usernameIcon.image = UIImage.loadImage("com_icon_user_18_pre")
        } else {
            passwordLine.alpha = 1.0
            passwordIcon.image = UIImage.loadImage("com_icon_password_pre")
        }
        UIApplication.shared.setStatusBarStyle(.lightContent, animated: false)
        
        UIView.animate(withDuration: 0.3, animations: {
            self.avatorView.isHidden = true
            self.headerView.frame = CGRect(x: 0, y: 0, width: self.view.width, height: 64)
            self.bgView.frame = CGRect(x: 0, y: -100, width: self.view.width, height: self.view.height)
        })
        
    }
    func textFieldDidEndEditing(_ textField: UITextField) {
        _updateRegisterButton()
        if textField.tag == 1001 {
            usernameLine.alpha = 0.4
            usernameIcon.image = UIImage.loadImage("com_icon_user_18")
        } else {
            passwordLine.alpha = 0.4
            passwordIcon.image = UIImage.loadImage("com_icon_password")
        }
        
        UIApplication.shared.setStatusBarStyle(.default, animated: false)
        UIView.animate(withDuration: 0.3) {
            self.avatorView.isHidden = false
            self.headerView.frame = CGRect(x: 0, y: -64, width: self.view.width, height: 64)
            self.bgView.frame = CGRect(x: 0, y: 0, width: self.view.width, height: self.view.height)
        }
    }
}

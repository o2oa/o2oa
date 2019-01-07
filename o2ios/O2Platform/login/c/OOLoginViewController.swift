//
//  OOLoginViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/9.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import ReactiveCocoa
import ReactiveSwift
import Whisper
import CocoaLumberjack
import AVFoundation
import O2OA_Auth_SDK

class OOLoginViewController: OOBaseViewController {

    @IBOutlet weak var logoImageView: UIImageView!
    
    @IBOutlet weak var userNameTextField: OOUITextField!
    
    @IBOutlet weak var passwordTextField: OOUIDownButtonTextField!
    
    @IBOutlet weak var passwordField: OOUITextField!
    
    @IBOutlet weak var copyrightLabel: UILabel!
    
    @IBOutlet weak var submitButton: OOBaseUIButton!
    
    @IBOutlet weak var rebindBtn: UIButton!
    
    @IBOutlet weak var faceRecgnizeLoginBtn: UIButton!
    
    @IBAction func faceRecgnizeAction(_ sender: UIButton) {
        DDLogDebug("点了，使用人脸识别登录")
        let status = AVCaptureDevice.authorizationStatus(for: .video)
        if status == .denied || status == .restricted {
            ProgressHUD.showError("没有摄像头权限，请先开启！")
        }else {
            let faceVC = OOFaceRecognizeLoginViewController()
            let nav = ZLNavigationController(rootViewController: faceVC)
            nav.modalTransitionStyle = .flipHorizontal
            self.present(nav, animated: true, completion: nil)
        }
        
    }
    var viewModel:OOLoginViewModel = {
       return OOLoginViewModel()
    }()
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //delegate
        passwordTextField.buttonDelegate = self
        setupUI()
    }
    
    private func setupUI(){
        logoImageView.image = OOCustomImageManager.default.loadImage(.login_avatar)
        let backImageView = UIImageView(image: #imageLiteral(resourceName: "pic_beijing"))
        backImageView.frame = self.view.frame
        //毛玻璃效果
        let blur = UIBlurEffect(style: .light)
        let effectView = UIVisualEffectView(effect: blur)
        effectView.frame = backImageView.frame
        backImageView.addSubview(effectView)
        
        
        if O2IsConnect2Collect {
            self.rebindBtn.isHidden = false
            self.passwordTextField.isHidden = false //验证码
            self.passwordField.isHidden = true //密码
            if let host = O2AuthSDK.shared.bindUnit()?.centerHost, (host == "dev.o2oa.io" || host == "dev.o2server.io" || host == "dev.o2oa.net") {
                self.faceRecgnizeLoginBtn.isHidden = false
            }else {
                self.faceRecgnizeLoginBtn.isHidden = true
            }
        }else {
            self.rebindBtn.isHidden = true
            self.passwordTextField.isHidden = true
            self.passwordField.isHidden = false
        }
        
        self.passwordTextField.reactive.isEnabled <~ viewModel.passwordIsValid
        self.passwordField.reactive.isEnabled <~ viewModel.pwdIsValid
        self.passwordTextField.downButton!.reactive.isEnabled <~ viewModel.passwordIsValid
        self.submitButton.reactive.isEnabled <~ viewModel.submitButtionIsValid
        self.submitButton.reactive.backgroundColor <~ viewModel.submitButtonCurrentColor
        self.faceRecgnizeLoginBtn.reactive.isEnabled <~ viewModel.faceRecognizeLoginButtonisValid
        if O2IsConnect2Collect {
            viewModel.loginControlIsValid(self.userNameTextField, self.passwordTextField, false)
        }else {
            viewModel.loginControlIsValid(self.userNameTextField, self.passwordField, true)
        }
        
        viewModel.faceRecognizeValidate()
        
        
        //版权信息
        self.view.insertSubview(backImageView, belowSubview: self.logoImageView)
        let year = Calendar.current.component(Calendar.Component.year, from: Date())
        copyrightLabel.text = "Copyright © 2015 - \(year)  All Rights Reserved"
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func btnReBindNodeAction(_ sender: UIButton) {
        showAlert(title: "重新绑定", message: "重新绑定到新的服务节点，原节点信息将被清空，确认吗？", okHandler: { (ok) in
            O2AuthSDK.shared.clearAllInformationBeforeReBind(callback: { (result, msg) in
                DDLogInfo("清空登录和绑定信息，result:\(result), msg:\(msg ?? "")")
                OOAppsInfoDB.shareInstance.removeAll()
                DispatchQueue.main.async {
                    self.forwardDestVC("login", nil)
                }
            })
            
        }) { (cancel) in
            
        }
        
    }
    
    
    @IBAction func btnLogin(_ sender: OOBaseUIButton) {
        self.view.endEditing(true)
        let credential = userNameTextField.text ?? ""
        var codeAnswer = ""
        if O2IsConnect2Collect {
            codeAnswer = passwordTextField.text ?? ""
        }else {
            codeAnswer = passwordField.text ?? ""
        }
        
        if credential == "" || codeAnswer == "" {
            self.showError(title: "手机号码或密码不能为空！")
            return
        }
        self.showMessage(title: "登录中...")
        if O2IsConnect2Collect {
            passwordTextField.stopTimerButton()
            O2AuthSDK.shared.login(mobile: credential, code: codeAnswer) { (result, msg) in
                if result {
                    self.dismissProgressHUD()
                    self.gotoMain()
                }else  {
                    self.showError(title: "登录失败,\(msg ?? "")")
                }
            }
        }else {
            //todo内网版本登录
            O2AuthSDK.shared.loginWithPassword(username: credential, password: codeAnswer) { (result, msg) in
                if result {
                    self.dismissProgressHUD()
                    self.gotoMain()
                }else  {
                    self.showError(title: "登录失败,\(msg ?? "")")
                }
            }
        }
        
    }
    
    private func gotoMain() {
        //跳转到主页
        let destVC = OOTabBarController.genernateVC()
        destVC.selectedIndex = 2 // 首页选中 TODO 图标不亮。。。。。
        UIApplication.shared.keyWindow?.rootViewController = destVC
        UIApplication.shared.keyWindow?.makeKeyAndVisible()
    }

}

extension OOLoginViewController:OOUIDownButtonTextFieldDelegate {
    func viewButtonClicked(_ textField: OOUIDownButtonTextField, _ sender: OOTimerButton) {
        guard let credential = userNameTextField.text else {
            self.showError(title: "请输入手机号码！")
            sender.stopTiming()
            return
        }
        O2AuthSDK.shared.sendLoginSMS(mobile: credential) { (result, msg) in
            if !result {
                DDLogError((msg ?? ""))
            }
        }
        
    }
}


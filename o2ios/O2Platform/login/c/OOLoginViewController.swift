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
    
    @IBOutlet weak var bioAuthLoginBtn: UIButton!
    
//    @IBOutlet weak var faceRecgnizeLoginBtn: UIButton!
    
//    @IBAction func faceRecgnizeAction(_ sender: UIButton) {
//        DDLogDebug("点了，使用人脸识别登录")
//        let status = AVCaptureDevice.authorizationStatus(for: .video)
//        if status == .denied || status == .restricted {
//            ProgressHUD.showError("没有摄像头权限，请先开启！")
//        }else {
//            let faceVC = OOFaceRecognizeLoginViewController()
//            let nav = ZLNavigationController(rootViewController: faceVC)
//            nav.modalTransitionStyle = .flipHorizontal
//            self.present(nav, animated: true, completion: nil)
//        }
//
//    }
    var viewModel:OOLoginViewModel = {
       return OOLoginViewModel()
    }()
    
    var notUseBioAuth = false
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //delegate
        passwordTextField.buttonDelegate = self
        setupUI()
        
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let bioAuthUser = AppConfigSettings.shared.bioAuthUser
        if !notUseBioAuth && !bioAuthUser.isEmpty {
            DDLogDebug("已开启生物识别认证")
            self.gotoBioAuthLogin()
        }
        self.bioAuthLoginBtn.isHidden = bioAuthUser.isEmpty
    }
    
    @IBAction func unwindFromBioAuthLogin(_ unwindSegue: UIStoryboardSegue) {
        if unwindSegue.identifier == "goBack2Login" {
            DDLogDebug("从生物识别认证页面返回的，所以不需要再跳转了。。。。。。")
            notUseBioAuth = true
        }
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
        // 皮肤
        let baseColor = O2ThemeManager.color(for: "Base.base_color")!
        self.passwordTextField.themeUpdate(buttonTitleColor: baseColor)
        self.passwordTextField.themeUpdate(leftImage: O2ThemeManager.image(for: "Icon.icon_verification_code_nor"), leftLightImage: O2ThemeManager.image(for: "Icon.icon_verification_code_sel"), lineColor: baseColor.alpha(0.4), lineLightColor: baseColor)
        self.passwordField.themeUpdate(leftImage: O2ThemeManager.image(for: "Icon.icon_verification_code_nor"), leftLightImage: O2ThemeManager.image(for: "Icon.icon_verification_code_sel"), lineColor: baseColor.alpha(0.4), lineLightColor: baseColor)
        self.userNameTextField.themeUpdate(leftImage: O2ThemeManager.image(for: "Icon.icon_user_nor"), leftLightImage: O2ThemeManager.image(for: "Icon.icon_user_sel"), lineColor: baseColor.alpha(0.4), lineLightColor: baseColor)
        
        self.passwordTextField.keyboardType = .numberPad
        self.userNameTextField.returnKeyType = .next
        self.userNameTextField.returnNextDelegate = self
        
        if O2IsConnect2Collect {
            self.rebindBtn.isHidden = false
            self.passwordTextField.isHidden = false //验证码
            self.passwordField.isHidden = true //密码
            
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
        if O2IsConnect2Collect {
            viewModel.loginControlIsValid(self.userNameTextField, self.passwordTextField, false)
        }else {
            viewModel.loginControlIsValid(self.userNameTextField, self.passwordField, true)
        }
        
//        viewModel.faceRecognizeValidate()
        
        let bioType = O2BioLocalAuth.shared.checkBiometryType()
        switch bioType {
        case O2BiometryType.FaceID:
            self.bioAuthLoginBtn.setTitle("人脸识别登录", for: .normal)
            break
        case O2BiometryType.TouchID:
            self.bioAuthLoginBtn.setTitle("指纹识别登录", for: .normal)
            break
        default:
            break
        }
        
        
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
        self.showSystemAlert(title: "重新绑定", message: "重新绑定到新的服务节点，原节点信息将被清空，确认吗？") { (action) in
            O2AuthSDK.shared.clearAllInformationBeforeReBind(callback: { (result, msg) in
                DDLogInfo("清空登录和绑定信息，result:\(result), msg:\(msg ?? "")")
                OOAppsInfoDB.shareInstance.removeAll()
                DispatchQueue.main.async {
                    self.forwardDestVC("login", nil)
                }
            })
        }
        
    }
    
    @IBAction func bioAuthLoginBtnAction(_ sender: UIButton) {
        self.gotoBioAuthLogin()
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
        let destVC = O2MainController.genernateVC()
        destVC.selectedIndex = 2 // 首页选中 TODO 图标不亮。。。。。
        UIApplication.shared.keyWindow?.rootViewController = destVC
        UIApplication.shared.keyWindow?.makeKeyAndVisible()
    }

    private func gotoBioAuthLogin() {
        DispatchQueue.main.async {
            self.performSegue(withIdentifier: "showBioAuthLogin", sender: nil)
        }
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
                self.showError(title: "验证码发送失败！")
            }
        }
        
    }
}

extension OOLoginViewController: OOUITextFieldReturnNextDelegate {
    func next() {
        if self.userNameTextField.isFirstResponder {
            if self.passwordField.isHidden == false {
                self.passwordField.becomeFirstResponder()
            }
            if self.passwordTextField.isHidden == false {
                self.passwordTextField.becomeFirstResponder()
            }
        }
    }
    
    
}


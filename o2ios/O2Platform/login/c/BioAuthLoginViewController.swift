//
//  BioAuthLoginViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/3/11.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import O2OA_Auth_SDK
import CocoaLumberjack

class BioAuthLoginViewController: UIViewController {

    @IBOutlet weak var bioImageView: UIImageView!
    @IBOutlet weak var bioAuthBtn: OOBaseUIButton!
    
    
    var bioType = O2BiometryType.None
    var typeTitle = "立即验证登录"
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let bioAuthUser = AppConfigSettings.shared.bioAuthUser
        if bioAuthUser.isEmpty {
            bioAuthBtn.isEnabled = false
        }else {
            bioType = O2BioLocalAuth.shared.checkBiometryType()
            switch bioType {
            case O2BiometryType.FaceID:
                typeTitle = "立即验证登录"
                bioImageView.image = UIImage(named: "faceid")
                bioAuthBtn.setTitle(typeTitle, for: .normal)
                bioAuthBtn.isEnabled = true
                break
            case O2BiometryType.TouchID:
                typeTitle = "立即验证登录"
                bioImageView.image = UIImage(named: "touchid")
                bioAuthBtn.setTitle(typeTitle, for: .normal)
                bioAuthBtn.isEnabled = true
                break
            case O2BiometryType.None:
                typeTitle = "登录"
                bioImageView.image = UIImage(named: "pic_o2_moren1")
                bioAuthBtn.setTitle(typeTitle, for: .normal)
                bioAuthBtn.isEnabled = false
                break
            }
        }
        
    }
    

    @IBAction func tapBioAuthLogin(_ sender: OOBaseUIButton) {
        
        if self.bioType != O2BiometryType.None {
            O2BioLocalAuth.shared.auth(reason: "使用\(typeTitle)", selfAuthTitle: "用户名登录", block: { (result, errorMsg) in
                switch result {
                case O2BioEvaluateResult.SUCCESS:
                   // login
                    self.loginO2OA()
                    break
                case O2BioEvaluateResult.FALLBACK:
                    self.back2Login()
                    break
                case O2BioEvaluateResult.LOCKED:
                    self.showSystemAlert(title: "提示", message: "多次错误，已被锁定，请到手机解锁界面输入密码!", okHandler: { (action) in
                        //
                    })
                    break
                    
                case O2BioEvaluateResult.FAILURE:
                    DDLogError(errorMsg)
                    self.showError(title: "验证失败！")
                    break
                }
            })
        }else {
            self.showError(title: "手机系统未开启或不支持识别功能")
        }

    }
    
    @objc private func back2Login() {
        DispatchQueue.main.async {
            self.performSegue(withIdentifier: "goBack2Login", sender: nil)
        }
    }
    
    private func loginO2OA() {
        self.showMessage(title: "登录中...")
        O2AuthSDK.shared.faceRecognizeLogin(userId: AppConfigSettings.shared.bioAuthUser, callback: { (result, msg) in
            if result {
                 self.dismissProgressHUD()
                DispatchQueue.main.async {
                    let destVC = O2MainController.genernateVC()
                    destVC.selectedIndex = 2
                    UIApplication.shared.keyWindow?.rootViewController = destVC
                    UIApplication.shared.keyWindow?.makeKeyAndVisible()
                }
            }else {
                DDLogError("登录失败。。。。。。。\(msg ?? "")")
                self.showError(title: "登录服务器失败，请尝试使用其它方式登录")
            }
        })
    }
    

}

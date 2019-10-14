//
//  LoginViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

import AlamofireObjectMapper
import Alamofire
import SwiftyTimer
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack
import Promises
import O2OA_Auth_SDK

class LoginViewController: UIViewController {
    
    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var startImage: UIImageView!
    
    var viewModel:OOLoginViewModel = {
        return OOLoginViewModel()
    }()
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return UIStatusBarStyle.default
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //load image
        if AppConfigSettings.shared.isFirstTime != true {
            let launchImage = OOCustomImageManager.default.loadImage(.launch_logo)
            iconImageView.image = launchImage
            iconImageView.isHidden = false
        }
        self.startImage.image = UIImage(named: "startImage")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        NotificationCenter.default.addObserver(self, selector: #selector(receiveBindCompleted(customNotification:)), name: OONotification.bindCompleted.notificationName, object: nil)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if AppConfigSettings.shared.isFirstTime == true {
            O2Logger.info("启动开始 isFirstTime is true")
            AppConfigSettings.shared.isFirstTime = false
            let pVC = OOGuidePageController(nibName: "OOGuidePageController", bundle: nil)
            //let navVC = ZLNavigationController(rootViewController: pVC)
            self.presentVC(pVC)
        }else{
            O2Logger.info("启动开始 isFirstTime is false")
            self.startFlowForPromise()
        }
    }

    func startFlowForPromise() {
        
        if !O2IsConnect2Collect {
            let unit = O2BindUnitModel()
            if let infoPath = Bundle.main.path(forResource: "Info", ofType: "plist"), let dic = NSDictionary(contentsOfFile: infoPath) {
                let o2Server = dic["o2 server"] as? NSDictionary
                let id = o2Server?["id"] as? String
                let name = o2Server?["name"] as? String
                let centerHost = o2Server?["centerHost"] as? String
                let centerContext = o2Server?["centerContext"] as? String
                let centerPort = o2Server?["centerPort"] as? Int
                let httpProtocol = o2Server?["httpProtocol"] as? String
                O2Logger.debug("连接服务器：\(String(describing: name)) , host:\(String(describing: centerHost)) , context:\(String(describing: centerContext)), port:\(centerPort ?? 0), portocal:\(String(describing: httpProtocol)) ")
                if name == nil || centerHost == nil || centerContext == nil {
                    self.showError(title:  "服务器配置信息异常！")
                    return
                }
                unit.id = id
                unit.centerContext = centerContext
                unit.centerHost = centerHost
                unit.centerPort = centerPort
                unit.httpProtocol = httpProtocol
                unit.name = name
            }else {
                self.showError(title:  "没有配置服务器信息！")
                return
            }
            
            O2AuthSDK.shared.launchInner(unit: unit) { (state, msg) in
                switch state {
                case .bindError:
                    //校验绑定结点信息错误
                   self.showError(title: "未知错误！")
                    break
                case .loginError:
                    self.forwardToSegue("loginSystemSegue")
                    //自动登录出错
                    break
                case .unknownError:
                    self.showError(title: msg ?? "未知错误！")
                    break
                case .success:
                    //处理移动端应用
                    self.viewModel._saveAppConfigToDb()
                    //跳转到主页
                    let destVC = O2MainController.genernateVC()
                    destVC.selectedIndex = 2 // 首页选中 TODO 图标不亮。。。。。
                    UIApplication.shared.keyWindow?.rootViewController = destVC
                    UIApplication.shared.keyWindow?.makeKeyAndVisible()
                }
            }
        }else {
            //本地 -> 校验 -> 下载NodeAPI -> 下载configInfo -> 自动登录
            O2AuthSDK.shared.launch { (state, msg) in
                switch state {
                case .bindError:
                    //校验绑定结点信息错误
                    self.forwardToSegue("bindPhoneSegue")
                    break
                case .loginError:
                    self.forwardToSegue("loginSystemSegue")
                    //自动登录出错
                    break
                case .unknownError:
//                    self.showError(title: msg ?? "未知错误！")
                    self.needReBind(msg ?? "未知错误！")
                    break
                case .success:
                    //处理移动端应用
                    self.viewModel._saveAppConfigToDb()
                    //跳转到主页
                    let destVC = O2MainController.genernateVC()
                    destVC.selectedIndex = 2 // 首页选中 TODO 图标不亮。。。。。
                    UIApplication.shared.keyWindow?.rootViewController = destVC
                    UIApplication.shared.keyWindow?.makeKeyAndVisible()
                }
            }
        }
        
    }

    

    
    // MARK:- 到不同的segue
    func forwardToSegue(_ segueIdentitifer:String){
        DispatchQueue.main.async {
            self.performSegue(withIdentifier: segueIdentitifer, sender: nil)
        }
    }
    
    @objc func receiveBindCompleted(customNotification:Notification){
        self.startFlowForPromise()
    }
    
    private func needReBind(_ error: String) {
        DispatchQueue.main.async {
            let alertController = UIAlertController(title: "提示", message: "加载出错，是否重新绑定？错误：\(error)", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "重新绑定", style: .default, handler: {(action) in
                self.rebind()
            })
            let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: {(action) in
                
            })
            alertController.addAction(okAction)
            alertController.addAction(cancelAction)
            self.present(alertController, animated: true, completion: nil)
        }
    }
    
    private func rebind() {
        O2AuthSDK.shared.clearAllInformationBeforeReBind(callback: { (result, msg) in
            DDLogInfo("清空登录和绑定信息，result:\(result), msg:\(msg ?? "")")
            OOAppsInfoDB.shareInstance.removeAll()
            DispatchQueue.main.async {
                self.forwardToSegue("bindPhoneSegue")
            }
        })
    }
    

    @IBAction func unBindComplete(_ sender: UIStoryboardSegue){
        //绑定完成，执行
        self.startFlowForPromise()
    }
    
    @IBAction func show(_ sender: UITapGestureRecognizer) {
        //ProgressHUD.show("系统加截中，请稍候...", interaction: true)
    }
    
    //登录后返回执行此方法
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
}



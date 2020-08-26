//
//  QRCodeResultViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/8/26.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import CocoaLumberjack
import O2OA_Auth_SDK

class QRCodeResultViewController: UIViewController {
    
    ///打开扫码结果
    static func openQRResult(result: String, vc: UIViewController) {
        let resultVC = QRCodeResultViewController()
        resultVC.scanResult = result
        vc.navigationController?.pushViewController(resultVC, animated: false)
    }
    
    @IBOutlet weak var loginStackView: UIStackView!
    @IBOutlet weak var loginImage: UIImageView!
    @IBOutlet weak var loginBtn: UIButton!
    @IBOutlet weak var resultLabel: O2CanCopyUILabel!
    
    //扫码结果
    var scanResult: String?
    
    //登录url
    private var loginURL: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if let result = scanResult {
            self.showLoading()
            //开始解析结果
            //todo 判断url还是其他
            self.resolveResult(result: result)
        }else {
            self.title = "扫码结果"
            self.resultLabel.isHidden = false
            self.resultLabel.text = "扫码结果为空"
        }
    }

    @IBAction func tap2Login(_ sender: UIButton) {
        //点击登陆
        if let login = self.loginURL {
            self.showLoading()
            let account = O2AuthSDK.shared.myInfo()
            Alamofire.request(login, method: .post, parameters: nil, encoding: JSONEncoding.default, headers: ["x-token":(account?.token)!]).responseJSON(completionHandler: { (response) in
                switch response.result {
                case .success(let val):
                    DispatchQueue.main.async {
                        DDLogDebug(String(describing:val))
                        self.hideLoading()
                        let alertController = UIAlertController(title: "扫描结果", message: "PC端登录成功", preferredStyle: .alert)
                        let okAction = UIAlertAction(title: "确定", style: .default) {
                            action in
                            self.popVC()
                        }
                        alertController.addAction(okAction)
                        self.presentVC(alertController)
                    }
                case .failure(let err):
                    DispatchQueue.main.async {
                        self.hideLoading()
                        DDLogError(err.localizedDescription)
                        let alertController = UIAlertController(title: "扫描结果", message: "PC端登录失败", preferredStyle: .alert)
                        let okAction = UIAlertAction(title: "确定", style: .destructive) {
                            action in
                            self.popVC()
                        }
                        alertController.addAction(okAction)
                        self.presentVC(alertController)
                    }
                }
            })
        }
    }
    
    ///解析扫码结果
    private func resolveResult(result: String) {
        let url = NSURL(string: result)
       //会议签到功能
       var isMeetingCheck = false
       let allU = url?.absoluteString
       if allU != nil && allU!.contains("/checkin") && allU!.contains("x_meeting_assemble_control") {
           isMeetingCheck = true
       }
        if(isMeetingCheck) {//会议签到
            self.meetingCheck(url: allU!)
        }else {
            self.hideLoading()
            self.title = "扫码登录"
            let query = url?.query
            let querys = query?.split("&")
            var meta = ""
            querys?.forEach { (e) in
                let name = e.split("=")[0]
                if name == "meta" {
                    meta = e.split("=")[1]
                }
            }
            if meta != "" {//登录O2OA
                self.loginURL = AppDelegate.o2Collect.generateURLWithAppContextKey(LoginContext.loginContextKey, query: LoginContext.scanCodeAuthActionQuery, parameter: ["##meta##":meta as AnyObject])
                self.loginStackView.isHidden = false
                self.loginBtn.isHidden = false
                
            }else {//其他扫描结果
//                let alertController = UIAlertController(title: "扫描结果", message: result, preferredStyle: .alert)
//                let okAction = UIAlertAction(title: "确定", style: .default) {
//                    action in
//                    self.popVC()
//                }
//                alertController.addAction(okAction)
//                self.presentVC(alertController)
                self.resultLabel.isHidden = false
                self.resultLabel.text = result
            }
        }
    }
    
    
    //会议签到
    private func meetingCheck(url: String) {
        self.title = "会议签到"
        let account = O2AuthSDK.shared.myInfo()
        Alamofire.request(url, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: ["x-token":(account?.token)!]).responseJSON(completionHandler: {(response) in
            switch response.result {
            case .success(let val):
                DispatchQueue.main.async {
                    self.hideLoading()
                    DDLogDebug(String(describing:val))
                    let alertController = UIAlertController(title: "提示", message: "签到成功", preferredStyle: .alert)
                    let okAction = UIAlertAction(title: "确定", style: .default) {
                        action in
                        self.popVC()
                    }
                    alertController.addAction(okAction)
                    self.presentVC(alertController)
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    self.hideLoading()
                    DDLogError(err.localizedDescription)
                    let alertController = UIAlertController(title: "提示", message: "签到失败", preferredStyle: .alert)
                    let okAction = UIAlertAction(title: "确定", style: .destructive) {
                        action in
                        self.popVC()
                    }
                    alertController.addAction(okAction)
                    self.presentVC(alertController)
                }
            }
        })
    }
    

}

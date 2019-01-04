//
//  NewScanViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/22.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import swiftScan
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import CocoaLumberjack
import O2OA_Auth_SDK

class NewScanViewController: LBXScanViewController {
    
    var myStyle = LBXScanViewStyle() {
        didSet {
            myStyle.centerUpOffset = 44;
            myStyle.photoframeAngleStyle = LBXScanViewPhotoframeAngleStyle.Inner;
            myStyle.photoframeLineW = 2;
            myStyle.photoframeAngleW = 18;
            myStyle.photoframeAngleH = 18;
            myStyle.isNeedShowRetangle = false;
            
            myStyle.anmiationStyle = LBXScanViewAnimationStyle.LineMove;
            
            myStyle.colorAngle = UIColor(red: 0.0/255, green: 200.0/255.0, blue: 20.0/255.0, alpha: 1.0)
            
            //qrcode_Scan_weixin_Line
            myStyle.animationImage = #imageLiteral(resourceName: "qrcode_scan_light_green.png")
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        //self.scanStyle = myStyle
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //扫码结果
    override func handleCodeResult(arrayResult: [LBXScanResult]) {
        
        for result:LBXScanResult in arrayResult
        {
            print("%@",result.strScanned ?? "")
        }
        
        let result:LBXScanResult = arrayResult[0]
        let url = NSURL(string: result.strScanned!)
        let query = url?.query
        let querys = query?.split("&")
        var meta = ""
        querys?.forEach { (e) in
            let name = e.split("=")[0]
            if name == "meta" {
                meta = e.split("=")[1]
            }
        }
        if meta != "" {
            let account = O2AuthSDK.shared.myInfo()
            let loginURL = AppDelegate.o2Collect.generateURLWithAppContextKey(LoginContext.loginContextKey, query: LoginContext.scanCodeAuthActionQuery, parameter: ["##meta##":meta as AnyObject])
            Alamofire.request(loginURL!, method: .post, parameters: nil, encoding: JSONEncoding.default, headers: ["x-token":(account?.token)!]).responseJSON(completionHandler: { (response) in
                switch response.result {
                case .success(let val):
                    DispatchQueue.main.async {
                        DDLogDebug(String(describing:val))
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
        }else {
            let alertController = UIAlertController(title: "扫描结果", message: result.strScanned!, preferredStyle: .alert)
            let okAction = UIAlertAction(title: "确定", style: .default) {
                action in
                self.popVC()
            }
            alertController.addAction(okAction)
            self.presentVC(alertController)
        }

    }

}

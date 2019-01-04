//
//  ScanViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/12.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import CocoaLumberjack
import O2OA_Auth_SDK

class ScanViewController: UIViewController {
    
    var scanView:LXDScanView!
    
    override func viewWillAppear(_ animated: Bool) {
        self.hidesBottomBarWhenPushed =  true
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.scanView.stop()
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.scanView = LXDScanView(showIn: self)
        self.view.addSubview(scanView)
        self.scanView.start()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension ScanViewController:LXDScanViewDelegate{
    func scanView(_ scanView: LXDScanView!, codeInfo: String!) {
        DDLogDebug(codeInfo)
        let url = NSURL(string: codeInfo)
        if let meta = url?.query {
            let metaString = meta.split("=")[1]
            let account = O2AuthSDK.shared.myInfo()
            let loginURL = AppDelegate.o2Collect.generateURLWithAppContextKey(LoginContext.loginContextKey, query: LoginContext.scanCodeAuthActionQuery, parameter: ["##meta##":metaString as AnyObject])
            Alamofire.request(loginURL!, method: .post, parameters: nil, encoding: JSONEncoding.default, headers: ["x-token":(account?.token)!]).responseJSON(completionHandler: { (response) in
                switch response.result {
                    case .success(let val):
                        DispatchQueue.main.async {
                            DDLogDebug(String(describing: val))
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
            
        }else{
            let alertController = UIAlertController(title: "扫描结果", message: codeInfo, preferredStyle: .alert)
            let okAction = UIAlertAction(title: "确定", style: .default) {
                action in
                self.popVC()
            }
            alertController.addAction(okAction)
            self.presentVC(alertController)
        }
        
    }
}

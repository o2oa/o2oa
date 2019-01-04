//
//  MailViewController.swift
//  O2Platform
//
//  Created by 林玲 on 2017/10/20.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack

class MailViewController: BaseWebViewUIViewController {
    
    static var app:O2App?
    static var isIndexShow:Bool?

    override func viewDidLoad() {
        super.viewDidLoad()
        //监听清除缓存之后需要重载
        NotificationCenter.default.addObserver(self, selector: #selector(loadDetailSubject), name: Notification.Name("reloadPortal"), object: nil)
        if MailViewController.isIndexShow ?? false {
            self.navigationItem.leftBarButtonItems = []
        }else {
            //            self.navigationController?.title = MailViewController.app!.title!
            self.title = MailViewController.app!.title!
            let closeBtn = UIButton(frame: CGRect(x: 0, y: 0, w: 30, h: 30))
            closeBtn.setImage(UIImage(named: "icon_off_white2"), for: .normal)
            closeBtn.addTapGesture { (tap) in
                self.navigationController?.dismiss(animated: true, completion: nil)
            }
            let closeItem = UIBarButtonItem(customView: closeBtn)
            
            let backBtn = UIButton(frame: CGRect(x: 0, y: 0, w: 30, h: 30))
            backBtn.setImage(UIImage(named: "icon_fanhui"), for: .normal)
            backBtn.addTapGesture { (tap) in
                self.goBack(isBackBtn: true)
            }
            let backItem = UIBarButtonItem(customView: backBtn)
            
            self.navigationItem.leftBarButtonItems = [backItem, closeItem]
        }
        self.theWebView()
    }

    override func viewWillAppear(_ animated: Bool) {
        if MailViewController.isIndexShow ?? false {
            let statusBarWindow : UIView = UIApplication.shared.value(forKey: "statusBarWindow") as! UIView
            let statusBar : UIView = statusBarWindow.value(forKey: "statusBar") as! UIView
            if statusBar.responds(to:#selector(setter: UIView.backgroundColor)) {
                statusBar.backgroundColor = base_color
            }
            self.navigationController?.navigationBar.isHidden = true
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.navigationBar.isHidden = false
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func theWebView(){
        super.theWebView()
        self.webView.allowsBackForwardNavigationGestures = true
        loadDetailSubject()
    }
    
    @objc func loadDetailSubject(){
        if let req = Alamofire.request((MailViewController.app?.vcName?.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!)!).request {
            self.webView?.load(req)
        }else{
            MBProgressHUD_JChat.show(text: "加载出错，请重试", view: webView, 3.0)
        }
    }
    
    func goBack(isBackBtn: Bool) {
        if self.webView?.canGoBack ?? false {
            self.webView?.goBack()
        }else {
            if isBackBtn {
                self.navigationController?.dismiss(animated: true, completion: nil)
            }
        }
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

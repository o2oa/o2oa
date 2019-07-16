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
    // 首页显示门户 默认没有NavigationBar
    var isIndexShow:Bool = false
    // 门户内部是否有显示NavigationBar
    var hasInnerBar:Bool = false

    override func viewDidLoad() {
        super.viewDidLoad()
        //监听清除缓存之后需要重载
        NotificationCenter.default.addObserver(self, selector: #selector(loadDetailSubject), name: OONotification.reloadPortal.notificationName, object: nil)
        if self.isIndexShow {
            self.navigationItem.leftBarButtonItems = []
        }else {
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
        self.delegate = self
    }

    override func viewWillAppear(_ animated: Bool) {
        if self.isIndexShow || self.hasInnerBar {
            let statusBarWindow : UIView = UIApplication.shared.value(forKey: "statusBarWindow") as! UIView
            let statusBar : UIView = statusBarWindow.value(forKey: "statusBar") as! UIView
            if statusBar.responds(to:#selector(setter: UIView.backgroundColor)) {
                statusBar.backgroundColor = base_color
            }
            self.navigationController?.setNavigationBarHidden(true, animated: true)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: false)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func theWebView(){
        super.theWebView()
        self.view = webView
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

extension MailViewController: BaseWebViewUIViewControllerJSDelegate {
    func closeUIViewWindow() {
        DDLogDebug("关闭啦。。。。。。。。。。。。。。")
        self.navigationController?.dismiss(animated: true, completion: nil)
    }
    func actionBarLoaded(show: Bool) {
        DDLogDebug("actionBar 显示了。。。。\(show)。")
        if(show) {
            self.hasInnerBar = true
            self.navigationController?.setNavigationBarHidden(true, animated: true)
        }
    }
}

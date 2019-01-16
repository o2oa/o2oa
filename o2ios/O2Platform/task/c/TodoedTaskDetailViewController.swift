//
//  TodoedTaskDetailViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack

class TodoedTaskDetailViewController: BaseTaskWebViewController {
    
    @IBOutlet weak var progress: UIProgressView!
    
    var loadUrl:String?
    
    
    var actionModel:TodoedActionModel?{
        didSet {
            if actionModel?.workType == "workCompletedList" {
                self.loadUrl = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.todoedDestopQuery, parameter: ["##workCompletedId##":(actionModel?.workId)! as AnyObject])
            }else{
                self.loadUrl = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.todoDesktopQuery, parameter: ["##workid##":(actionModel?.workId)! as AnyObject])
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.theWebView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        //监控进度
        self.addObserver(webView, forKeyPath: "estimatedProgress", options: .new, context: nil)
    }
    
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.removeObserver(webView, forKeyPath: "estimatedProgress")
    }
    
    override func theWebView(){
        super.theWebView()
        webView.frame = CGRect(x:0,y:0,width:view.frame.width,height:view.frame.height)
        self.view.addSubview(webView)
        webView.navigationDelegate = self
        
        webView.uiDelegate = self
        
        webView.load(URLRequest(url: URL(string: loadUrl!)!))
        
        webView.allowsBackForwardNavigationGestures = true
        
       
    }
    
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "estimatedPrgress" {
            progress.isHidden = webView.estimatedProgress == 1
            progress.setProgress(Float(webView.estimatedProgress), animated: true)
        }
    }
    

}

extension TodoedTaskDetailViewController:WKNavigationDelegate,WKUIDelegate {
    
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        DDLogDebug("didStartProvisionalNavigation")
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        DDLogDebug("didCommit")
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        DDLogDebug("didFinish")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        DDLogDebug("didFail")
        DDLogError(error.localizedDescription)
    }
    

}


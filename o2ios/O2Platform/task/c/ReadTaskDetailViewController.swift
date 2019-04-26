//
//  ReadTaskDetailViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/3.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import CocoaLumberjack

class ReadTaskDetailViewController: BaseTaskWebViewController {
    
    @IBOutlet weak var progress: UIProgressView!
    
    @IBOutlet weak var toolbarView: UIToolbar!
    
    var loadUrl:String?
    
    var todoTask:TodoTask? {
        didSet {
            var url:String?
            if let workId = todoTask?.work {
                url = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.todoDesktopQuery, parameter: ["##workid##":workId as AnyObject])
            }else if let workCompletedId = todoTask?.workCompleted {
                url = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.todoedDestopQuery, parameter: ["##workCompletedId##":workCompletedId as AnyObject])
            }
            self.loadUrl = url
        }
    }

    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = todoTask?.title
        self.theWebView()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
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
        webView.frame = CGRect(x:0,y:0,width:view.frame.width,height:view.frame.height - 40)
        self.view.insertSubview(webView, belowSubview: toolbarView)
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.load(Alamofire.request(loadUrl!).request!)
        webView.allowsBackForwardNavigationGestures = true
        
    }
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "estimatedPrgress" {
            progress.isHidden = webView.estimatedProgress == 1
            progress.setProgress(Float(webView.estimatedProgress), animated: true)
        }
    }
    
    @IBAction func changeDocAction(_ sender: UIBarButtonItem) {
        DDLogDebug("readButtonAction")
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ReadContext.readContextKey, query: ReadContext.readProcessing, parameter: ["##id##":(todoTask?.id)! as AnyObject])
        self.showMessage(title: "提交中...")
        Alamofire.request(url!, method:.post, parameters: todoTask?.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON {  response in
            switch response.result {
            case .success(let val):
                DDLogDebug(JSON(val).description)
                let json = JSON(val)
                if json["type"]=="success"{
                    self.showSuccess(title: "提交成功")
                    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.3, execute: {
                        self.performSegue(withIdentifier: "backToReadTask", sender: nil)
                    })
                }else {
                    DDLogError(json["message"].description)
                    self.showError(title: "提交失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "提交失败")
            }
            
        }

    }
    
}

extension ReadTaskDetailViewController:WKNavigationDelegate,WKUIDelegate {
    
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




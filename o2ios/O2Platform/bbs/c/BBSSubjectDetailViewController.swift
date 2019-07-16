//
//  BBSSubjectDetailViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack

class BBSSubjectDetailViewController: BaseWebViewUIViewController {
    
    
    @IBOutlet weak var progressView: UIProgressView!
    
    var loadUrl:String?
    
    var window:UIWindow?
    
    var button:UIButton?
    
    var subject:BBSSubjectData? {
        didSet {
            loadUrl = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.bbsItemDetailQuery, parameter: ["##subjectId##":subject?.id as AnyObject])
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.window?.isHidden = false
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.window?.isHidden = true
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        createButton()
        self.theWebView()
        self.view = webView
    }
    
    func createButton(){
        let width = SCREEN_WIDTH
        let height = SCREEN_HEIGHT
        self.button  = UIButton(frame: CGRect(x: 0,y: 0,width: 40,height: 40))
        self.button?.setImage(UIImage(named: "icon_bbs_reply_white"), for: UIControl.State())
        self.button?.addTarget(self, action: #selector(replyAction), for: .touchUpInside)
        self.window = UIWindow(frame: CGRect(x: width - 60, y: height - 60, width: 40, height: 40))
        self.window?.windowLevel = UIWindow.Level.alert + 1
        self.window?.backgroundColor = UIColor.green
        self.window?.layer.cornerRadius = 20
        self.window?.layer.masksToBounds = true
        self.window?.addSubview(self.button!)
        self.window?.makeKeyAndVisible()
    }
    
    func replyAction(sender:Any?){
        self.performSegue(withIdentifier:"showReplyActionSegue", sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showReplyActionSegue" {
            let navVC = segue.destination as! ZLNavigationController
            let destVC = navVC.topViewController as! BBSReplySubjectViewController
            destVC.subject = self.subject
            if let parentId = sender {
                destVC.parentId = parentId as? String
            }
        }
    }
    
    override func theWebView(){
        super.theWebView()
        
        webView.navigationDelegate = self
        
        webView.uiDelegate = self
        
        //self.view.insertSubview(webView, belowSubview: progressView)
        
        webView.allowsBackForwardNavigationGestures = true
        
        //监控进度
        //self.addObserver(webView, forKeyPath: "estimatedProgress", options: .new, context: nil)
        
        loadDetailSubject()
    }
    
    func loadDetailSubject(){
        webView.load(Alamofire.request(loadUrl!).request!)
    }
    
    @IBAction func unFromReplyBackSubject(_ segue:UIStoryboardSegue){
        loadDetailSubject()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "estimatedPrgress" {
            progressView.isHidden = webView.estimatedProgress == 1
            progressView.setProgress(Float(webView.estimatedProgress), animated: true)
        }
    }
    

}



extension BBSSubjectDetailViewController:WKNavigationDelegate,WKUIDelegate {
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        DDLogDebug("didFailProvisionalNavigation \(navigation)  error = \(error)")
    }

    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        DDLogDebug("didStartProvisionalNavigation \(navigation)")
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        DDLogDebug("didCommit")
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        DDLogDebug("didFinish")
        //self.setupData()
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        DDLogDebug("didFail")
        DDLogError(error.localizedDescription)
    }
    
    
}

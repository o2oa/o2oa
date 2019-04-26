//
//  BaseWebViewUIViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/11.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import CocoaLumberjack
import Alamofire
import ObjectMapper
import swiftScan
import JHTAlertController

protocol BaseWebViewUIViewControllerJSDelegate {
    func closeUIViewWindow()
    func actionBarLoaded(show: Bool)
}

open class BaseWebViewUIViewController: UIViewController {
    
    var webView:WKWebView!
    var delegate: BaseWebViewUIViewControllerJSDelegate?

    override open func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override open func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    open func theWebView(){
        setupWebView()
    }
    
    public func setupWebView() {
        let userContentController = WKUserContentController()
        //cookie脚本
        if let cookies = HTTPCookieStorage.shared.cookies {
            let script = getJSCookiesString(cookies: cookies)
            let cookieScript = WKUserScript(source: script, injectionTime: WKUserScriptInjectionTime.atDocumentStart, forMainFrameOnly: false)
            userContentController.addUserScript(cookieScript)
        }
        let webViewConfig = WKWebViewConfiguration()
        webViewConfig.userContentController = userContentController
        //加入js-app message
        // bbs 回复
        userContentController.add(self, name: "ReplyAction")
        
        // protal已经存在ActionBar
         userContentController.add(self, name: "actionBarLoaded")
        
        // 打开工作 {"work":"", "workCompleted":"", "title":""}
        userContentController.add(self, name: "openO2Work")
        // 4个分类 task taskCompleted read readCompleted
        userContentController.add(self, name: "openO2WorkSpace")
        
        // 打开cms appId
        userContentController.add(self, name: "openO2CmsApplication")
        // 打开cms docId docTitle
        userContentController.add(self, name: "openO2CmsDocument")
        // 打开meeting
        userContentController.add(self, name: "openO2Meeting")
        // 打开 calendar
        userContentController.add(self, name: "openO2Calendar")
        // 打开扫一扫
        userContentController.add(self, name: "openScan")
        
        userContentController.add(self, name: "openO2Alert")
        // 打开钉钉
        userContentController.add(self, name: "openDingtalk")
        // 关闭当前UIViewController
        userContentController.add(self, name: "closeNativeWindow")
        

        
        self.webView = WKWebView(frame: self.view.frame, configuration: webViewConfig)
        
        view = webView
        
    }
    
    ///Generates script to create given cookies
    public func getJSCookiesString(cookies: [HTTPCookie]) -> String {
        var result = ""
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = NSTimeZone(abbreviation: "UTC") as TimeZone?
        dateFormatter.dateFormat = "EEE, d MMM yyyy HH:mm:ss zzz"
        
        for cookie in cookies {
            result += "document.cookie='\(cookie.name)=\(cookie.value); domain=\(cookie.domain); path=\(cookie.path); "
            if let date = cookie.expiresDate {
                result += "expires=\(dateFormatter.string(from: date)); "
            }
            if (cookie.isSecure) {
                result += "secure; "
            }
            result += "'; "
        }
        return result
    }
    
    
}

extension BaseWebViewUIViewController: WKScriptMessageHandler {
    
    public func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let name = message.name
        switch name {
        case "ReplyAction":
            DDLogDebug("回复 帖子 message.body = \(message.body)")
            let pId : String?
            if message.body is NSDictionary {
                let parentId:NSDictionary = message.body as! NSDictionary
                pId = parentId["body"] as? String
            }else if message.body is NSString {
                pId = String(message.body as! NSString)
            }else {
                pId = nil
            }
            self.performSegue(withIdentifier: "showReplyActionSegue", sender: pId)
            break
        case "openO2Work":
            DDLogDebug("打开工作界面。。。。。")
            let body = message.body
            if body is NSDictionary {
                let dic = body as! NSDictionary
                let work = dic["work"] as? String
                let workCompleted = dic["workCompleted"] as? String
                let title = dic["title"] as? String
                self.openWork(work: (work ?? ""), workCompleted: (workCompleted ?? ""), title: (title ?? ""))
            }else {
                DDLogError("message body 不是一个字典。。。。。。")
            }
            break
        case "openO2WorkSpace":
            DDLogDebug("打开工作列表。。。。。")
            if message.body is NSString {
                let type = message.body as! NSString
                self.openO2WorkSpace(type: String(type))
            }else {
                DDLogError("打开工作列表失败， type不存在！！！！！")
            }
            break
        case "openO2CmsApplication":
            DDLogDebug("打开cms栏目。。。。。")
            if message.body is NSString {
                let appId = message.body as! NSString
                self.openCmsApplication(appId: String(appId))
            }else if message.body is NSDictionary {
                let appBody = message.body as! NSDictionary
                if let appId = appBody["appId"] {
                    self.openCmsApplication(appId: (appId as! String))
                }
            }else {
                DDLogError("打开cms栏目失败， appId不存在！！！！！")
            }
            break
        case "openO2CmsDocument":
            DDLogDebug("打开cms 文档。。。。。")
            if message.body is NSDictionary {
                let appBody = message.body as! NSDictionary
                let docId = appBody["docId"] as? String
                let docTitle = appBody["docTitle"] as? String
                self.openCmsDocument(docId: (docId ?? "" ), docTitle: (docTitle ?? ""))
            }else {
                DDLogError("打开cms文档失败， 参数不存在！！！！！")
            }
            break
        case "openO2Meeting":
            DDLogDebug("打开会议管理。。。。。")
            self.openO2Meeting()
            break
        case "openO2Calendar":
            DDLogDebug("打开日程管理。。。。。")
            self.openO2Calendar()
            break
        case "openScan":
            self.openScan()
            break
        case "openO2Alert":
            if message.body is NSString {
                let msg = message.body as! NSString
                self.openO2Alert(message: String(msg))
            }
            break
        case "closeNativeWindow":
            DDLogDebug("关闭窗口！！！！")
            self.delegate?.closeUIViewWindow()
            break
        case "openDingtalk":
            self.openDingtalk()
            break
        case "actionBarLoaded":
            self.delegate?.actionBarLoaded(show: true)
            break
        default:
            break
        }
    }
    
    private func openO2Alert(message: String) {
        DDLogDebug("O2 alert msg:\(message)")
        let alertController = JHTAlertController(title: "", message: message, preferredStyle: .alert)
        alertController.titleImage = #imageLiteral(resourceName: "logo80-bai")
        alertController.messageTextColor = UIColor(hex: "#030303")
        alertController.titleViewBackgroundColor = UIColor.hexInt(0xFB4747)
        alertController.alertBackgroundColor =  UIColor(hexString: "#FCFCFC", alpha: 0.9)!
        alertController.setAllButtonBackgroundColors(to: UIColor(hexString: "#FCFCFC", alpha: 0.9)!)
        alertController.setButtonTextColorFor(.default, to: UIColor(hex: "#FB4747"))
        alertController.setButtonTextColorFor(.cancel, to: UIColor(hex: "#FB4747"))
        alertController.hasRoundedCorners = true
        let okAction = JHTAlertAction(title: "确定", style: .default, handler: {action in
            
        })
        alertController.addActions([okAction])
        present(alertController, animated: true, completion: nil)
    }
    
    
    private func openWork(work: String, workCompleted: String, title: String) {
        let storyBoard = UIStoryboard(name: "task", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
        let json = """
        {"work":"\(work)", "workCompleted":"\(workCompleted)", "title":"\(title)"}
        """
        DDLogDebug("openWork json: \(json)")
        let todo = TodoTask(JSONString: json)
        destVC.todoTask = todo
        destVC.backFlag = 3 //隐藏就行
        self.show(destVC, sender: nil)
    }
    
    // task taskCompleted read readCompleted
    private func openO2WorkSpace(type: String) {
        let storyBoard = UIStoryboard(name: "task", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "todoTask")
        let nsType = NSString(string: type).lowercased
        DDLogDebug("打开工作区， type：\(nsType)")
        if "taskcompleted" == nsType {
            AppConfigSettings.shared.taskIndex = 2
        }else if "read" == nsType {
            AppConfigSettings.shared.taskIndex = 1
        }else if "readcompleted" == nsType {
            AppConfigSettings.shared.taskIndex = 3
        }else {
            AppConfigSettings.shared.taskIndex = 0
        }
        self.show(destVC, sender: nil)
    }
    
    private func openCmsApplication(appId: String) {
        DDLogInfo("打开栏目， appId：\(appId)")
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsCategoryListQuery, parameter: ["##appId##": appId as AnyObject])
        self.showMessage(title: "Loading...")
        Alamofire.request(url!, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let categroyList = Mapper<CMSCategoryData>().map(JSONObject: val)
                if let count = categroyList?.data?.count {
                    if count > 0 {
                        let storyBoard = UIStoryboard(name: "information", bundle: nil)
                        let destVC = storyBoard.instantiateViewController(withIdentifier: "CMSCategoryListController") as! CMSCategoryListViewController
                        destVC.title = categroyList?.data?.first?.appName ?? ""
                        let d = CMSData(JSONString: "{\"id\":\"\"}")
                        d?.wrapOutCategoryList = categroyList?.data
                        destVC.cmsData = d
                        self.show(destVC, sender: nil)
                    }
                }
                self.dismissProgressHUD()
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.dismissProgressHUD()
            }
        }
        
    }
    
    private func openCmsDocument(docId: String, docTitle: String) {
        DDLogInfo("打开文档， docId：\(docId) , docTitle:\(docTitle)")
        let storyBoard = UIStoryboard(name: "information", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "CMSSubjectDetailVC") as! CMSItemDetailViewController
        let json = """
        {"title":"\(docTitle)", "id":"\(docId)"}
       """
        destVC.itemData =  CMSCategoryItemData(JSONString: json)
        self.show(destVC, sender: nil)
    }
    
    private func openO2Meeting() {
        let storyBoard = UIStoryboard(name: "meeting", bundle: nil)
        if let destVC = storyBoard.instantiateInitialViewController() {
            self.show(destVC, sender: nil)
        }else {
            DDLogError("会议 模块打开失败，没有找到vc")
        }
    }
    
    private func openO2Calendar() {
        let storyBoard = UIStoryboard(name: "calendar", bundle: nil)
        if let destVC = storyBoard.instantiateInitialViewController() {
            self.show(destVC, sender: nil)
        }else {
            DDLogError("calendar 模块打开失败，没有找到vc")
        }
    }
    
    private func openScan() {
        if let scanVC = ScanHelper.initScanViewController() {
            self.pushVC(scanVC)
        }else {
            gotoApplicationSettings(alertMessage: "是否跳转到手机设置页面开启相机权限？")
        }
    }
    
    private func openDingtalk() {
        UIApplication.shared.open(URL(string: "dingtalk://dingtalkclient/")!, options: [:]) { (result) in
            DDLogInfo("打开了钉钉。。。。\(result)")
        }
    }
}

//
//  O2JsApiNotification.swift
//  O2Platform
//
//  Created by FancyLou on 2019/4/22.
//  Copyright © 2019 zoneland. All rights reserved.
//


import UIKit
import WebKit
import CocoaLumberjack

/**
 * o2m.notification
 **/
class O2JsApiNotification: NSObject, WKScriptMessageHandler {
    
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.body is NSString {
            let json = message.body as! NSString
            DDLogDebug("message json:\(json)")
            if let jsonData = String(json).data(using: .utf8) {
                let dicArr = try! JSONSerialization.jsonObject(with: jsonData, options: .allowFragments) as! [String:AnyObject]
                let type = dicArr["type"] as! String
                switch type {
                case "alert":
                    alert(json: String(json))
                    break
                default:
                    break
                }
            }else {
                DDLogError("消息json解析异常。。。")
            }
        }else {
            DDLogError("message 消息 body 类型不正确。。。")
        }
    }
    
    private func alert(json: String) {
//        if let alert = O2NotificationMessage<O2NotificationAlertMessage>.deserialize(from: json) {
//            var buttonName = alert.data?.buttonName ?? ""
//            if buttonName == "" {
//                buttonName = "确定"
//            }
//            let title = alert.data?.title ?? ""
//            let message = alert.data?.message ?? "消息"
//            self.showSystemAlertWithButtonName(title: title, message: message , buttonName: buttonName) { (action) in
//                if alert.callback != nil {
//                    let callJs = "\(alert.callback!)()"
//                    DDLogDebug(callJs)
//                    self.webView.evaluateJavaScript(callJs, completionHandler: { (result, err) in
//
//                    })
//                }
//            }
//        }else {
//            DDLogError("解析json失败")
//            self.showError(title: "参数不正确！")
//        }
    }
}

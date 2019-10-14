//
//  O2JsApiNotification.swift
//  O2Platform
//
//  Created by FancyLou on 2019/4/22.
//  Copyright © 2019 zoneland. All rights reserved.
//


import UIKit
import WebKit
import AudioToolbox
import CocoaLumberjack

/**
 * o2m.notification
 **/
class O2JsApiNotification: O2WKScriptMessageHandlerImplement {
    
    let viewController: BaseWebViewUIViewController
    
    init(viewController: BaseWebViewUIViewController) {
        self.viewController = viewController
    }
    
    func userController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.body is NSString {
            let json = message.body as! NSString
            DDLogDebug("message json:\(json)")
            if let jsonData = String(json).data(using: .utf8) {
                let dicArr = try! JSONSerialization.jsonObject(with: jsonData, options: .allowFragments) as! [String:AnyObject]
                if let type = dicArr["type"] as? String {
                    switch type {
                    case "alert":
                        alert(json: String(json))
                        break
                    case "confirm":
                        confirm(json: String(json))
                        break
                    case "prompt":
                        prompt(json: String(json))
                        break
                    case "vibrate":
                        vibrate(json: String(json))
                        break
                    case "toast":
                        toast(json: String(json))
                        break
                    case "actionSheet":
                        actionSheet(json: String(json))
                        break
                    case "showLoading":
                        showLoading(json: String(json))
                        break
                    case "hideLoading":
                        hideLoading(json: String(json))
                        break
                    default:
                        DDLogError("notification类型不正确, type: \(type)")
                    }
                }else {
                    DDLogError("notification类型 json解析异常。。。。。")
                }
            }else {
                DDLogError("消息json解析异常。。。")
            }
        }else {
            DDLogError("message 消息 body 类型不正确。。。")
        }
    }
    
    //o2m.notification.alert
    private func alert(json: String) {
            DDLogDebug("alert:\(json)")
            if let alert = O2NotificationMessage<O2NotificationAlertMessage>.deserialize(from: json) {
                var buttonName = alert.data?.buttonName ?? ""
                if buttonName == "" {
                    buttonName = "确定"
                }
                let title = alert.data?.title ?? ""
                let message = alert.data?.message ?? "消息"
                self.viewController.showSystemAlertWithButtonName(title: title, message: message , buttonName: buttonName) { (action) in
                    if alert.callback != nil {
                        let callJs = "\(alert.callback!)()"
                        self.evaluateJs(callBackJs: callJs)
                    }
                }
            }else {
                DDLogError("alert, 解析json失败")
            }
    }
    
    //o2m.notification.confirm
    private func confirm(json: String) {
        DDLogDebug("confirm:\(json)")
        if let alert = O2NotificationMessage<O2NotificationConfirm>.deserialize(from: json) {
            let title = alert.data?.title ?? ""
            let message = alert.data?.message ?? ""
            let buttons = alert.data?.buttonLabels ?? ["确定", "取消"]
            if buttons.count != 2 {
                self.viewController.showError(title: "确认框按钮个数不正确！")
                return
            }
            let okAction = UIAlertAction(title: buttons[0], style: .default) { (ok) in
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)(0)"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            let cancelAction = UIAlertAction(title: buttons[1], style: .cancel) { (cancel) in
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)(1)"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            self.viewController.showDefaultConfirm(title: title, message: message, okAction: okAction, cancelAction: cancelAction)
        }else {
            DDLogError("confirm , 解析json失败")
        }
    }
    
    //o2m.notification.prompt
    private func prompt(json: String) {
        DDLogDebug("prompt:\(json)")
        if let alert = O2NotificationMessage<O2NotificationConfirm>.deserialize(from: json) {
            let title = alert.data?.title ?? ""
            let message = alert.data?.message ?? ""
            let buttons = alert.data?.buttonLabels ?? ["确定", "取消"]
            if buttons.count != 2 {
                self.viewController.showError(title: "回复框按钮个数不正确！")
                return
            }
            
            let promptController = UIAlertController(title: title, message: message, preferredStyle: .alert)
            promptController.addTextField { (textField) in
                textField.placeholder = "测试"
            }
            let okAction = UIAlertAction(title: buttons[0], style: .default) { (ok) in
                if alert.callback != nil {
                    let value = promptController.textFields?.first?.text ?? ""
                    let json = "{buttonIndex: 0, value: \"\(value)\"}"
                    let callJs = "\(alert.callback!)('\(json)')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            let cancelAction = UIAlertAction(title: buttons[1], style: .cancel) { (cancel) in
                if alert.callback != nil {
                    let value = promptController.textFields?[0].text ?? ""
                    let json = "{buttonIndex: 1, value: \"\(value)\"}"
                    let callJs = "\(alert.callback!)('\(json)')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            promptController.addAction(okAction)
            promptController.addAction(cancelAction)
            self.viewController.present(promptController, animated: true, completion: nil)
        }else {
            DDLogError("prompt , 解析json失败")
        }
    }
    
    //o2m.notification.vibrate
    private func vibrate(json: String) {
        DDLogDebug("vibrate:\(json)")
        if let alert = O2NotificationMessage<O2NotificationToast>.deserialize(from: json) {
            //这个代码好像对机器有要求 6s以上的手机才行？
//            if #available(iOS 10.0, *) {
//                DDLogDebug("vibrate after iOS 10.0")
//                UIImpactFeedbackGenerator.init(style: .medium).impactOccurred()
//            }else {
                DDLogDebug("vibrate before iOS 10.0")
                let soundID = SystemSoundID(kSystemSoundID_Vibrate)
                AudioServicesPlaySystemSound(soundID)
//            }
            if alert.callback != nil {
                let callJs = "\(alert.callback!)()"
                self.evaluateJs(callBackJs: callJs)
            }
        }else {
            DDLogError("vibrate , 解析json失败")
        }
    }
    
    //o2m.notification.toast
    private func toast(json: String) {
        DDLogDebug("toast:\(json)")
        if let alert = O2NotificationMessage<O2NotificationToast>.deserialize(from: json) {
            let message = alert.data?.message ?? ""
            self.viewController.showSuccess(title: message)
            if alert.callback != nil {
                let callJs = "\(alert.callback!)()"
                self.evaluateJs(callBackJs: callJs)
            }
        }else {
            DDLogError("toast , 解析json失败")
        }
    }
    
    //o2m.notification.actionSheet
    private func actionSheet(json: String) {
        DDLogDebug("actionSheet:\(json)")
        if let alert = O2NotificationMessage<O2NotificationActionSheet>.deserialize(from: json) {
            let title = alert.data?.title ?? ""
            let cancelButton = alert.data?.cancelButton ?? "取消"
            let otherButtons = alert.data?.otherButtons ?? []
            if otherButtons.count < 1 {
                self.viewController.showError(title: "列表按钮个数必须大于0！")
                return
            }
            var actions : [UIAlertAction] = []
            for  (index, text) in otherButtons.enumerated() {
                let okAction = UIAlertAction(title: text, style: .default) { (ok) in
                    if alert.callback != nil {
                        let callJs = "\(alert.callback!)(\(index))"
                        self.evaluateJs(callBackJs: callJs)
                    }
                }
                actions.append(okAction)
            }
            let cancelAction = UIAlertAction(title: cancelButton, style: .cancel) { (cancel) in
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)(-1)"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            actions.append(cancelAction)
            self.viewController.showActionSheetIncludeCancelBtn(title: "", message: title, actions: actions)
        }else {
            DDLogError("actionSheet , 解析json失败")
        }
    }
    
    //o2m.notification.showLoading
    private func showLoading(json: String) {
        DDLogDebug("showLoading:\(json)")
        if let alert = O2NotificationMessage<O2NotificationLoading>.deserialize(from: json) {
            let text = alert.data?.text ?? "Loading..."
            self.viewController.showLoading(title: text)
            if alert.callback != nil {
                let callJs = "\(alert.callback!)()"
                self.evaluateJs(callBackJs: callJs)
            }
        }else {
            DDLogError("toast , 解析json失败")
        }
    }
    //o2m.notification.hideLoading
    private func hideLoading(json: String) {
        DDLogDebug("hideLoading:\(json)")
        self.viewController.hideLoading()
        if let alert = O2NotificationMessage<O2NotificationLoading>.deserialize(from: json) {
            if alert.callback != nil {
                let callJs = "\(alert.callback!)()"
                self.evaluateJs(callBackJs: callJs)
            }
        }else {
            DDLogError("toast , 解析json失败")
        }
    }
    
    
    private func evaluateJs(callBackJs: String) {
        DDLogDebug("执行回调js："+callBackJs)
        self.viewController.webView.evaluateJavaScript(callBackJs, completionHandler: { (result, err) in
            DDLogDebug("回调js执行完成！")
        })
    }
}

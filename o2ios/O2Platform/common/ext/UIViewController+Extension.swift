//
//  UIViewController+Extension.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/9.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import JHTAlertController
import ProgressHUDSwift
import Whisper


extension UIViewController {
    
    func setNavRightBarItemsTextDefault() {
        if let rightItems = self.navigationItem.rightBarButtonItems {
            for item in rightItems {
                item.setTitleTextAttributes([NSAttributedString.Key.font : UIFont(name: "PingFangTC-Regular", size: 14)!], for: .normal)
            }
        }
    }
    
    func setNavLeftBarItemsTextDefault() {
        if let leftItems = self.navigationItem.leftBarButtonItems {
            for item in leftItems {
                item.setTitleTextAttributes([NSAttributedString.Key.font : UIFont(name: "PingFangTC-Regular", size: 14)!], for: .normal)
            }
        }
    }
    
    func forwardDestVC(_ storyBoardName:String,_ destVCIdentitifer:String?){
        let window = UIApplication.shared.keyWindow
        let storyBoard:UIStoryboard = UIStoryboard.init(name: storyBoardName, bundle: nil)
        var destVC:UIViewController!
        if destVCIdentitifer != nil {
            destVC = storyBoard.instantiateViewController(withIdentifier: destVCIdentitifer!)
        }else{
            destVC = storyBoard.instantiateInitialViewController()
        }
        window?.rootViewController = destVC
        window?.makeKeyAndVisible()
    }
    
    func forwardDestVC(_ targetVC:UIViewController){
        let window = UIApplication.shared.keyWindow
        window?.rootViewController = targetVC
        window?.makeKeyAndVisible()
    }
    
    
    func getDestVC<T>(vcType:T.Type,storyBoardName:String,identitiferController:String?) -> T {
        let storyBoard = UIStoryboard(name: storyBoardName, bundle: nil)
        var destVC:T
        if  let identitifer  = identitiferController {
            destVC = storyBoard.instantiateViewController(withIdentifier: identitifer) as! T
        }else{
            destVC = storyBoard.instantiateInitialViewController() as! T
        }
        return destVC
    }
}

// MARK:- AlertController
extension UIViewController {
    
    
    
    /// 系统弹出框确认
    ///
    /// - Parameters:
    ///   - title: 标题
    ///   - message: 提示消息
    ///   - okHandler: 确定行为
    func showDefaultConfirm(title: String, message: String, okHandler: @escaping ((UIAlertAction) -> Void)) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "确定", style: .default, handler: okHandler)
        let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
        alertController.addAction(okAction)
        alertController.addAction(cancelAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    /// 系统弹出框提示
    ///
    /// - Parameters:
    ///   - title: 标题
    ///   - message: 提示消息
    ///   - okHandler: 确定行为
    func showSystemAlert(title: String, message: String, okHandler: @escaping ((UIAlertAction) -> Void)) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "确定", style: .default, handler: okHandler)
        alertController.addAction(okAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func showSystemAlertWithButtonName(title: String, message: String, buttonName: String, okHandler: @escaping ((UIAlertAction) -> Void)) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: buttonName, style: .default, handler: okHandler)
        alertController.addAction(okAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    // actionSheet 形式的弹出提示框  可以传入多个Action 已经有取消Action了
    func showSheetAction(title: String?, message: String?, actions: [UIAlertAction]) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .actionSheet)
        actions.forEach { (action) in
            alertController.addAction(action)
        }
        let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
        alertController.addAction(cancelAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func showAlert(title:String,message:String,okHandler:((JHTAlertAction) -> Void)!,cancelHandler:((JHTAlertAction) -> Void)!){
        let alertController = JHTAlertController(title: "", message: message, preferredStyle: .alert)
        //alertController.view.backgroundColor = UIColor(hexString: "#FCFCFC", alpha: 0.9)!
        //alertController.alertMessageViewBackgroundColor = UIColor(hexString: "#FCFCFC", alpha: 0.9)!
        alertController.titleImage = #imageLiteral(resourceName: "logo80-bai")
        alertController.messageTextColor = UIColor(hex: "#030303")
        alertController.titleViewBackgroundColor = UIColor.hexInt(0xFB4747)
        alertController.alertBackgroundColor =  UIColor(hexString: "#FCFCFC", alpha: 0.9)!
        alertController.setAllButtonBackgroundColors(to: UIColor(hexString: "#FCFCFC", alpha: 0.9)!)
        alertController.setButtonTextColorFor(.default, to: UIColor(hex: "#FB4747"))
        alertController.setButtonTextColorFor(.cancel, to: UIColor(hex: "#FB4747"))
        alertController.hasRoundedCorners = true
        let okAction = JHTAlertAction(title: "确定", style: .default, handler: okHandler)
        let cancelAction = JHTAlertAction(title: "取消", style: .cancel, handler: cancelHandler)
        alertController.addActions([cancelAction,okAction])
        present(alertController, animated: true, completion: nil)
    }
    
    
    func datePickerTapped(_ title:String,_ dateType:UIDatePicker.Mode,_ format:String,callBackResult:((_ result:Date) -> Void)?) {
        let locale = Locale(identifier: "zh")
        let theDate = Date()
        var dateComponents = DateComponents()
        dateComponents.month = -12
        let threeMonthAgo = Calendar.current.date(byAdding: dateComponents, to: theDate)
        dateComponents.month = 12
        let nextYearMonthAgo = Calendar.current.date(byAdding: dateComponents, to: theDate)
        
        let datePicker = LWDatePickerDialog(textColor: .red,
                                            buttonColor: .red,
                                            font: UIFont.boldSystemFont(ofSize: 17),
                                            locale:locale ,
                                            showCancelButton: true)
        datePicker.show(title,
                        doneButtonTitle: "确定",
                        cancelButtonTitle: "取消",
                        defaultDate: theDate,
                        minimumDate: threeMonthAgo,
                        maximumDate: nextYearMonthAgo,
                        datePickerMode: dateType) { (date) in
                            if let dt = date {
                                let formatter = DateFormatter()
                                formatter.dateFormat = format
                                let _ = formatter.string(from: dt)
                                if let block = callBackResult {
                                    block(dt)
                                }
                            }
        }
    }
    
}

// MARK:- ProgressHUD
extension UIViewController {
    
    func showSuccess(title:String) {
        DispatchQueue.main.async {
            ProgressSHD.showSuccess(title)
        }
    }
    
    func showError(title:String){
        DispatchQueue.main.async {
            ProgressSHD.showError(title)
        }
    }
    
    func showMessage(title:String){
        DispatchQueue.main.async {
            ProgressSHD.show(title)
        }
    }
    
    func dismissProgressHUD() {
        DispatchQueue.main.async {
            ProgressSHD.dismiss()
        }
    }
}

// MARK:- 加动画退出app
extension UIViewController {
    func exitAPP() {
        let appDelegate = UIApplication.shared.delegate!
        let window = appDelegate.window!
        UIView.animate(withDuration: 0.4, animations: {
            UIView.animate(withDuration: 0.4) {
                window?.alpha = 0
                let y = window?.bounds.size.height
                let x = (window?.bounds.size.width)! / 2
                window?.frame = CGRect(x: x, y: y!, width: 0, height: 0)
            }
        }) { (completed) in
            exit(0)
        }
    }
}

// MARK:- Whisper
extension UIViewController {
    func showWhisperMessage(title:String,message:String) {
        if let nav = self.navigationController {
            let msg = Message(title: title, backgroundColor: .red)
            // Show and hide a message after delay
            Whisper.show(whisper: msg, to: nav, action: .show)
            // Present a permanent message
            Whisper.show(whisper: msg, to: nav, action: .present)
            // Hide a message
            Whisper.hide(whisperFrom: nav, after: 3)
        }else{
            let murmur = Murmur(title:title)
            // Present a permanent status bar message
            Whisper.show(whistle: murmur, action: .show(3))
        }
    }
}

//MARK: - 跳转到设置页面
extension UIViewController {
    
    
    ///跳转到应用设置页面
    ///
    /// - Parameter alertMessage: 确认提示消息，如果是nil就直接跳转
    func gotoApplicationSettings(alertMessage:String? = nil) {
        if alertMessage != nil {
            showDefaultConfirm(title: "提示", message: alertMessage!, okHandler: { (okAction) in
                UIApplication.shared.openURL(URL(string: UIApplication.openSettingsURLString)!)
            })
        }else {
            UIApplication.shared.openURL(URL(string: UIApplication.openSettingsURLString)!)
        }
    }
}

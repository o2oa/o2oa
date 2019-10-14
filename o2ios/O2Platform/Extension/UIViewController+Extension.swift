//
//  UIViewController+Extension.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/9.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import Chrysan


extension UIViewController {
    
    
    ///EZSE: Pushes a view controller onto the receiver’s stack and updates the display.
    open func pushVC(_ vc: UIViewController) {
        navigationController?.pushViewController(vc, animated: true)
    }
    
    ///EZSE: Pops the top view controller from the navigation stack and updates the display.
    open func popVC() {
        _ = navigationController?.popViewController(animated: true)
    }
    
    /// EZSE: Added extension for popToRootViewController
    open func popToRootVC() {
        _ = navigationController?.popToRootViewController(animated: true)
    }
    
    ///EZSE: Presents a view controller modally.
    open func presentVC(_ vc: UIViewController) {
        present(vc, animated: true, completion: nil)
    }
    
    ///EZSE: Dismisses the view controller that was presented modally by the view controller.
    open func dismissVC(completion: (() -> Void)? ) {
        dismiss(animated: true, completion: completion)
    }
    
    
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
    
    func showDefaultConfirm(title: String, message: String, okAction: UIAlertAction, cancelAction: UIAlertAction) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
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
    //actionSheet 传入的actions需要包含取消Action
    func showActionSheetIncludeCancelBtn(title: String?, message: String?, actions: [UIAlertAction]) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .actionSheet)
        actions.forEach { (action) in
            alertController.addAction(action)
        }
        self.present(alertController, animated: true, completion: nil)
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
             self.chrysan.show(.succeed, message: title, hideDelay: 1)
        }
    }
    
    func showError(title:String){
        DispatchQueue.main.async {
            self.chrysan.show(.error, message: title, hideDelay: 1)
        }
    }
    
    func showLoading(title:String){
        DispatchQueue.main.async {
            self.chrysan.show(.running, message: title)
        }
    }
    
    func showLoading()  {
        DispatchQueue.main.async {
            self.chrysan.show()
        }
    }
    
    func hideLoading() {
        DispatchQueue.main.async {
            self.chrysan.hide()
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


//MARK: - 
extension UIViewController {
    
    ///EZSE: Adds an NotificationCenter with name and Selector
    open func addNotificationObserver(_ name: String, selector: Selector) {
        NotificationCenter.default.addObserver(self, selector: selector, name: NSNotification.Name(rawValue: name), object: nil)
    }
    
    ///EZSE: Removes an NSNotificationCenter for name
    open func removeNotificationObserver(_ name: String) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: name), object: nil)
    }
    
    ///EZSE: Removes NotificationCenter'd observer
    open func removeNotificationObserver() {
        NotificationCenter.default.removeObserver(self)
    }
    
    ///EZSE: Adds a NotificationCenter Observer for keyboardWillShowNotification()
    ///
    /// ⚠️ You also need to implement ```keyboardWillShowNotification(_ notification: Notification)```
    open func addKeyboardWillShowNotification() {
        
        self.addNotificationObserver(UIResponder.keyboardWillShowNotification.rawValue, selector: #selector(UIViewController.keyboardWillShowNotification(_:)))
    }
    
    ///EZSE:  Adds a NotificationCenter Observer for keyboardDidShowNotification()
    ///
    /// ⚠️ You also need to implement ```keyboardDidShowNotification(_ notification: Notification)```
    public func addKeyboardDidShowNotification() {
        self.addNotificationObserver(UIResponder.keyboardDidShowNotification.rawValue, selector: #selector(UIViewController.keyboardDidShowNotification(_:)))
    }
    
    ///EZSE:  Adds a NotificationCenter Observer for keyboardWillHideNotification()
    ///
    /// ⚠️ You also need to implement ```keyboardWillHideNotification(_ notification: Notification)```
    open func addKeyboardWillHideNotification() {
        self.addNotificationObserver(UIResponder.keyboardWillHideNotification.rawValue, selector: #selector(UIViewController.keyboardWillHideNotification(_:)))
    }
    
    ///EZSE:  Adds a NotificationCenter Observer for keyboardDidHideNotification()
    ///
    /// ⚠️ You also need to implement ```keyboardDidHideNotification(_ notification: Notification)```
    open func addKeyboardDidHideNotification() {
        self.addNotificationObserver(UIResponder.keyboardDidHideNotification.rawValue, selector: #selector(UIViewController.keyboardDidHideNotification(_:)))
    }
    
    ///EZSE: Removes keyboardWillShowNotification()'s NotificationCenter Observer
    open func removeKeyboardWillShowNotification() {
        self.removeNotificationObserver(UIResponder.keyboardWillShowNotification.rawValue)
    }
    
    ///EZSE: Removes keyboardDidShowNotification()'s NotificationCenter Observer
    open func removeKeyboardDidShowNotification() {
        self.removeNotificationObserver(UIResponder.keyboardDidShowNotification.rawValue)
    }
    
    ///EZSE: Removes keyboardWillHideNotification()'s NotificationCenter Observer
    open func removeKeyboardWillHideNotification() {
        self.removeNotificationObserver(UIResponder.keyboardWillHideNotification.rawValue)
    }
    
    ///EZSE: Removes keyboardDidHideNotification()'s NotificationCenter Observer
    open func removeKeyboardDidHideNotification() {
        self.removeNotificationObserver(UIResponder.keyboardDidHideNotification.rawValue)
    }
    
    @objc open func keyboardDidShowNotification(_ notification: Notification) {
        if let nInfo = (notification as NSNotification).userInfo, let value = nInfo[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            
            let frame = value.cgRectValue
            keyboardDidShowWithFrame(frame)
        }
    }
    
    @objc open func keyboardWillShowNotification(_ notification: Notification) {
        if let nInfo = (notification as NSNotification).userInfo, let value = nInfo[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            
            let frame = value.cgRectValue
            keyboardWillShowWithFrame(frame)
        }
    }
    
    @objc open func keyboardWillHideNotification(_ notification: Notification) {
        if let nInfo = (notification as NSNotification).userInfo, let value = nInfo[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            
            let frame = value.cgRectValue
            keyboardWillHideWithFrame(frame)
        }
    }
    
    @objc open func keyboardDidHideNotification(_ notification: Notification) {
        if let nInfo = (notification as NSNotification).userInfo, let value = nInfo[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            
            let frame = value.cgRectValue
            keyboardDidHideWithFrame(frame)
        }
    }
    
    @objc open func keyboardWillShowWithFrame(_ frame: CGRect) {
        
    }
    
    @objc open func keyboardDidShowWithFrame(_ frame: CGRect) {
        
    }
    
    @objc open func keyboardWillHideWithFrame(_ frame: CGRect) {
        
    }
    
    @objc open func keyboardDidHideWithFrame(_ frame: CGRect) {
        
    }
    //EZSE: Makes the UIViewController register tap events and hides keyboard when clicked somewhere in the ViewController.
    open func hideKeyboardWhenTappedAround(cancelTouches: Bool = false) {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = cancelTouches
        view.addGestureRecognizer(tap)
    }


    //EZSE: Dismisses keyboard
    @objc open func dismissKeyboard() {
        view.endEditing(true)
    }
    
    ///跳转到应用设置页面
    ///
    /// - Parameter alertMessage: 确认提示消息，如果是nil就直接跳转
    func gotoApplicationSettings(alertMessage:String? = nil) {
        if alertMessage != nil {
            showDefaultConfirm(title: "提示", message: alertMessage!, okHandler: { (okAction) in
                UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!, options: [:], completionHandler: nil)
            })
        }else {
            UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!, options: [:], completionHandler: nil)
        }
    }
}

//
//  JCAddFriendViewController.swift
//  JChat
//
//  Created by deng on 2017/4/27.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCAddFriendViewController: UIViewController {
    
    var user: JMSGUser?

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    private lazy var navRightButton: UIBarButtonItem = UIBarButtonItem(title: "发送", style: .plain, target: self, action: #selector(_addFriend))
    fileprivate lazy var textField: UITextField = UITextField(frame: CGRect(x: 0, y: 64, width: self.view.width, height: 45))

    //MARK: - private func
    private func _init() {
        self.title = "验证信息"
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        
        textField.text = "我是"
        textField.backgroundColor = .white
        textField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 0))
        textField.leftViewMode = .always
        view.addSubview(textField)
        _setupNavigation()
    }
    
    private func _setupNavigation() {
        navigationItem.rightBarButtonItem =  navRightButton
    }
    
    //MARK: - click func
    func _addFriend() {
        guard let user = user else {
            return
        }
        JMSGFriendManager.sendInvitationRequest(withUsername: user.username, appKey: user.appKey, reason: textField.text) { (result, error) in
            if error == nil {
                let info = JCVerificationInfo.create(username: user.username, nickname: user.nickname, appkey: user.appKey!, resaon: self.textField.text, state: JCVerificationType.wait.rawValue)
                JCVerificationInfoDB.shareInstance.insertData(info)
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateVerification), object: nil)
                MBProgressHUD_JChat.show(text: "好友请求已发送", view: self.view, 2)
                weak var weakSelf = self
                let time: TimeInterval = 2
                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + time) {
                    weakSelf?.navigationController?.popViewController(animated: true)
                }
            } else {
                MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
            }
        }
    }

}

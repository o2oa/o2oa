//
//  JCGroupNameViewController.swift
//  JChat
//
//  Created by deng on 2017/5/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCGroupNameViewController: UIViewController {

    var group: JMSGGroup!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
        groupName = group.displayName()
        groupNameTextField.text = groupName
        var count = 20 - groupName.count
        count = count < 0 ? 0 : count
        tipLabel.text = "\(count)"
        groupNameTextField.becomeFirstResponder()
    }

    private var topOffset: CGFloat {
        if isIPhoneX {
            return 88
        }
        return 64
    }
    private lazy var navRightButton: UIBarButtonItem = UIBarButtonItem(title: "完成", style: .plain, target: self, action: #selector(_saveNickname))
    fileprivate lazy var groupNameTextField: UITextField = UITextField(frame: CGRect(x: 0, y: self.topOffset, width: self.view.width, height: 45))
    fileprivate lazy var tipLabel:  UILabel = UILabel(frame: CGRect(x: self.view.width - 15 - 50, y: self.topOffset + 21, width: 28, height: 12))
    
    private var groupName = ""
    
    //MARK: - private func
    private func _init() {
        self.title = "群组名称"
        automaticallyAdjustsScrollViewInsets = false
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        
        groupNameTextField.backgroundColor = .white
        groupNameTextField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 0))
        groupNameTextField.rightView = UIView(frame: CGRect(x: 0, y: 0, width: 27, height: 0))
        groupNameTextField.leftViewMode = .always
        groupNameTextField.rightViewMode = .always
        groupNameTextField.addTarget(self, action: #selector(textFieldDidChanged(_ :)), for: .editingChanged)
        view.addSubview(groupNameTextField)
        
        tipLabel.textColor = UIColor(netHex: 0x999999)
        tipLabel.font = UIFont.systemFont(ofSize: 12)
        tipLabel.textAlignment = .right
        view.addSubview(tipLabel)
        _setupNavigation()
    }
    
    private func _setupNavigation() {
        navigationItem.rightBarButtonItem =  navRightButton
    }

    @objc func textFieldDidChanged(_ textField: UITextField) {
        if textField.markedTextRange == nil {
            let text = textField.text!
            if text.count > 20 {
                let range = (text.startIndex ..< text.index(text.startIndex, offsetBy: 20))
                
                let subText = text.substring(with: range)
                textField.text = subText
            }
            let count = 20 - (textField.text?.count)!
            tipLabel.text = "\(count)"
        }
    }
    
    //MARK: - click func
    @objc func _saveNickname() {
        groupNameTextField.resignFirstResponder()
        let groupName = groupNameTextField.text
        MBProgressHUD_JChat.showMessage(message: "修改中...", toView: view)
        var desc = group.desc
        if group.desc != nil && group.desc!.isEmpty {
            desc = nil
        }
        JMSGGroup.updateGroupInfo(withGroupId: group.gid, name: groupName!, desc: desc) { (result, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateGroupInfo), object: nil)
                self.navigationController?.popViewController(animated: true)
            } else {
                MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
            }
        }
    }
}

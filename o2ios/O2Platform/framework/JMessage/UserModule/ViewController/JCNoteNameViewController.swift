//
//  JCNoteNameViewController.swift
//  JChat
//
//  Created by deng on 2017/5/15.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCNoteNameViewController: UIViewController {
    
    var user: JMSGUser!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
        noteName = user.noteName ?? ""
        noteNameTextField.text = noteName
        let count = 20 - noteName.count
        tipLabel.text = "\(count)"
        noteNameTextField.becomeFirstResponder()
    }

    private var topOffset: CGFloat {
        if isIPhoneX {
            return 88
        }
        return 64
    }
    private lazy var navRightButton: UIBarButtonItem = UIBarButtonItem(title: "完成", style: .plain, target: self, action: #selector(_saveNickname))
    fileprivate lazy var noteNameTextField: UITextField = {
        let noteNameTextField = UITextField()
        noteNameTextField.backgroundColor = .white
        noteNameTextField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 0))
        noteNameTextField.rightView = UIView(frame: CGRect(x: 0, y: 0, width: 27, height: 0))
        noteNameTextField.leftViewMode = .always
        noteNameTextField.rightViewMode = .always
        noteNameTextField.addTarget(self, action: #selector(textFieldDidChanged(_ :)), for: .editingChanged)
        return noteNameTextField
    }()
    fileprivate lazy var tipLabel:  UILabel = {
        let tipLabel = UILabel(frame: CGRect(x: self.view.width - 15 - 28, y: self.topOffset + 21, width: 28, height: 12))
        tipLabel.textColor = UIColor(netHex: 0x999999)
        tipLabel.font = UIFont.systemFont(ofSize: 12)
        tipLabel.textAlignment = .right
        return tipLabel
    }()
    
    private var noteName = ""
    
    //MARK: - private func
    private func _init() {
        self.title = "备注名"
        automaticallyAdjustsScrollViewInsets = false
        view.backgroundColor = UIColor(netHex: 0xe8edf3)

        _setupNavigation()

        view.addSubview(noteNameTextField)
        view.addSubview(tipLabel)
        
        view.addConstraint(_JCLayoutConstraintMake(noteNameTextField, .left, .equal, view, .left))
        view.addConstraint(_JCLayoutConstraintMake(noteNameTextField, .right, .equal, view, .right))
        view.addConstraint(_JCLayoutConstraintMake(noteNameTextField, .top, .equal, view, .top, self.topOffset))
        view.addConstraint(_JCLayoutConstraintMake(noteNameTextField, .height, .equal, nil, .notAnAttribute, 45))
    }
    
    private func _setupNavigation() {
        navigationItem.rightBarButtonItem =  navRightButton
    }
    
    func textFieldDidChanged(_ textField: UITextField) {
        // markedTextRange指的是当前高亮选中的，除了长按选中，用户中文输入拼音过程往往也是高亮选中状态
        if textField.markedTextRange == nil {
            let text = textField.text!
            if text.count > 20 {
                let range = text.startIndex ..< text.index(text.startIndex, offsetBy: 20)
                let subText = text.substring(with: range)
                textField.text = subText
            }
            let count = 20 - (textField.text?.count)!
            tipLabel.text = "\(count)"
        }
    }
    
    //MARK: - click func
    func _saveNickname() {
        noteNameTextField.resignFirstResponder()
        MBProgressHUD_JChat.showMessage(message: "修改中", toView: self.view)
        user.updateNoteName(noteNameTextField.text!) { (result, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateFriendInfo), object: nil)
                self.navigationController?.popViewController(animated: true)
            } else {
                MBProgressHUD_JChat.show(text: "修改失败", view: self.view)
                print("error:\(String(describing: error?.localizedDescription))")
            }
        }
    }
}

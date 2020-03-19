//
//  JCGroupDescViewController.swift
//  JChat
//
//  Created by deng on 2017/5/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCGroupDescViewController: UIViewController {
    
    var group: JMSGGroup!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
        descTextView.text = group.desc
        var count = 80 - (group.desc?.count ?? 0)
        count = count < 0 ? 0 : count
        tipLabel.text = "\(count)"
        descTextView.becomeFirstResponder()
    }

    private lazy var bgView: UIView = UIView(frame: CGRect(x: 0, y: 64, width: self.view.width, height: 120))
    private lazy var descTextView: UITextView = UITextView(frame: CGRect(x: 15, y: 15, width: self.view.width - 30, height: 90))
    private lazy var navRightButton: UIBarButtonItem = UIBarButtonItem(title: "完成", style: .plain, target: self, action: #selector(_saveSignature))
    fileprivate lazy var tipLabel:  UILabel = UILabel(frame: CGRect(x: self.bgView.width - 15 - 50, y: self.bgView.height - 24, width: 50, height: 12))
    
    //MARK: - private func
    private func _init() {
        self.title = "群描述"
        automaticallyAdjustsScrollViewInsets = false;
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        
        bgView.backgroundColor = .white
        view.addSubview(bgView)
        
        descTextView.delegate = self
        descTextView.font = UIFont.systemFont(ofSize: 16)
        descTextView.backgroundColor = .white
        bgView.addSubview(descTextView)
        
        tipLabel.textColor = UIColor(netHex: 0x999999)
        tipLabel.font = UIFont.systemFont(ofSize: 12)
        tipLabel.textAlignment = .right
        bgView.addSubview(tipLabel)
        
        _setupNavigation()
    }
    
    private func _setupNavigation() {
        navigationItem.rightBarButtonItem =  navRightButton
    }
    
    //MARK: - click func
    func _saveSignature() {
        descTextView.resignFirstResponder()
        let desc = descTextView.text!
        MBProgressHUD_JChat.showMessage(message: "修改中...", toView: view)
        var name: String? = group.name
        if name!.isEmpty {
            name = nil
        }
        JMSGGroup.updateGroupInfo(withGroupId: group.gid, name: name, desc: desc) { (result, error) in
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

extension JCGroupDescViewController: UITextViewDelegate {
    func textViewDidChange(_ textView: UITextView) {
        textView.limitNonMarkedTextSize(80)

        let count = 80 - (nonMarkedText(textView)?.count ?? 0)
        tipLabel.text = "\(count)"
    }
}


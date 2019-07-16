//
//  JCSignatureViewController.swift
//  JChat
//
//  Created by deng on 2017/3/29.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCSignatureViewController: UIViewController {
    
    var signature: String!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
        signatureTextView.text = signature
        var count = 30 - signature.count
        count = count < 0 ? 0 : count
        tipLabel.text = "\(count)"
    }

    private var topOffset: CGFloat {
        if isIPhoneX {
            return 88
        }
        return 64
    }
    private lazy var saveButton: UIButton = {
        var saveButton = UIButton()
        saveButton.setTitle("提交", for: .normal)
        let colorImage = UIImage.createImage(color: UIColor(netHex: 0x2dd0cf), size: CGSize(width: self.view.width - 30, height: 40))
        saveButton.setBackgroundImage(colorImage, for: .normal)
        saveButton.addTarget(self, action: #selector(_saveSignature), for: .touchUpInside)
        return saveButton
    }()
    private lazy var bgView: UIView = {
        let bgView = UIView(frame: CGRect(x: 0, y: self.topOffset, width: self.view.width, height: 120))
        bgView.backgroundColor = .white
        return bgView
    }()
    private lazy var signatureTextView: UITextView = {
        let signatureTextView = UITextView(frame: CGRect(x: 15, y: 15, width: self.view.width - 30, height: 90))
        signatureTextView.delegate = self
        signatureTextView.font = UIFont.systemFont(ofSize: 16)
        signatureTextView.backgroundColor = .white
        return signatureTextView
    }()
    fileprivate lazy var tipLabel:  UILabel = {
        let tipLabel = UILabel(frame: CGRect(x: self.bgView.width - 15 - 50, y: self.bgView.height - 24, width: 50, height: 12))
        tipLabel.textColor = UIColor(netHex: 0x999999)
        tipLabel.font = UIFont.systemFont(ofSize: 12)
        tipLabel.textAlignment = .right
        return tipLabel
    }()

    private lazy var navRightButton: UIBarButtonItem = UIBarButtonItem(title: "保存", style: .plain, target: self, action: #selector(_saveSignature))

    //MARK: - private func 
    private func _init() {
        self.title = "个性签名"
        automaticallyAdjustsScrollViewInsets = false;
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        
        view.addSubview(saveButton)
        view.addSubview(bgView)

        bgView.addSubview(signatureTextView)
        bgView.addSubview(tipLabel)

        view.addConstraint(_JCLayoutConstraintMake(saveButton, .left, .equal, view, .left, 15))
        view.addConstraint(_JCLayoutConstraintMake(saveButton, .right, .equal, view, .right, -15))
        view.addConstraint(_JCLayoutConstraintMake(saveButton, .top, .equal, bgView, .bottom, 15))
        view.addConstraint(_JCLayoutConstraintMake(saveButton, .height, .equal, nil, .notAnAttribute, 40))
        
        _setupNavigation()
    }
    
    private func _setupNavigation() {
        navigationItem.rightBarButtonItem = navRightButton
    }
    
    //MARK: - click func
    func _saveSignature() {
        signatureTextView.resignFirstResponder()
        JMSGUser.updateMyInfo(withParameter: signatureTextView.text!, userFieldType: .fieldsSignature) { (resultObject, error) -> Void in
            if error == nil {
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateUserInfo), object: nil)
                self.navigationController?.popViewController(animated: true)
            } else {
                print("error:\(String(describing: error?.localizedDescription))")
            }
        }
    }
}

extension JCSignatureViewController: UITextViewDelegate {
    func textViewDidChange(_ textView: UITextView) {
        if textView.markedTextRange == nil {
            let text = textView.text!
            if text.count > 30 {
                let range = (text.startIndex ..< text.index(text.startIndex, offsetBy: 30))
                let subText = text.substring(with: range)
                textView.text = subText
            }
            let count = 30 - (textView.text?.count)!
            tipLabel.text = "\(count)"
        }
    }
}

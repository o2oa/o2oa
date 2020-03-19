//
//  JCFeedbackViewController.swift
//  JChat
//
//  Created by deng on 2017/3/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import YHPhotoKit

class JCFeedbackViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    fileprivate lazy var images: [UIImage] = []

    private var topOffset: CGFloat {
        if isIPhoneX {
            return 88
        }
        return 64
    }
    private lazy var bgView: UIView = UIView(frame: CGRect(x: 0, y: self.topOffset, width: self.view.width, height: 180))
    private lazy var textView: UITextView = {
        var textView = UITextView(frame: CGRect(x: 15, y: 12, width: self.view.width - 30, height: 65))
        textView.delegate = self
        textView.text = self.placeholder
        textView.font = UIFont.systemFont(ofSize: 16)
        textView.textColor = self.placeholderColor
        return textView
    }()
    fileprivate lazy  var tipLabel: UILabel = {
        var tipLabel = UILabel(frame: CGRect(x: self.view.width - 60 - 15 , y: 82.5, width: 60, height: 16.5))
        tipLabel.textAlignment = .right
        tipLabel.textColor = UIColor(netHex: 0x999999)
        tipLabel.font = UIFont.systemFont(ofSize: 16)
        tipLabel.text = "300";
        return tipLabel
    }()
    private lazy  var sendButton: UIButton = {
        var sendButton = UIButton(frame: CGRect(x: 15, y: self.topOffset + 180 + 20, width: self.view.width - 30, height: 40))
        let image = UIImage.createImage(color: UIColor(netHex: 0x2dd0cf),
                                        size: CGSize(width: self.view.width - 30, height: 40))
        sendButton.setBackgroundImage(image, for: .normal)
        sendButton.layer.cornerRadius = 3.0
        sendButton.layer.masksToBounds = true
        sendButton.setTitle("提交", for: .normal)
        sendButton.addTarget(self, action: #selector(_sendFeedBack), for: .touchUpInside)
        return sendButton
    }()
    fileprivate lazy  var photoBar: JCPhotoBar = JCPhotoBar(frame: CGRect(x: 0, y: 105, width: self.view.width, height: 65))
    fileprivate let placeholder = "请输入您的问题和意见，感谢您的使用。"
    fileprivate let placeholderColor = UIColor(netHex: 0x999999)
    
    //MARK: - private func
    private func _init() {
        self.title = "意见反馈"
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(_tapView))
        view.addGestureRecognizer(tap)

        photoBar.delegate = self
        photoBar.bindData([])

        bgView.backgroundColor = .white
        bgView.addSubview(tipLabel)
        bgView.addSubview(textView)
        bgView.addSubview(photoBar)

        view.addSubview(bgView)
        view.addSubview(sendButton)
    }
    
    @objc func _sendFeedBack() {
        if textView.text == placeholder && images.count == 0 {
            MBProgressHUD_JChat.show(text: "反馈内容不能为空", view: view)
            return
        }
        if !textView.text.isEmpty && textView.text != placeholder {
            let content = JMSGTextContent(text: textView.text)
            let message = JMSGMessage.createSingleMessage(with: content, username: "feedback_ios")
            JMSGMessage.send(message, optionalContent: JMSGOptionalContent.ex.default)
        }
        images.forEach { (image) in
            let content = JMSGImageContent(imageData: image.jpegData(compressionQuality: 1)!)
            let message = JMSGMessage.createSingleMessage(with: content!, username: "feedback_ios")
            JMSGMessage.send(message, optionalContent: JMSGOptionalContent.ex.default)
        }
        JMSGConversation.deleteSingleConversation(withUsername: "feedback_ios")
        view.endEditing(true)
        JCAlertView.bulid().setDelegate(self).setTitle("提交成功").setMessage("感谢您的反馈，我们将持续为您改进").addButton("确定").setTag(1001).show()
    }
    
    @objc func _tapView() {
        view.endEditing(true)
    }

}

extension JCFeedbackViewController: JCPhotoBarDelegate {
    
    private func _pushToSelectPhotos() {
        let photoPicker = YHPhotoPickerViewController()
        photoPicker.pickerDelegate = self
        let count = 4 - images.count
        photoPicker.maxPhotosCount = Int32(count)
        present(photoPicker, animated: true)
    }
    
    func photoBarAddImage() {
        if PHPhotoLibrary.authorizationStatus() != .authorized {
            PHPhotoLibrary.requestAuthorization({ (status) in
                DispatchQueue.main.sync {
                    if status != .authorized {
                        let alertView = UIAlertView(title: "无权限访问照片", message: "请在设备的设置-极光 IM中允许访问照片。", delegate: self, cancelButtonTitle: "好的", otherButtonTitles: "去设置")
                        alertView.show()
                    } else {
                        self._pushToSelectPhotos()
                    }
                }
            })
        } else {
            _pushToSelectPhotos()
        }
    }
    
    func photoBarClickImage(index: Int) {
        let browserImageVC = JCImageBrowserViewController()
        browserImageVC.imageArr = images
        browserImageVC.imgCurrentIndex = index
        present(browserImageVC, animated: true) {}
    }
    
    func photoBarDeleteImage(index: Int) {
        images.remove(at: index)
        photoBar.bindData(images)
    }
}

extension JCFeedbackViewController: YHPhotoPickerViewControllerDelegate {
    func selectedPhotoBeyondLimit(_ count: Int32, currentView view: UIView!) {
        MBProgressHUD_JChat.show(text: "最多选择\(count)张图片", view: nil)
    }
    
    func yhPhotoPickerViewController(_ PhotoPickerViewController: YHSelectPhotoViewController!, selectedPhotos photos: [Any]!) {
        for image in photos as! [UIImage] {
            images.append(image)
        }
        photoBar.bindData(images)
    }

}

extension JCFeedbackViewController: UITextViewDelegate {
    
    func textViewDidBeginEditing(_ textView: UITextView) {
        if textView.text == placeholder {
            textView.textColor = .black
            textView.text = ""
        }
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        if textView.text.count <= 0 {
            textView.text = placeholder
            textView.textColor = placeholderColor
            tipLabel.text = "300"
        }
    }
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if text == "\n" {
            return false
        }
        return true
    }
    
    func textViewDidChange(_ textView: UITextView) {
        if textView.markedTextRange == nil {
            let text = textView.text!
            if text.count > 300 {
                let range = (text.startIndex ..< text.index(text.startIndex, offsetBy: 300))
                
                let subText = text.substring(with: range)
                textView.text = subText
            }
            let count = 300 - (textView.text?.count)!
            tipLabel.text = "\(count)"
        }
    }
}

extension JCFeedbackViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        if alertView.tag == 1001 {
            navigationController?.popViewController(animated: true)
            return
        }
        if buttonIndex == 1 {
            JCAppManager.openAppSetter()
        }
    }
}

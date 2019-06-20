//
//  JCRegisterInfoViewController.swift
//  JChat
//
//  Created by deng on 2017/5/12.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage


class JCRegisterInfoViewController: UIViewController {
    
    var username: String!
    var password: String!
    
    //MARK: - life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        UIApplication.shared.setStatusBarStyle(.lightContent, animated: false)
        self.navigationController?.setNavigationBarHidden(false, animated: false)
    }
    
    private lazy var nicknameTextField: UITextField = {
        var textField = UITextField()
        textField.addTarget(self, action: #selector(textFieldDidChanged(_ :)), for: .editingChanged)
        textField.clearButtonMode = .whileEditing
        textField.placeholder = "请输入昵称"
        textField.font = UIFont.systemFont(ofSize: 16)
        textField.frame = CGRect(x: 38 + 40 + 15, y: 64 + 40 + 80 + 40, width: self.view.width - 76 - 38, height: 40)
        return textField
    }()
    
    fileprivate lazy var avatorView: UIImageView = {
        var avatorView = UIImageView()
        avatorView.isUserInteractionEnabled = true
        avatorView.frame = CGRect(x: self.view.centerX - 40, y: 64 + 40, width: 80, height: 80)
        avatorView.image = UIImage.loadImage("com_icon_upload")
        let tapGR = UITapGestureRecognizer(target: self, action: #selector(_tapHandler))
        avatorView.addGestureRecognizer(tapGR)
        return avatorView
    }()
    
    private lazy var registerButton: UIButton = {
        var button = UIButton()
        button.backgroundColor = UIColor(netHex: 0x2DD0CF)
//        button.frame = CGRect(x: 38, y: 64 + 40 + 80 + 40 + 40 + 38, width: self.view.width - 76, height: 40)
        let y = 64 + 40 + 80 + 40 + 40 + 38
        button.frame = CGRect(x: 38, y: y, width: Int(self.view.width - 76), height: 40)
        button.setTitle("完成", for: .normal)
        button.layer.cornerRadius = 3.0
        button.layer.masksToBounds = true
        button.addTarget(self, action: #selector(_userRegister), for: .touchUpInside)
        return button
    }()
    
    fileprivate lazy var tipsLabel: UILabel = {
        let tipsLabel = UILabel()
        tipsLabel.frame = CGRect(x: 38, y: 64 + 40 + 80 + 40 + 11 , width: 40, height: 18)
        tipsLabel.text = "昵称"
        tipsLabel.font = UIFont.systemFont(ofSize: 16)
        tipsLabel.textColor = UIColor(netHex: 0x999999)
        return tipsLabel
    }()
    
    fileprivate lazy var usernameLine: UILabel = {
        var line = UILabel()
        line.backgroundColor = UIColor(netHex: 0xD2D2D2)
        line.alpha = 0.4
        line.frame = CGRect(x: 38, y: self.nicknameTextField.y + 40, width: self.view.width - 76, height: 2)
        return line
    }()
    
    fileprivate lazy var imagePicker: UIImagePickerController = {
        var picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.cameraCaptureMode = .photo
        picker.delegate = self
        return picker
    }()
    
    fileprivate var image: UIImage?
    
    //MARK: - private func
    private func _init() {
        self.title = "补充信息"
        view.backgroundColor = .white
        navigationController?.setNavigationBarHidden(false, animated: false)
        navigationController?.interactivePopGestureRecognizer?.isEnabled = true
        
        nicknameTextField.addTarget(self, action: #selector(textFieldDidChanged(_ :)), for: .editingChanged)
        
        view.addSubview(avatorView)
        view.addSubview(nicknameTextField)
        view.addSubview(registerButton)
        view.addSubview(tipsLabel)
        view.addSubview(usernameLine)
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(_tapView))
        view.addGestureRecognizer(tap)
    }
    
    func textFieldDidChanged(_ textField: UITextField) {
        if textField.markedTextRange == nil {
            let text = textField.text!
            if text.count > 30 {
                let range = (text.startIndex ..< text.index(text.startIndex, offsetBy: 30))
                
                let subText = text.substring(with: range)
                textField.text = subText
            }
        }
    }
    
    func _tapView() {
        view.endEditing(true)
    }
     
    func _tapHandler() {
        view.endEditing(true)
        let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "  从相册中选择", "拍照")
        actionSheet.tag = 1001
        actionSheet.show(in: self.view)
    }

    //MARK: - click event
    func _userRegister() {
        MBProgressHUD_JChat.showMessage(message: "保存中", toView: self.view)
        userLogin(withUsername: self.username, password: self.password)
    }
    
    private func userLogin(withUsername: String, password: String) {
        JMSGUser.login(withUsername: self.username, password: self.password) { (result, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                self.setupNickname()
                self.uploadImage()
                UserDefaults.standard.set(self.username, forKey: kLastUserName)
                UserDefaults.standard.set(self.username, forKey: kCurrentUserName)
                let appDelegate = UIApplication.shared.delegate
                let window = appDelegate?.window!
                window?.rootViewController = JCMainTabBarController()
            } else {
                MBProgressHUD_JChat.show(text: "登录失败", view: self.view)
            }
        }
    }
    
    private func setupNickname() {
        JMSGUser.updateMyInfo(withParameter: self.nicknameTextField.text!, userFieldType: .fieldsNickname) { (resultObject, error) -> Void in
            if error == nil {
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateUserInfo), object: nil)
            } else {
                print("error:\(String(describing: error?.localizedDescription))")
            }
        }
    }
    
    private func uploadImage() {
        if let image = image {
            let imageData = image.jpegData(compressionQuality: 0.8)
            JMSGUser.updateMyInfo(withParameter: imageData!, userFieldType: .fieldsAvatar, completionHandler: { (result, error) in
                if error == nil {
                    let avatorData = NSKeyedArchiver.archivedData(withRootObject: imageData!)
                    UserDefaults.standard.set(avatorData, forKey: kLastUserAvator)
                    NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateUserInfo), object: nil)
                }
            })
        } else {
            UserDefaults.standard.removeObject(forKey: kLastUserAvator)
        }
    }
    
}

extension JCRegisterInfoViewController: UIActionSheetDelegate {
    func actionSheet(_ actionSheet: UIActionSheet, clickedButtonAt buttonIndex: Int) {
        switch buttonIndex {
        case 1:
            // 从相册中选择
            let picker = UIImagePickerController()
            picker.delegate = self
            picker.sourceType = .photoLibrary
            let temp_mediaType = UIImagePickerController.availableMediaTypes(for: picker.sourceType)
            picker.mediaTypes = temp_mediaType!
            picker.modalTransitionStyle = .coverVertical
            self.present(picker, animated: true, completion: nil)
            
        case 2:
            // 拍照
            present(imagePicker, animated: true, completion: nil)
        default:
            break
        }
    }
}

extension JCRegisterInfoViewController: UINavigationControllerDelegate, UIImagePickerControllerDelegate {
    
    // MARK: - UIImagePickerControllerDelegate
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        
        var image = info[UIImagePickerController.InfoKey.originalImage] as! UIImage?
        image = image?.fixOrientation()
        self.image = image
        avatorView.image = image
        picker.dismiss(animated: true, completion: nil)
    }
}


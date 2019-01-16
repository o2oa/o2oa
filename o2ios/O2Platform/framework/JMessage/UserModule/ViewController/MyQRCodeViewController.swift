//
//  MyQRCodeViewController.swift
//  JChat
//
//  Created by deng on 2017/8/15.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class MyQRCodeViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    fileprivate lazy var infoView: UIView = {
        var infoView = UIView()
        infoView.backgroundColor = .white
        return infoView
    }()
    
    private lazy var avator: UIImageView = {
        var avator = UIImageView()
        return avator
    }()
    
    private lazy var qrcode: UIImageView = {
        var qrcode = UIImageView()
        return qrcode
    }()
    
    private lazy var tipsLabel: UILabel = {
        var tipsLabel = UILabel()
        tipsLabel.textAlignment = .center
        tipsLabel.text = "扫一扫上面二维码，加我为好友吧！"
        tipsLabel.font = UIFont.systemFont(ofSize: 12)
        tipsLabel.textColor = UIColor(netHex: 0x999999)
        return tipsLabel
    }()
    
    private lazy var usernameLabel: UILabel = {
        var usernameLabel = UILabel()
        usernameLabel.textAlignment = .center
        usernameLabel.font = UIFont.systemFont(ofSize: 15)
        usernameLabel.textColor = UIColor(netHex: 0x999999)
        return usernameLabel
    }()
    
    private func _init() {
        self.title = "我的二维码"
        self.view.backgroundColor = UIColor(netHex: 0xE8EDF3)
        _setupNavigation()
        infoView.addSubview(avator)
        infoView.addSubview(usernameLabel)
        infoView.addSubview(qrcode)
        infoView.addSubview(tipsLabel)
        view.addSubview(infoView)
        
        view.addConstraint(_JCLayoutConstraintMake(infoView, .left, .equal, view, .left, 22.5))
        view.addConstraint(_JCLayoutConstraintMake(infoView, .right, .equal, view, .right, -22.5))
        view.addConstraint(_JCLayoutConstraintMake(infoView, .top, .equal, view, .top, 64 + 41.5))
        view.addConstraint(_JCLayoutConstraintMake(infoView, .bottom, .equal, view, .bottom, -80.5))
        
        infoView.addConstraint(_JCLayoutConstraintMake(avator, .centerX, .equal, infoView, .centerX))
        infoView.addConstraint(_JCLayoutConstraintMake(avator, .top, .equal, infoView, .top, 44.5))
        infoView.addConstraint(_JCLayoutConstraintMake(avator, .width, .equal, nil, .notAnAttribute, 80))
        infoView.addConstraint(_JCLayoutConstraintMake(avator, .height, .equal, nil, .notAnAttribute, 80))
        
        infoView.addConstraint(_JCLayoutConstraintMake(usernameLabel, .left, .equal, infoView, .left))
        infoView.addConstraint(_JCLayoutConstraintMake(usernameLabel, .top, .equal, avator, .bottom, 11.5))
        infoView.addConstraint(_JCLayoutConstraintMake(usernameLabel, .right, .equal, infoView, .right))
        infoView.addConstraint(_JCLayoutConstraintMake(usernameLabel, .height, .equal, nil, .notAnAttribute, 21))
        
        infoView.addConstraint(_JCLayoutConstraintMake(qrcode, .centerX, .equal, infoView, .centerX))
        infoView.addConstraint(_JCLayoutConstraintMake(qrcode, .top, .equal, avator, .bottom, 60))
        infoView.addConstraint(_JCLayoutConstraintMake(qrcode, .width, .equal, nil, .notAnAttribute, 240))
        infoView.addConstraint(_JCLayoutConstraintMake(qrcode, .height, .equal, nil, .notAnAttribute, 240))
        
        infoView.addConstraint(_JCLayoutConstraintMake(tipsLabel, .left, .equal, infoView, .left))
        infoView.addConstraint(_JCLayoutConstraintMake(tipsLabel, .top, .equal, qrcode, .bottom, 19))
        infoView.addConstraint(_JCLayoutConstraintMake(tipsLabel, .right, .equal, infoView, .right))
        infoView.addConstraint(_JCLayoutConstraintMake(tipsLabel, .height, .equal, nil, .notAnAttribute, 16.5))
        
        _bindData()
    }
    
    private func _bindData() {
        let user = JMSGUser.myInfo()
        let username = user.username
        let appkey = user.appKey
        
        user.thumbAvatarData { (data, id, error) in
            if let data = data {
                self.avator.image = UIImage(data: data)
            } else {
                self.avator.image = UIImage.loadImage("com_icon_user_80")
            }
        }
        usernameLabel.text = "用户名：\(username)"
        let url = "{\"type\":\"user\",\"user\": {\"appkey\":\"\(appkey ?? "")\",\"username\": \"\(username)\",\"platform\": \"iOS\"}}"
        qrcode.image = createQRForString(qrString: url, qrImageName: nil)
    }
    
    func createQRForString(qrString: String?, qrImageName: String?) -> UIImage?{
        if let sureQRString = qrString{
            let stringData = sureQRString.data(using: String.Encoding.utf8, allowLossyConversion: false)
            //创建一个二维码的滤镜
            let qrFilter = CIFilter(name: "CIQRCodeGenerator")
            qrFilter?.setValue(stringData, forKey: "inputMessage")
            qrFilter?.setValue("H", forKey: "inputCorrectionLevel")
            let qrCIImage = qrFilter?.outputImage
            
            // 创建一个颜色滤镜,黑白色
            let colorFilter = CIFilter(name: "CIFalseColor")!
            colorFilter.setDefaults()
            colorFilter.setValue(qrCIImage, forKey: "inputImage")
            colorFilter.setValue(CIColor(red: 0, green: 0, blue: 0), forKey: "inputColor0")
            colorFilter.setValue(CIColor(red: 1, green: 1, blue: 1), forKey: "inputColor1")
            // 返回二维码image
            let codeImage = UIImage(ciImage: (colorFilter.outputImage!.transformed(by: CGAffineTransform(scaleX: 5, y: 5))))
            
            // 中间一般放logo
            guard let imageName = qrImageName else {
                return codeImage
            }
            if let iconImage = UIImage(named: imageName) {
                let rect = CGRect(x: 0, y: 0, width: codeImage.size.width, height: codeImage.size.height)
                
                UIGraphicsBeginImageContext(rect.size)
                codeImage.draw(in: rect)
                let avatarSize = CGSize(width: rect.size.width*0.25, height: rect.size.height*0.25)
                
                let x = (rect.width - avatarSize.width) * 0.5
                let y = (rect.height - avatarSize.height) * 0.5
                iconImage.draw(in: CGRect(x: x, y: y, width: avatarSize.width, height: avatarSize.height))
                
                let resultImage = UIGraphicsGetImageFromCurrentImageContext()
                
                UIGraphicsEndImageContext()
                return resultImage
            }
            return codeImage
        }
        return nil
    }
    
    private func _setupNavigation() {
        let navButton = UIButton(frame: CGRect(x: 0, y: 0, width: 18, height: 18))
        navButton.setImage(UIImage.loadImage("com_icon_file_more"), for: .normal)
        navButton.addTarget(self, action: #selector(_saveImage), for: .touchUpInside)
        let item1 = UIBarButtonItem(customView: navButton)
        navigationItem.rightBarButtonItems =  [item1]
    }
    
    func _saveImage() {
        let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "保存图片")
        actionSheet.show(in: view)
    }
    
    func makeImage(_ view: UIView) -> UIImage {
        // TODO:- 要改写
        return UIImage(named: "aa")!
//
//        UIGraphicsBeginImageContextWithOptions(view.size, false, UIScreen.main.scale)
//        view.layer.render(in: UIGraphicsGetCurrentContext()!)
//        let image = UIGraphicsGetImageFromCurrentImageContext()
//        UIGraphicsEndImageContext()
//        return image!
    }
}

extension MyQRCodeViewController: UIActionSheetDelegate {
    @objc func actionSheet(_ actionSheet: UIActionSheet, clickedButtonAt buttonIndex: Int) {
        if buttonIndex == 1 {
            let image = makeImage(infoView)
            UIImageWriteToSavedPhotosAlbum(image, self, #selector(image(image:didFinishSavingWithError:contextInfo:)), nil)
        }
    }
    
    @objc func image(image: UIImage, didFinishSavingWithError error: NSError?, contextInfo:UnsafeRawPointer){
        if error == nil {
            MBProgressHUD_JChat.show(text: "保存成功", view: view)
        } else {
            MBProgressHUD_JChat.show(text: "保存失败，请重试", view: view)
        }
    }
}

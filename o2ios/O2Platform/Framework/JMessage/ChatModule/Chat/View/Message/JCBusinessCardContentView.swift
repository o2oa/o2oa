//
//  JCBusinessCardContentView.swift
//  JChat
//
//  Created by 邓永豪 on 2017/8/31.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCBusinessCardContentView: UIView, JCMessageContentViewType {

    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    open func apply(_ message: JCMessageType) {
        guard let content = message.content as? JCBusinessCardContent else {
            return
        }
        
        _message = message
        _delegate = content.delegate
        _userName = content.userName
        _appKey = content.appKey
        
        userNameLabel.text = "用户名：\(String(describing: _userName!))"
        
        if let userName = _userName {
            JMSGUser.userInfoArray(withUsernameArray: [userName], completionHandler: { (result, error) in
                let users = result as? [JMSGUser]
                guard let user = users?.first else {
                    return
                }
                self._user = user

                if user.nickname != nil && !user.nickname!.isEmpty {
                    self.nickNameLabel.text = user.nickname
                    self.nickNameLabel.frame = CGRect(x: 62, y: 11.5, width: 126, height: 22.5)
                } else {
                    self.userNameLabel.text = ""
                    self.nickNameLabel.text = user.username
                    self.nickNameLabel.frame = CGRect(x: 62, y: 22.5, width: 126, height: 22.5)
                }

                user.thumbAvatarData({ (data, msgId, error) in
                    if let data = data {
                        self.imageView.image = UIImage(data: data)
                    } else {
                        self.imageView.image = UIImage.loadImage("com_icon_user_40")
                    }
                })
            })
        }
    }
    
    
    private weak var _delegate: JCMessageDelegate?
    
    private var _userName: String?
    private var _appKey: String?
    private var _nickname: String?
    private var _message: JCMessageType!
    private var _user: JMSGUser?
    
    private lazy var imageView: UIImageView = {
        let imageView = UIImageView(frame: CGRect(x: 12, y: 13.5, width: 40, height: 40))
        imageView.image = UIImage.loadImage("com_icon_user_40")
        return imageView
    }()
    private lazy var line: UILabel = {
        let line = UILabel()
        line.frame = CGRect(x: 10, y: 66, width: 180, height: 1)
        line.layer.backgroundColor = UIColor(netHex: 0xE8E8E8).cgColor
        return line
    }()
    private lazy var userNameLabel: UILabel = {
        let userNameLabel = UILabel()
        userNameLabel.frame = CGRect(x: 62, y: 37, width: 126, height: 20)
        userNameLabel.font = UIFont.systemFont(ofSize: 14)
        userNameLabel.textColor = UIColor(netHex: 0x999999)
        return userNameLabel
    }()
    private lazy var nickNameLabel: UILabel = {
        let nickNameLabel = UILabel()
        nickNameLabel.frame = CGRect(x: 62, y: 11.5, width: 126, height: 22.5)
        nickNameLabel.font = UIFont.systemFont(ofSize: 16)
        nickNameLabel.textColor = .black
        return nickNameLabel
    }()
    private lazy var tipsLabel: UILabel = {
        let tipsLabel = UILabel()
        tipsLabel.frame = CGRect(x: 12, y: 69.5, width: 100, height: 14)
        tipsLabel.font = UIFont.systemFont(ofSize: 10)
        tipsLabel.textColor = UIColor(netHex: 0x989898)
        tipsLabel.text = "个人名片"
        return tipsLabel
    }()
    
    private func _commonInit() {
        _tapGesture()

        addSubview(imageView)
        addSubview(nickNameLabel)
        addSubview(userNameLabel)
        addSubview(tipsLabel)
        addSubview(line)
    }
    
    func _tapGesture() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(_clickCell))
        tap.numberOfTapsRequired = 1
        addGestureRecognizer(tap)
    }
    
    @objc func _clickCell() {
        _delegate?.message?(message: _message, user: _user, businessCardName: _userName!, businessCardAppKey: _appKey!)
    }

}

//
//  JCVerificationCell.swift
//  JChat
//
//  Created by deng on 2017/4/6.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCVerificationCell: JCTableViewCell {

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        _init()
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        _init()
    }
    
    func bindData(_ info: JCVerificationInfo) {
        self.info = info
        if info.nickname.isEmpty {
            nickname.text = info.username
        } else {
            nickname.text = info.nickname
        }
        
        reason.text = info.resaon
        switch info.state {
        case JCVerificationType.accept.rawValue:
            tipInfo.text = "已添加"
            acceptButton.isHidden = true
        case JCVerificationType.wait.rawValue:
            tipInfo.text = "等待验证"
            acceptButton.isHidden = true
        case JCVerificationType.receive.rawValue:
            acceptButton.isHidden = false
        case JCVerificationType.reject.rawValue:
            tipInfo.text = "已拒绝"
            acceptButton.isHidden = true
        default:
            break
        }
        iconView.image = UIImage.loadImage("com_icon_user_50")
        JMSGUser.userInfoArray(withUsernameArray: [info.username]) { (result, error) in
            if error == nil {
                let users = result as! [JMSGUser]
                let user = users.first
                user?.thumbAvatarData({ (data, id, error) in
                    if let data = data {
                        let image = UIImage(data: data)
                        self.iconView.image = image
                    }
                })
            }
        }
    }
    
    private var info: JCVerificationInfo!
    private lazy var iconView: UIImageView = UIImageView()
    private lazy var nickname: UILabel = UILabel()
    private lazy var reason: UILabel = UILabel()
    private lazy var tipInfo: UILabel = UILabel()
    private lazy var acceptButton: UIButton = {
        let acceptButton = UIButton()
        acceptButton.setTitle("同意", for: .normal)
        let bgImage = UIImage.createImage(color: UIColor(netHex: 0x2dd0cf), size: CGSize(width: 50, height: 25))
        acceptButton.setBackgroundImage(bgImage, for: .normal)
        acceptButton.addTarget(self, action: #selector(_clickAcceptButton), for: .touchUpInside)
        return acceptButton
    }()
    
    private func _init() {

        nickname.font = UIFont.systemFont(ofSize: 16)
        nickname.textColor = UIColor(netHex: 0x2c2c2c)
        nickname.textAlignment = .left
        
        reason.font = UIFont.systemFont(ofSize: 14)
        reason.textColor = UIColor(netHex: 0x999999)
        reason.textAlignment = .left
        
        tipInfo.font = UIFont.systemFont(ofSize: 14)
        tipInfo.textColor = UIColor(netHex: 0xb5b6b6)
        tipInfo.textAlignment = .right
        
        acceptButton.titleLabel?.font = UIFont.systemFont(ofSize: 14)        
        
        addSubview(iconView)
        addSubview(nickname)
        addSubview(reason)
        addSubview(tipInfo)
        addSubview(acceptButton)
        
        addConstraint(_JCLayoutConstraintMake(iconView, .top, .equal, contentView, .top, 7.5))
        addConstraint(_JCLayoutConstraintMake(iconView, .left, .equal, self, .left, 15))
        addConstraint(_JCLayoutConstraintMake(iconView, .width, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(iconView, .height, .equal, nil, .notAnAttribute, 50))
        
        addConstraint(_JCLayoutConstraintMake(nickname, .top, .equal, contentView, .top, 10.5))
        addConstraint(_JCLayoutConstraintMake(nickname, .left, .equal, iconView, .right, 11))
        addConstraint(_JCLayoutConstraintMake(nickname, .right, .equal, self, .centerX))
        addConstraint(_JCLayoutConstraintMake(nickname, .height, .equal, nil, .notAnAttribute, 22.5))
        
        addConstraint(_JCLayoutConstraintMake(reason, .top, .equal, nickname, .bottom, 1.5))
        addConstraint(_JCLayoutConstraintMake(reason, .left, .equal, nickname, .left))
        addConstraint(_JCLayoutConstraintMake(reason, .right, .equal, tipInfo, .left, -5))
        addConstraint(_JCLayoutConstraintMake(reason, .height, .equal, nil, .notAnAttribute, 20))
        
        addConstraint(_JCLayoutConstraintMake(tipInfo, .centerY, .equal, self, .centerY))
        addConstraint(_JCLayoutConstraintMake(tipInfo, .width, .equal, nil, .notAnAttribute, 80))
        addConstraint(_JCLayoutConstraintMake(tipInfo, .right, .equal, self, .right, -15))
        addConstraint(_JCLayoutConstraintMake(tipInfo, .height, .equal, nil, .notAnAttribute, 20))
        
        addConstraint(_JCLayoutConstraintMake(acceptButton, .top, .equal, contentView, .top, 20))
        addConstraint(_JCLayoutConstraintMake(acceptButton, .right, .equal, self, .right, -15))
        addConstraint(_JCLayoutConstraintMake(acceptButton, .width, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(acceptButton, .height, .equal, nil, .notAnAttribute, 25))
        
    }
    
    func _clickAcceptButton() {
        JMSGFriendManager.acceptInvitation(withUsername: info.username, appKey: info.appkey) { (result, error) in
            if error == nil {
                self.info.state = JCVerificationType.accept.rawValue
                JCVerificationInfoDB.shareInstance.updateData(self.info)
                self.bindData(self.info)
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateFriendList), object: nil)
            }
        }
    }

}

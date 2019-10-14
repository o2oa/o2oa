//
//  JCConversationCell.swift
//  JChat
//
//  Created by deng on 2017/3/22.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage
import CocoaLumberjack

class JCConversationCell: JCTableViewCell {
    
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

    private lazy var avatorView: UIImageView = {
        let avatorView = UIImageView()
        avatorView.contentMode = .scaleToFill
        return avatorView
    }()
    private lazy var statueView: UIImageView = UIImageView()
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFont(ofSize: 16)
        return titleLabel
    }()
    private lazy var msgLabel: UILabel = {
        let msgLabel = UILabel()
        msgLabel.textColor = UIColor(netHex: 0x808080)
        msgLabel.font = UIFont.systemFont(ofSize: 14)
        return msgLabel
    }()
    private lazy var dateLabel: UILabel = {
        let dateLabel = UILabel()
        dateLabel.textAlignment = .right
        dateLabel.font = UIFont.systemFont(ofSize: 12)
        dateLabel.textColor = UIColor(netHex: 0xB3B3B3)
        return dateLabel
    }()
    private lazy var redPoin: UILabel = {
        let redPoin = UILabel(frame: CGRect(x: 65 - 17, y: 4.5, width: 20, height: 20))
        redPoin.textAlignment = .center
        redPoin.font = UIFont.systemFont(ofSize: 11)
        redPoin.textColor = .white
        redPoin.layer.backgroundColor = UIColor(netHex: 0xEB424C).cgColor
        redPoin.textAlignment = .center
        return redPoin
    }()
    
    //MARK: - public func
    open func bindConversation(_ conversation: JMSGConversation) {
        statueView.isHidden = true
        let isGroup = conversation.ex.isGroup
        if conversation.unreadCount != nil && (conversation.unreadCount?.intValue)! > 0 {
            redPoin.isHidden = false
            var text = ""
            if (conversation.unreadCount?.intValue)! > 99 {
                text = "99+"
                redPoin.layer.cornerRadius = 9.0
                redPoin.layer.masksToBounds = true
                redPoin.frame = CGRect(x: 65 - 28, y: 4.5, width: 33, height: 18)
            } else {
                redPoin.layer.cornerRadius = 10.0
                redPoin.layer.masksToBounds = true
                redPoin.frame = CGRect(x: 65 - 15, y: 4.5, width: 20, height: 20)
                text = "\(conversation.unreadCount!)"
            }
            redPoin.text = text
            
            var isNoDisturb = false
            if isGroup {
                if let group = conversation.target as? JMSGGroup {
                    isNoDisturb = group.isNoDisturb
                }
            } else {
                if let user = conversation.target as? JMSGUser {
                    isNoDisturb = user.isNoDisturb
                }
            }
            
            if isNoDisturb {
                redPoin.layer.cornerRadius = 4.0
                redPoin.layer.masksToBounds = true
                redPoin.text = ""
                redPoin.frame = CGRect(x: 65 - 5, y: 4.5, width: 8, height: 8)
            }
        } else {
            redPoin.isHidden = true
        }
        
        if let latestMessage = conversation.latestMessage {
            let time = latestMessage.timestamp.intValue / 1000
            let date = Date(timeIntervalSince1970: TimeInterval(time))
            dateLabel.text = date.conversationDate()
        } else {
            dateLabel.text = ""
        }
        
        msgLabel.text = conversation.latestMessageContentText()
        if isGroup {
            if let latestMessage = conversation.latestMessage {
                let fromUser = latestMessage.fromUser
                if !fromUser.isEqual(to: JMSGUser.myInfo()) &&
                    latestMessage.contentType != .eventNotification &&
                    latestMessage.contentType != .prompt {
                    msgLabel.text = "\(fromUser.displayName()):\(msgLabel.text!)"
                }
                if conversation.unreadCount != nil &&
                    conversation.unreadCount!.intValue > 0 &&
                    latestMessage.contentType != .prompt {
                    if latestMessage.isAtAll() {
                        msgLabel.attributedText = getAttributString(attributString: "[@所有人]", string: msgLabel.text!)
                    } else if latestMessage.isAtMe() {
                        msgLabel.attributedText = getAttributString(attributString: "[有人@我]", string: msgLabel.text!)
                    }
                }
            }
        }
        
        if let draft = JCDraft.getDraft(conversation) {
            if !draft.isEmpty {
                msgLabel.attributedText = getAttributString(attributString: "[草稿]", string: draft)
            }
        }

        if !isGroup {
            let user = conversation.target as? JMSGUser
            titleLabel.text = user?.displayName() ?? ""
            // 处理头像
            DDLogDebug("更新头像，发送者头像：\(user?.username ?? "")")
            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":user?.username as AnyObject])
            let url = URL(string: urlstr!)
            let bound = self.avatorView.bounds
            if bound.width <= 0 || bound.height <= 0 {
                self.avatorView.bounds = CGRect(x: 15, y: 7.5, width: 50, height: 50)
            }
            self.avatorView.hnk_setImageFromURL(url!)

//            user?.thumbAvatarData { (data, username, error) in
//                guard let imageData = data else {
//                    self.avatorView.image = self.userDefaultIcon
//                    return
//                }
//                let image = UIImage(data: imageData)
//                self.avatorView.image = image
//            }
        } else {
            if let group = conversation.target as? JMSGGroup {
                titleLabel.text = group.displayName()
                if group.isShieldMessage {
                    statueView.isHidden = false
                }
                group.thumbAvatarData({ (data, _, error) in
                    if let data = data {
                        self.avatorView.image = UIImage(data: data)
                    } else {
                        self.avatorView.image = self.groupDefaultIcon
                    }
                })
            }
        }

        if conversation.ex.isSticky {
            backgroundColor = UIColor(netHex: 0xF5F6F8)
        } else {
            backgroundColor = .white
        }
    }
    
    func getAttributString(attributString: String, string: String) -> NSMutableAttributedString {
        let attr = NSMutableAttributedString(string: "")
        var attrSearchString: NSAttributedString!
        attrSearchString = NSAttributedString(string: attributString, attributes: [ NSAttributedString.Key.foregroundColor : UIColor(netHex: 0xEB424C), NSAttributedString.Key.font : UIFont.boldSystemFont(ofSize: 14.0)])
        attr.append(attrSearchString)
        attr.append(NSAttributedString(string: string))
        return attr
    }
    
    private lazy var groupDefaultIcon = UIImage.loadImage("com_icon_group_50")
    private lazy var userDefaultIcon = UIImage.loadImage("com_icon_user_50")
    
    //MARK: - private func
    private func _init() {
        avatorView.image = userDefaultIcon
        statueView.image = UIImage.loadImage("com_icon_shield")
        
        contentView.addSubview(avatorView)
        contentView.addSubview(statueView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(msgLabel)
        contentView.addSubview(dateLabel)
        contentView.addSubview(redPoin)
        
        addConstraint(_JCLayoutConstraintMake(avatorView, .left, .equal, contentView, .left, 15))
        addConstraint(_JCLayoutConstraintMake(avatorView, .top, .equal, contentView, .top, 7.5))
        addConstraint(_JCLayoutConstraintMake(avatorView, .width, .equal, nil, .notAnAttribute, 50))
        addConstraint(_JCLayoutConstraintMake(avatorView, .height, .equal, nil, .notAnAttribute, 50))
        
        addConstraint(_JCLayoutConstraintMake(titleLabel, .left, .equal, avatorView, .right, 10.5))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .top, .equal, contentView, .top, 10.5))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .right, .equal, dateLabel, .left, -3))
        addConstraint(_JCLayoutConstraintMake(titleLabel, .height, .equal, nil, .notAnAttribute, 22.5))
        
        addConstraint(_JCLayoutConstraintMake(msgLabel, .left, .equal, titleLabel, .left))
        addConstraint(_JCLayoutConstraintMake(msgLabel, .top, .equal, titleLabel, .bottom, 1.5))
        addConstraint(_JCLayoutConstraintMake(msgLabel, .right, .equal, statueView, .left, -5))
        addConstraint(_JCLayoutConstraintMake(msgLabel, .height, .equal, nil, .notAnAttribute, 20))
        
        addConstraint(_JCLayoutConstraintMake(dateLabel, .top, .equal, contentView, .top, 16))
        addConstraint(_JCLayoutConstraintMake(dateLabel, .right, .equal, contentView, .right, -15))
        addConstraint(_JCLayoutConstraintMake(dateLabel, .height, .equal, nil, .notAnAttribute, 16.5))
        addConstraint(_JCLayoutConstraintMake(dateLabel, .width, .equal, nil, .notAnAttribute, 100))
        
        addConstraint(_JCLayoutConstraintMake(statueView, .top, .equal, dateLabel, .bottom, 7))
        addConstraint(_JCLayoutConstraintMake(statueView, .right, .equal, contentView, .right, -16))
        addConstraint(_JCLayoutConstraintMake(statueView, .height, .equal, nil, .notAnAttribute, 12))
        addConstraint(_JCLayoutConstraintMake(statueView, .width, .equal, nil, .notAnAttribute, 12))
    }
}

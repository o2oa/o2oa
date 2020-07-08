//
//  IMConversationItemCell.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import O2OA_Auth_SDK
import CocoaLumberjack

class IMConversationItemCell: UITableViewCell {

    @IBOutlet weak var avatarImg: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var unreadNumberLabel: UILabel!
    @IBOutlet weak var emojiImg: UIImageView!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    
    func setInstantContent(item: InstantMessage) {
        self.avatarImg.image = UIImage(named: "icon_email")
        self.nameLabel.text = "通知消息"
        self.messageLabel.isHidden = false
        self.messageLabel.text = item.title
        if let time = item.createTime {
            let date = time.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
            self.timeLabel.text = date.friendlyTime()
        }
        self.emojiImg.isHidden = true
        self.unreadNumberLabel.isHidden = true
    }

    
    func bindConversation(conversation: IMConversationInfo) {
        //avatar name
        if conversation.type == o2_im_conversation_type_single {
            var person = ""
            conversation.personList?.forEach({ (p) in
                if  p != O2AuthSDK.shared.myInfo()?.distinguishedName {
                    person = p
                }
            })
            if person != "" {
                //头像
                let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":person as AnyObject], generateTime: false)
                if let u = URL(string: urlstr!) {
                    self.avatarImg.hnk_setImageFromURL(u)
                }else {
                    DDLogError("错误， 没有生成头像url")
                    self.avatarImg.image = UIImage(named: "icon_men")
                }
                //姓名
                self.nameLabel.text = person.split("@").first ?? ""
            }else {
                self.avatarImg.image = UIImage(named: "icon_men")
                self.nameLabel.text = ""
            }
        }else {//todo 群组头像 ?
            self.nameLabel.text = conversation.title
            self.avatarImg.image = UIImage(named: "group_default")
        }
        //time
        if let time = conversation.lastMessage?.createTime {
            let date = time.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
            self.timeLabel.text = date.friendlyTime()
        }
        // message
        if let msgBody = conversation.lastMessage?.body, let body = parseJson(msg: msgBody) {
            if body.type == o2_im_msg_type_text || body.type == o2_im_msg_type_image
                || body.type == o2_im_msg_type_audio || body.type == o2_im_msg_type_location {
                self.messageLabel.text = body.body
                self.messageLabel.isHidden = false
                self.emojiImg.isHidden = true
            } else if body.type == o2_im_msg_type_emoji {
                self.messageLabel.isHidden = true
                self.emojiImg.isHidden = false
                let bundle = Bundle().o2EmojiBundle(anyClass: IMConversationItemCell.self)
                let path = o2ImEmojiPath(emojiBody: body.body!)
                self.emojiImg.image = UIImage(named: path, in: bundle, compatibleWith: nil)
            } else {
                self.messageLabel.isHidden = true
                self.emojiImg.isHidden = true
            }
        }else {
            self.messageLabel.isHidden = true
            self.emojiImg.isHidden = true
        }
        //unread number
        let number = conversation.unreadNumber ?? 0
        if number > 0 && number < 100 {
            self.unreadNumberLabel.text = "\(number)"
            self.unreadNumberLabel.isHidden = false
        }else if number >= 100 {
            self.unreadNumberLabel.text = "99.."
            self.unreadNumberLabel.isHidden = false
        }else {
            self.unreadNumberLabel.isHidden = true
        }
        
    }
    
    private func parseJson(msg: String) -> IMMessageBodyInfo? {
        return IMMessageBodyInfo.deserialize(from: msg)
    }
    
    
    
}

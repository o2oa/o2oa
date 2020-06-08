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
    
    @IBOutlet weak var emojiImg: UIImageView!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
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
                DDLogDebug("头像url \(String(describing: urlstr))")
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
        }
        //time
        if let time = conversation.lastMessage?.createTime {
            DDLogDebug("time: \(time)")
            let date = time.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
            DDLogDebug("date \(date.description)")
            self.timeLabel.text = date.friendlyTime()
        }
        // message
        if let msgBody = conversation.lastMessage?.body, let body = parseJson(msg: msgBody) {
            if body.type == o2_im_msg_type_text {
                self.messageLabel.text = body.body
                self.messageLabel.isHidden = false
                self.emojiImg.isHidden = true
            }else if body.type == o2_im_msg_type_emoji {
                self.messageLabel.isHidden = true
                self.emojiImg.isHidden = false
//                self.emojiImg.image = UIImage(named: "setting_myCRM")
                //todo emoji表情导入
                let bundle = Bundle().o2EmojiBundle(anyClass: IMConversationItemCell.self)
                let path = o2ImEmojiPath(emojiBody: body.body!)
                DDLogDebug("path: \(path)")
                self.emojiImg.image = UIImage(named: path, in: bundle, compatibleWith: nil)
            }else {
                self.messageLabel.isHidden = true
                self.emojiImg.isHidden = true
            }
        }
        
    }
    
    private func parseJson(msg: String) -> IMMessageBodyInfo? {
        return IMMessageBodyInfo.deserialize(from: msg)
    }
    
    
    
}

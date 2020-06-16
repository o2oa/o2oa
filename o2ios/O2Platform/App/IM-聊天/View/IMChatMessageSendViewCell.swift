//
//  IMChatMessageSendViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/10.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class IMChatMessageSendViewCell: UITableViewCell {
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var avatarImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var messageBackgroundView: UIView!
    @IBOutlet weak var messageBgWidth: NSLayoutConstraint!
    @IBOutlet weak var messageBgHeight: NSLayoutConstraint!
    
    var delegate: IMChatMessageDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func setContent(item: IMMessageInfo) {
        //time
        if let time = item.createTime {
            let date = time.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
            self.timeLabel.text = date.friendlyTime()
        }
        //name avatart
        if let person = item.createPerson {
            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":person as AnyObject], generateTime: false)
            if let u = URL(string: urlstr!) {
                self.avatarImageView.hnk_setImageFromURL(u)
            }else {
                self.avatarImageView.image = UIImage(named: "icon_men")
            }
            //姓名
            self.nameLabel.text = person.split("@").first ?? ""
        }else {
            self.avatarImageView.image = UIImage(named: "icon_men")
            self.nameLabel.text = ""
        }
        self.messageBackgroundView.removeSubviews()
        if let jsonBody = item.body, let body = parseJson(msg: jsonBody) {
            if o2_im_msg_type_emoji == body.type {
                emojiMsgRender(emoji: body.body!)
            }else if o2_im_msg_type_image == body.type {
                imageMsgRender(info: body)
            } else {
                textMsgRender(msg: body.body!)
            }
        }
    }
    
    //图片消息
    private func imageMsgRender(info: IMMessageBodyInfo) {
        let width: CGFloat = 144
        let height: CGFloat = 192
        self.messageBgWidth.constant = width + 20
        self.messageBgHeight.constant = height + 20
        //图片
        let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: width, height: height))
        if let fileId = info.fileId {
            DDLogDebug("fileId  :\(fileId)")
            let urlStr = AppDelegate.o2Collect.generateURLWithAppContextKey(
                CommunicateContext.communicateContextKey,
                query: CommunicateContext.imDownloadImageWithSizeQuery,
                parameter: ["##id##": fileId as AnyObject,
                    "##width##": "144" as AnyObject,
                    "##height##": "192" as AnyObject], generateTime: false)
            if let url = URL(string: urlStr!) {
                imageView.hnk_setImageFromURL(url)
            } else {
                imageView.image = UIImage(named: "chat_image")
            }
        } else if let filePath = info.fileTempPath {
            DDLogDebug("filePath  :\(filePath)")
            imageView.hnk_setImageFromFile(filePath)
        } else {
            imageView.image = UIImage(named: "chat_image")
        }
        imageView.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(imageView)
        imageView.addTapGesture { (tap) in
            self.delegate?.clickImageMessage(fileId: info.fileId, tempPath: info.fileTempPath)
        }
        let top = NSLayoutConstraint(item: imageView, attribute: .top, relatedBy: .equal, toItem: imageView.superview!, attribute: .top, multiplier: 1, constant: 10)
        let bottom = NSLayoutConstraint(item: imageView.superview!, attribute: .bottom, relatedBy: .equal, toItem: imageView, attribute: .bottom, multiplier: 1, constant: 10)
        let left = NSLayoutConstraint(item: imageView, attribute: .leading, relatedBy: .equal, toItem: imageView.superview!, attribute: .leading, multiplier: 1, constant: 10)
        let right = NSLayoutConstraint(item: imageView.superview!, attribute: .trailing, relatedBy: .equal, toItem: imageView, attribute: .trailing, multiplier: 1, constant: 10)
        NSLayoutConstraint.activate([top, bottom, left, right])

    }
    
    private func emojiMsgRender(emoji: String) {
        let emojiSize = 36
        let width = CGFloat(emojiSize + 20)
        let height = CGFloat(emojiSize + 20)
        self.messageBgWidth.constant = width
        self.messageBgHeight.constant = height
        //背景图片
        let bgImg = UIImageView(frame: CGRect(x: 0, y: 0, width: width, height: height))
        let insets = UIEdgeInsets(top: 28, left: 5, bottom: 5, right: 10); // 上、左、下、右
        var bubble = UIImage(named: "chat_bubble_outgoing")
        bubble = bubble?.resizableImage(withCapInsets: insets, resizingMode: .stretch)
        bgImg.image = bubble
        self.messageBackgroundView.addSubview(bgImg)
        //表情图
        let emojiImage = UIImageView(frame: CGRect(x: 0, y: 0, width: emojiSize, height: emojiSize))
        let bundle = Bundle().o2EmojiBundle(anyClass: IMChatMessageSendViewCell.self)
        let path = o2ImEmojiPath(emojiBody: emoji)
        emojiImage.image = UIImage(named: path, in: bundle, compatibleWith: nil)
        emojiImage.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(emojiImage)
        let top = NSLayoutConstraint(item: emojiImage, attribute: .top, relatedBy: .equal, toItem: emojiImage.superview!, attribute: .top, multiplier: 1, constant: 10)
        let bottom = NSLayoutConstraint(item: emojiImage.superview! , attribute: .bottom, relatedBy: .equal, toItem: emojiImage, attribute: .bottom, multiplier: 1, constant: 10)
        let left = NSLayoutConstraint(item: emojiImage, attribute: .leading, relatedBy: .equal, toItem: emojiImage.superview!, attribute: .leading, multiplier: 1, constant: 10)
        let right = NSLayoutConstraint(item: emojiImage.superview!, attribute: .trailing, relatedBy: .equal, toItem: emojiImage, attribute: .trailing, multiplier: 1, constant: 10)
        NSLayoutConstraint.activate([top, bottom, left, right])
    }
    
    private func textMsgRender(msg: String) {
        let size = calTextSize(str: msg)
        self.messageBgWidth.constant = size.width + 20
        self.messageBgHeight.constant = size.height + 20
        //背景图片
        let bgImg = UIImageView(frame: CGRect(x: 0, y: 0, width: size.width + 20, height: size.height + 20))
        let insets = UIEdgeInsets(top: 28, left: 5, bottom: 5, right: 10); // 上、左、下、右
        var bubble = UIImage(named: "chat_bubble_outgoing")
        bubble = bubble?.resizableImage(withCapInsets: insets, resizingMode: .stretch)
        bgImg.image = bubble
        self.messageBackgroundView.addSubview(bgImg)
        //文字
        let label = generateMessagelabel(str: msg, size: size)
        label.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(label)
        let top = NSLayoutConstraint(item: label, attribute: .top, relatedBy: .equal, toItem: label.superview!, attribute: .top, multiplier: 1, constant: 10)
        let left = NSLayoutConstraint(item: label, attribute: .leading, relatedBy: .equal, toItem: label.superview!, attribute: .leading, multiplier: 1, constant: 10)
        let right = NSLayoutConstraint(item: label.superview!, attribute: .trailing, relatedBy: .equal, toItem: label, attribute: .trailing, multiplier: 1, constant: 10)
        NSLayoutConstraint.activate([top, left, right])
    }
    
    private func generateMessagelabel(str: String, size: CGSize) -> UILabel {
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        label.text = str
        label.font = UIFont.systemFont(ofSize: 16)
        label.numberOfLines = 0
        label.lineBreakMode = .byCharWrapping
        label.preferredMaxLayoutWidth = size.width
        return label
    }
    
    
    private func calTextSize(str: String) -> CGSize {
        let size = CGSize(width: 176, height: CGFloat(MAXFLOAT))
        return str.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16)], context: nil).size
    }
    
    //解析json为消息对象
    private func parseJson(msg: String) -> IMMessageBodyInfo? {
        return IMMessageBodyInfo.deserialize(from: msg)
    }
    
}

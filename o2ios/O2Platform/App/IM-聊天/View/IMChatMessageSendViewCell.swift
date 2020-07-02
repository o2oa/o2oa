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
    
    //音频消息 主体view
    private lazy var audioView: IMAudioView = {
        let view = Bundle.main.loadNibNamed("IMAudioView", owner: self, options: nil)?.first as! IMAudioView
        view.frame = CGRect(x: 0, y: 0, width: IMAudioView.IMAudioView_width, height: IMAudioView.IMAudioView_height)
        return view
    }()
    
    //位置消息 主体view
    private lazy var locationView: IMLocationView = {
        let view = Bundle.main.loadNibNamed("IMLocationView", owner: self, options: nil)?.first as! IMLocationView
        view.frame = CGRect(x: 0, y: 0, width: IMLocationView.IMLocationViewWidth, height: IMLocationView.IMLocationViewHeight)
        return view
    }()
    
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
            }else if o2_im_msg_type_audio == body.type {
                audioMsgRender(info: body)
            } else if o2_im_msg_type_location == body.type {
                locationMsgRender(info: body)
            }  else {
                textMsgRender(msg: body.body!)
            }
        }
    }
    
    
    
    //位置消息
    private func locationMsgRender(info: IMMessageBodyInfo) {
        self.messageBgWidth.constant = IMLocationView.IMLocationViewWidth + 20
        self.messageBgHeight.constant = IMLocationView.IMLocationViewHeight + 20
        self.locationView.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(self.locationView)
        self.locationView.setLocationAddress(address: info.address ?? "")
        //点击打开地址
        self.locationView.addTapGesture { (tap) in
            //open map view//open map view
            self.delegate?.openLocatinMap(info: info)
        }
        self.constraintWithContent(contentView: self.locationView)
    }
    
    //音频消息
    private func audioMsgRender(info: IMMessageBodyInfo) {
        self.messageBgWidth.constant = IMAudioView.IMAudioView_width + 20
        self.messageBgHeight.constant = IMAudioView.IMAudioView_height + 20
        self.audioView.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(self.audioView)
        self.audioView.setDuration(duration: info.audioDuration ?? "0")
        self.audioView.addTapGesture { (tap) in
            self.playAudio(info: info)
        }
        self.constraintWithContent(contentView: self.audioView)
    }
    
    private func playAudio(info: IMMessageBodyInfo) {
        if let fileId = info.fileId {
            var ext = info.fileExtension ?? "mp3"
            if ext.isEmpty {
                ext = "mp3"
            }
            O2IMFileManager.shared.getFileLocalUrl(fileId: fileId, fileExtension: ext)
                .then { (url) in
                    do {
                        let data = try Data(contentsOf: url)
                        AudioPlayerManager.shared.managerAudioWithData(data, toplay: true)
                    } catch {
                        DDLogError(error.localizedDescription)
                    }
            }.catch { (e) in
                DDLogError(e.localizedDescription)
            }
        } else if let filePath = info.fileTempPath {
            do {
                let data = try Data(contentsOf: URL(fileURLWithPath: filePath))
                AudioPlayerManager.shared.managerAudioWithData(data, toplay: true)
            } catch {
                DDLogError(error.localizedDescription)
            }
        }
    }
    
    private func constraintWithContent(contentView: UIView) {
        let top = NSLayoutConstraint(item: contentView, attribute: .top, relatedBy: .equal, toItem: contentView.superview!, attribute: .top, multiplier: 1, constant: 10)
        let bottom = NSLayoutConstraint(item: contentView.superview!, attribute: .bottom, relatedBy: .equal, toItem: contentView, attribute: .bottom, multiplier: 1, constant: 10)
        let left = NSLayoutConstraint(item: contentView, attribute: .leading, relatedBy: .equal, toItem: contentView.superview!, attribute: .leading, multiplier: 1, constant: 10)
        let right = NSLayoutConstraint(item: contentView.superview!, attribute: .trailing, relatedBy: .equal, toItem: contentView, attribute: .trailing, multiplier: 1, constant: 10)
        NSLayoutConstraint.activate([top, bottom, left, right])
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
            self.delegate?.clickImageMessage(info: info)
        }
        self.constraintWithContent(contentView: imageView)
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
        self.constraintWithContent(contentView: emojiImage)
    }
    
    private func textMsgRender(msg: String) {
        let size = msg.getSizeWithMaxWidth(fontSize: 16, maxWidth: messageWidth)
        self.messageBgWidth.constant = size.width + 28
        self.messageBgHeight.constant = size.height + 28
        //背景图片
        let bgImg = UIImageView(frame: CGRect(x: 0, y: 0, width: size.width + 28, height: size.height + 28))
        let insets = UIEdgeInsets(top: 28, left: 5, bottom: 5, right: 10); // 上、左、下、右
        var bubble = UIImage(named: "chat_bubble_outgoing")
        bubble = bubble?.resizableImage(withCapInsets: insets, resizingMode: .stretch)
        bgImg.image = bubble
        self.messageBackgroundView.addSubview(bgImg)
        //文字
        let label = generateMessagelabel(str: msg, size: size)
        label.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(label)
        self.constraintWithContent(contentView: label)
//        let top = NSLayoutConstraint(item: label, attribute: .top, relatedBy: .equal, toItem: label.superview!, attribute: .top, multiplier: 1, constant: 10)
//        let left = NSLayoutConstraint(item: label, attribute: .leading, relatedBy: .equal, toItem: label.superview!, attribute: .leading, multiplier: 1, constant: 10)
//        let right = NSLayoutConstraint(item: label.superview!, attribute: .trailing, relatedBy: .equal, toItem: label, attribute: .trailing, multiplier: 1, constant: 10)
//        NSLayoutConstraint.activate([top, left, right])
    }
    
    private func generateMessagelabel(str: String, size: CGSize) -> UILabel {
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: size.width + 8, height: size.height + 8))
        label.text = str
        label.font = UIFont.systemFont(ofSize: 16)
        label.numberOfLines = 0
        label.lineBreakMode = .byCharWrapping
        label.preferredMaxLayoutWidth = size.width
        return label
    }
    
    
//    private func calTextSize(str: String) -> CGSize {
//        let size = CGSize(width: 176, height: CGFloat(MAXFLOAT))
//        return str.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16)], context: nil).size
//    }
    
    //解析json为消息对象
    private func parseJson(msg: String) -> IMMessageBodyInfo? {
        return IMMessageBodyInfo.deserialize(from: msg)
    }
    
}

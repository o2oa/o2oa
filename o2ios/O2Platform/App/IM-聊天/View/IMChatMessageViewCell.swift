//
//  IMChatMessageViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/8.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

protocol IMChatMessageDelegate {
    func clickImageMessage(info: IMMessageBodyInfo)
    func openLocatinMap(info: IMMessageBodyInfo)
    func openApplication(storyboard: String)
    func openWork(workId: String)
}

class IMChatMessageViewCell: UITableViewCell {

    @IBOutlet weak var avatarImage: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var messageBackgroundView: UIView!
    @IBOutlet weak var messageBackgroundWidth: NSLayoutConstraint!
    @IBOutlet weak var messageBackgroundHeight: NSLayoutConstraint!
    

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

    //普通通知消息
    func setInstantContent(item: InstantMessage) {
        if let time = item.createTime {
            let date = time.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
            self.timeLabel.text = date.friendlyTime()
        }
        self.messageBackgroundView.removeSubviews()
        if let msg = item.title {
            let msgLabel = textMsgRender(msg: msg)
            setColorAndClickEvent(item: item, label: msgLabel)
        }
        if let type = item.type {
            if type.starts(with: "task_") {
                self.avatarImage.image = UIImage(named: "icon_daiban")
                self.titleLabel.text = "待办消息"
            } else if type.starts(with: "taskCompleted_") {
                self.avatarImage.image = UIImage(named: "icon_taskcompleted")
                self.titleLabel.text = "已办消息"
            } else if type.starts(with: "read_") {
                self.avatarImage.image = UIImage(named: "icon_read")
                self.titleLabel.text = "待阅消息"
            } else if type.starts(with: "readCompleted_") {
                self.avatarImage.image = UIImage(named: "icon_readcompleted")
                self.titleLabel.text = "已阅消息"
            } else if type.starts(with: "review_") || type.starts(with: "work_") || type.starts(with: "process_") {
                self.avatarImage.image = UIImage(named: "icon_daiban")
                self.titleLabel.text = "工作消息"
            } else if type.starts(with: "meeting_") {
                self.avatarImage.image = UIImage(named: "icon_meeting")
                self.titleLabel.text = "会议消息"
            } else if type.starts(with: "attachment_") {
                self.avatarImage.image = UIImage(named: "icon_yunpan")
                self.titleLabel.text = "云盘消息"
            } else if type.starts(with: "calendar_") {
                self.avatarImage.image = UIImage(named: "icon_calendar")
                self.titleLabel.text = "日历消息"
            } else if type.starts(with: "cms_") {
                self.avatarImage.image = UIImage(named: "icon_cms")
                self.titleLabel.text = "信息中心消息"
            } else if type.starts(with: "bbs_") {
                self.avatarImage.image = UIImage(named: "icon_bbs")
                self.titleLabel.text = "论坛消息"
            } else if type.starts(with: "mind_") {
                self.avatarImage.image = UIImage(named: "icon_mindMap")
                self.titleLabel.text = "脑图消息"
            } else {
                self.avatarImage.image = UIImage(named: "icon_email")
                self.titleLabel.text = "其他消息"
            }
        }
    }
    
    private func setcc(label:UILabel, clickEvent: ((UITapGestureRecognizer)->Void)?) {
        if let textString = label.text {
            let attributedString = NSMutableAttributedString(string: textString)
            attributedString.addAttribute(.underlineStyle, value: NSUnderlineStyle.single.rawValue, range: NSRange(location: 0, length: attributedString.length))
            attributedString.addAttribute(.foregroundColor, value: base_blue_color, range: NSRange(location: 0, length: attributedString.length))
            label.attributedText = attributedString
            label.addTapGesture(action: clickEvent)
        }
    }
    
    private func setColorAndClickEvent(item: InstantMessage, label:UILabel) {
        
        func parseWorkId(body: String) -> String? {
            if let jsonData = String(body).data(using: .utf8) {
                let dicArr = try! JSONSerialization.jsonObject(with: jsonData, options: .allowFragments) as! [String:AnyObject]
                if let work = dicArr["work"] as? String {
                    return work
                }
                if let workCompleted = dicArr["workCompleted"] as? String {
                    return workCompleted
                }
            }
            return nil
        }
        
        if let type = item.type {
            if type.starts(with: "task_") {
                if !type.contains("_delete") {
                    guard let body = item.body else {
                        return
                    }
                    guard let workId = parseWorkId(body: body) else {
                        return
                    }
                    setcc(label: label) { tap in
                        //打开工作 ?
                        self.delegate?.openWork(workId: workId)
                    }
                }
            } else if type.starts(with: "taskCompleted_") {
                if !type.contains("_delete") {
                    guard let body = item.body else {
                        return
                    }
                    guard let workId = parseWorkId(body: body) else {
                        return
                    }
                    setcc(label: label) { tap in
                        //打开已办
                        self.delegate?.openWork(workId: workId)
                    }
                }
            } else if type.starts(with: "read_") {
                if !type.contains("_delete") {
                    guard let body = item.body else {
                        return
                    }
                    guard let workId = parseWorkId(body: body) else {
                        return
                    }
                    setcc(label: label) { tap in
                        //打开待阅
                        self.delegate?.openWork(workId: workId)
                    }
                }
            } else if type.starts(with: "readCompleted_") {
                if !type.contains("_delete") {
                    guard let body = item.body else {
                        return
                    }
                    guard let workId = parseWorkId(body: body) else {
                        return
                    }
                    setcc(label: label) { tap in
                        //打开已阅
                        self.delegate?.openWork(workId: workId)
                    }
                }
            } else if type.starts(with: "review_") || type.starts(with: "work_") || type.starts(with: "process_") {
                if !type.contains("_delete") {
                    guard let body = item.body else {
                        return
                    }
                    guard let workId = parseWorkId(body: body) else {
                        return
                    }
                    setcc(label: label) { tap in
                        //打开 其他工作
                        self.delegate?.openWork(workId: workId)
                    }
                }
            } else if type.starts(with: "meeting_") {
                setcc(label: label) { tap in
                    //打开会议模块
                    self.delegate?.openApplication(storyboard: "meeting")
                }
            } else if type.starts(with: "attachment_") {
                setcc(label: label) { tap in
                    //打开云盘
                    self.delegate?.openApplication(storyboard: "CloudFile")
                }
            } else if type.starts(with: "calendar_") {
                setcc(label: label) { tap in
                    //打开日历
                    self.delegate?.openApplication(storyboard: "calendar")
                }
            } else if type.starts(with: "cms_") {
                setcc(label: label) { tap in
                    //打开cms
                    self.delegate?.openApplication(storyboard: "information")
                }
            } else if type.starts(with: "bbs_") {
               setcc(label: label) { tap in
                    //打开论坛
                self.delegate?.openApplication(storyboard: "bbs")
                }
            } else if type.starts(with: "mind_") {
                setcc(label: label) { tap in
                    //打开脑图
                    self.delegate?.openApplication(storyboard: "mind")
                }
            } else {
                
            }
        }
    }
    
    

    //聊天消息
    func setContent(item: IMMessageInfo) {
        //time
        if let time = item.createTime {
            let date = time.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
            self.timeLabel.text = date.friendlyTime()
        }
        //name avatart
        if let person = item.createPerson {
            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##": person as AnyObject], generateTime: false)
            if let u = URL(string: urlstr!) {
                self.avatarImage.hnk_setImageFromURL(u)
            } else {
                self.avatarImage.image = UIImage(named: "icon_men")
            }
            //姓名
            self.titleLabel.text = person.split("@").first ?? ""
        } else {
            self.avatarImage.image = UIImage(named: "icon_men")
            self.titleLabel.text = ""
        }
        self.messageBackgroundView.removeSubviews()
        if let jsonBody = item.body, let body = parseJson(msg: jsonBody) {
            if body.type == o2_im_msg_type_emoji {
                emojiMsgRender(emoji: body.body!)
            } else if body.type == o2_im_msg_type_image {
                imageMsgRender(info: body)
            } else if o2_im_msg_type_audio == body.type {
                audioMsgRender(info: body)
            } else if o2_im_msg_type_location == body.type {
                locationMsgRender(info: body)
            } else {
                _ = textMsgRender(msg: body.body!)
            }
        }
    }

    //位置消息
    private func locationMsgRender(info: IMMessageBodyInfo) {
        self.messageBackgroundWidth.constant = IMLocationView.IMLocationViewWidth + 20
        self.messageBackgroundHeight.constant = IMLocationView.IMLocationViewHeight + 20
        self.locationView.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(self.locationView)
        self.locationView.setLocationAddress(address: info.address ?? "")
        //点击打开地址
        self.locationView.addTapGesture { (tap) in
            //open map view
            self.delegate?.openLocatinMap(info: info)
        }
        self.constraintWithContent(contentView: self.locationView)
    }

    //音频消息
    private func audioMsgRender(info: IMMessageBodyInfo) {
        self.messageBackgroundWidth.constant = IMAudioView.IMAudioView_width + 20
        self.messageBackgroundHeight.constant = IMAudioView.IMAudioView_height + 20
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
        self.messageBackgroundWidth.constant = width + 20
        self.messageBackgroundHeight.constant = height + 20
        //图片
        let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: width, height: height))
        if let fileId = info.fileId {
            DDLogDebug("file id :\(fileId)")
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
        self.messageBackgroundWidth.constant = width
        self.messageBackgroundHeight.constant = height
        //背景图片
        let bgImg = UIImageView(frame: CGRect(x: 0, y: 0, width: width, height: height))
        let insets = UIEdgeInsets(top: 28, left: 10, bottom: 5, right: 5); // 上、左、下、右
        var bubble = UIImage(named: "chat_bubble_incomming")
        bubble = bubble?.resizableImage(withCapInsets: insets, resizingMode: .stretch)
        bgImg.image = bubble
        self.messageBackgroundView.addSubview(bgImg)
        //表情图
        let emojiImage = UIImageView(frame: CGRect(x: 0, y: 0, width: emojiSize, height: emojiSize))
        let bundle = Bundle().o2EmojiBundle(anyClass: IMChatMessageViewCell.self)
        let path = o2ImEmojiPath(emojiBody: emoji)
        emojiImage.image = UIImage(named: path, in: bundle, compatibleWith: nil)
        emojiImage.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(emojiImage)
        self.constraintWithContent(contentView: emojiImage)
    }

    private func textMsgRender(msg: String) -> UILabel {
        let size = msg.getSizeWithMaxWidth(fontSize: 16, maxWidth: messageWidth)
        self.messageBackgroundWidth.constant = size.width + 28
        self.messageBackgroundHeight.constant = size.height + 28
        //背景图片
        let bgImg = UIImageView(frame: CGRect(x: 0, y: 0, width: size.width + 28, height: size.height + 28))
        let insets = UIEdgeInsets(top: 28, left: 10, bottom: 5, right: 5); // 上、左、下、右
        var bubble = UIImage(named: "chat_bubble_incomming")
        bubble = bubble?.resizableImage(withCapInsets: insets, resizingMode: .stretch)
        bgImg.image = bubble
        self.messageBackgroundView.addSubview(bgImg)
        //文字
        let label = generateMessagelabel(str: msg, size: size)
        label.translatesAutoresizingMaskIntoConstraints = false
        self.messageBackgroundView.addSubview(label)
        self.constraintWithContent(contentView: label)
        return label
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
//        let size = CGSize(width: messageWidth.toCGFloat, height: CGFloat(MAXFLOAT))
//        return str.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16)], context: nil).size
//    }

    //解析json为消息对象
    private func parseJson(msg: String) -> IMMessageBodyInfo? {
        return IMMessageBodyInfo.deserialize(from: msg)
    }
}

//
//  FileManagerViewController.swift
//  JChat
//
//  Created by 邓永豪 on 2017/8/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

protocol FileManagerDelegate {
    func didSelectFile(_ fileMessage: JMessage)
    func isEditModel() -> Bool
}

class FileManagerViewController: UIViewController {
    
    var conversation: JMSGConversation!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    fileprivate let imageFileViewController = ImageFileViewController()
    fileprivate let docFileViewController =  FileViewController()
    fileprivate let videoFileViewController =  FileViewController()
    fileprivate let musicFileViewController =  FileViewController()
    fileprivate let otherFileViewController =  FileViewController()
    
    private var allMessage: [JMSGMessage] = []
    private var imageMessages: [JMSGMessage] = []
    private var docMessages: [JMSGMessage] = []
    private var videoMessages: [JMSGMessage] = []
    private var musicMessages: [JMSGMessage] = []
    private var otherFileMessages: [JMSGMessage] = []
    private var selectMessage: [JMSGMessage] = []

    private var topOffset: CGFloat {
        if isIPhoneX {
            return 88
        }
        return 64
    }
    
    private lazy var tabedSlideView: DLTabedSlideView = {
        var tabedSlideView = DLTabedSlideView(frame: CGRect(x: 0, y: self.topOffset, width: self.view.width, height: self.view.height - self.topOffset))
        tabedSlideView.delegate = self
        tabedSlideView.baseViewController = self
        tabedSlideView.tabItemNormalColor = .black
        tabedSlideView.tabItemSelectedColor =  O2ThemeManager.color(for: "Base.base_color")!
        tabedSlideView.tabbarTrackColor = O2ThemeManager.color(for: "Base.base_color")!
        tabedSlideView.tabbarBackgroundImage = UIImage.createImage(color: .white, size: CGSize(width: self.view.width, height: 39))
        tabedSlideView.tabbarBottomSpacing = 3.0
        return tabedSlideView
    }()
    
    private lazy var navRightButton: UIBarButtonItem = UIBarButtonItem(title: "选择", style: .plain, target: self, action: #selector(_clickNavRightButton))
    fileprivate var isEditMode = false
    private lazy var barView: UIView = {
        var barView = UIView(frame: CGRect(x: 0, y: self.view.height - 45, width: self.view.width, height: 45))
        let line = UILabel(frame: CGRect(x: 0, y: 0, width: barView.width, height: 0.5))
        line.layer.backgroundColor = UIColor(netHex: 0xE8E8E8).cgColor
        barView.addSubview(line)
        barView.backgroundColor = .white
        barView.isHidden = true
        return barView
    }()
    private lazy var delButton: UIButton = {
        var delButton = UIButton()
        delButton.setTitle("删除", for: .normal)
        delButton.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        delButton.layer.cornerRadius = 3
        delButton.layer.masksToBounds = true
        delButton.addTarget(self, action: #selector(_delFile), for: .touchUpInside)
        delButton.backgroundColor = UIColor(netHex: 0xEB424D)
        return delButton
    }()
    
    private lazy var selectCountLabel: UILabel = {
        var label = UILabel(frame: CGRect(x: 17.5, y: 11.5, width: 120, height: 22))
        label.textAlignment = .left
        label.textColor = UIColor(netHex: 0x999999)
        label.font = UIFont.systemFont(ofSize: 16)
        label.isHidden = true
        return label
    }()
    
    private func _init() {
        self.title = "聊天文件"
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        
        view.addSubview(tabedSlideView)
        let imageItem = DLTabedbarItem(title: "照片", image: nil, selectedImage: nil)
        let fileItem = DLTabedbarItem(title: "文档", image: nil, selectedImage: nil)
        let videoItem = DLTabedbarItem(title: "视频", image: nil, selectedImage: nil)
        let musicItem = DLTabedbarItem(title: "音乐", image: nil, selectedImage: nil)
        let otherItem = DLTabedbarItem(title: "其它", image: nil, selectedImage: nil)
        tabedSlideView.tabbarItems = [imageItem!, fileItem!, videoItem!, musicItem!, otherItem!]
        tabedSlideView.buildTabbar()
        tabedSlideView.selectedIndex = 0
        
        view.addSubview(barView)
        barView.addSubview(delButton)
        barView.addSubview(selectCountLabel)
        delButton.frame = CGRect(x: barView.width - 72 - 16.6, y: 8.5, width: 72, height: 29)
        _setupNavigation()
        
        conversation.allMessages({ (result, error) in
            if let message = result as? [JMSGMessage] {
                self.allMessage = message
                self.classifyMessage(message)
            }
        })
        
        NotificationCenter.default.addObserver(self, selector: #selector(_didSelectFileMessage), name: NSNotification.Name(rawValue: "kDidSelectFileMessage"), object: nil)
    }
    
    func _didSelectFileMessage() {
        selectMessage.removeAll()
        selectMessage.append(contentsOf: imageFileViewController.selectMessages)
        selectMessage.append(contentsOf: docFileViewController.selectMessages)
        selectMessage.append(contentsOf: videoFileViewController.selectMessages)
        selectMessage.append(contentsOf: musicFileViewController.selectMessages)
        selectMessage.append(contentsOf: otherFileViewController.selectMessages)
        
        if selectMessage.count > 0 {
            selectCountLabel.isHidden = false
            selectCountLabel.text = "已选（\(selectMessage.count)）"
        } else {
            selectCountLabel.isHidden = true
        }
    }
    
    func classifyMessage(_ messages: [JMSGMessage]) {
        docMessages.removeAll()
        videoMessages.removeAll()
        musicMessages.removeAll()
        imageMessages.removeAll()
        otherFileMessages.removeAll()
        for message in messages {
            if message.contentType == .image {
                imageMessages.append(message)
                continue
            }
            if !message.ex.isFile {
                continue
            }
            if let fileType = message.ex.fileType {
                switch fileType.fileFormat() {
                case .document:
                    docMessages.append(message)
                case .video:
                    videoMessages.append(message)
                case .voice:
                    musicMessages.append(message)
                case .photo:
                    imageMessages.append(message)
                default:
                    otherFileMessages.append(message)
                }
            }
        }
        reloadAllFileViewController()
    }
    
    func reloadAllFileViewController() {
        imageFileViewController.messages = imageMessages
        docFileViewController.messages = docMessages
        videoFileViewController.messages = videoMessages
        musicFileViewController.messages = musicMessages
        otherFileViewController.messages = otherFileMessages
        
        imageFileViewController.reloadDate()
        docFileViewController.reloadDate()
        videoFileViewController.reloadDate()
        musicFileViewController.reloadDate()
        otherFileViewController.reloadDate()
    }
    
    private func _setupNavigation() {
        self.navigationItem.rightBarButtonItem =  navRightButton
    }
    
    @objc func _clickNavRightButton() {
        if isEditMode {
            navRightButton.title = "选择"
            tabedSlideView.frame = CGRect(x: tabedSlideView.x, y: tabedSlideView.y, width: tabedSlideView.width, height: tabedSlideView.height + 45)
            barView.isHidden = true
        } else {
            navRightButton.title = "取消"
            tabedSlideView.frame = CGRect(x: tabedSlideView.x, y: tabedSlideView.y, width: tabedSlideView.width, height: tabedSlideView.height - 45)
            barView.isHidden = false
        }
        isEditMode = !isEditMode
        imageFileViewController.isEditModel = isEditMode
        otherFileViewController.isEditModel = isEditMode
        videoFileViewController.isEditModel = isEditMode
        musicFileViewController.isEditModel = isEditMode
        docFileViewController.isEditModel = isEditMode
        selectMessage = []
    }
    
    func _delFile() {
        if selectMessage.count <= 0 {
            return
        }
        isEditMode = true
        for message in selectMessage {
            allMessage = allMessage.filter({ (m) -> Bool in
                message.msgId != m.msgId
            })
            conversation.deleteMessage(withMessageId: message.msgId)
        }
        classifyMessage(allMessage)
        _clickNavRightButton()
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: kReloadAllMessage), object: nil)
    }
}

extension FileManagerViewController: DLTabedSlideViewDelegate {
    func numberOfTabs(in sender: DLTabedSlideView!) -> Int {
        return 5
    }
    
    func dlTabedSlideView(_ sender: DLTabedSlideView!, controllerAt index: Int) -> UIViewController! {
        switch index {
        case 0:
            return imageFileViewController
        case 1:
            docFileViewController.fileType = .doc
            return docFileViewController
        case 2:
            videoFileViewController.fileType = .video
            return videoFileViewController
        case 3:
            musicFileViewController.fileType = .music
            return musicFileViewController
        default:
            otherFileViewController.fileType = .other
            return otherFileViewController
        }
    }
}

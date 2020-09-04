//
//  IMChatViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/8.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import O2OA_Auth_SDK
import BSImagePicker
import Photos
import Alamofire
import AlamofireImage
import SwiftyJSON
import QuickLook

class IMChatViewController: UIViewController {

    // MARK: - IBOutlet
    //消息列表
    @IBOutlet weak var tableView: UITableView!
    //消息输入框
    @IBOutlet weak var messageInputView: UITextField!
    //底部工具栏的高度约束
    @IBOutlet weak var bottomBarHeightConstraint: NSLayoutConstraint!
    //底部工具栏
    @IBOutlet weak var bottomBar: UIView!

    private let emojiBarHeight = 196
    //表情窗口
    private lazy var emojiBar: IMChatEmojiBarView = {
        let view = Bundle.main.loadNibNamed("IMChatEmojiBarView", owner: self, options: nil)?.first as! IMChatEmojiBarView
        view.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: emojiBarHeight.toCGFloat)
        return view
    }()
    //语音录制按钮
    private lazy var audioBtnView: IMChatAudioView = {
        let view = Bundle.main.loadNibNamed("IMChatAudioView", owner: self, options: nil)?.first as! IMChatAudioView
        view.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: emojiBarHeight.toCGFloat)
        view.delegate = self
        return view
    }()
    //录音的时候显示的view
    private var voiceIconImage: UIImageView?
    private var voiceIocnTitleLable: UILabel?
    private var voiceImageSuperView: UIView?
    
    
    //预览文件
    private lazy var previewVC: CloudFilePreviewController = {
        return CloudFilePreviewController()
    }()

    private lazy var viewModel: IMViewModel = {
        return IMViewModel()
    }()

    // MARK: - properties
    var conversation: IMConversationInfo? = nil
    
    //private
    private var chatMessageList: [IMMessageInfo] = []
    private var page = 0
    private var isShowEmoji = false
    private var isShowAudioView = false
    private var bottomBarHeight = 64 //底部输入框 表情按钮 的高度
    private let bottomToolbarHeight = 46 //底部工具栏 麦克风 相册 相机等按钮的位置


    // MARK: - functions
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib(nibName: "IMChatMessageViewCell", bundle: nil), forCellReuseIdentifier: "IMChatMessageViewCell")
        self.tableView.register(UINib(nibName: "IMChatMessageSendViewCell", bundle: nil), forCellReuseIdentifier: "IMChatMessageSendViewCell")
        self.tableView.separatorStyle = .none
//        self.tableView.rowHeight = UITableView.automaticDimension
//        self.tableView.estimatedRowHeight = 144
        self.tableView.backgroundColor = UIColor(hex: "#f3f3f3")
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
           self.loadMsgList()
        })
        
        self.messageInputView.delegate = self

        //底部安全距离 老机型没有
        self.bottomBarHeight = Int(iPhoneX ? 64 + IPHONEX_BOTTOM_SAFE_HEIGHT: 64) + self.bottomToolbarHeight
        self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat
        self.bottomBar.topBorder(width: 1, borderColor: base_gray_color.alpha(0.5))
        self.messageInputView.backgroundColor = base_gray_color

        //标题
        if self.conversation?.type == o2_im_conversation_type_single {
            if let c = self.conversation {
                var person = ""
                c.personList?.forEach({ (p) in
                    if p != O2AuthSDK.shared.myInfo()?.distinguishedName {
                        person = p
                    }
                })
                if !person.isEmpty {
                    self.title = person.split("@").first ?? ""
                }
            }
        } else {
            self.title = self.conversation?.title
        }
        //群会话 添加修改标题的按钮
        if self.conversation?.type == o2_im_conversation_type_group &&
            O2AuthSDK.shared.myInfo()?.distinguishedName == self.conversation?.adminPerson {
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "修改", style: .plain, target: self, action: #selector(clickUpdate))
        }
        
        //获取聊天数据
        self.loadMsgList()
        //阅读
        self.viewModel.readConversation(conversationId: self.conversation?.id)
    }

    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(receiveMessageFromWs(notice:)), name: OONotification.websocket.notificationName, object: nil)
    }
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }


    @objc private func receiveMessageFromWs(notice: Notification) {
        DDLogDebug("接收到websocket im 消息")
        if let message = notice.object as? IMMessageInfo {
            if message.conversationId == self.conversation?.id {
                self.chatMessageList.append(message)
                self.scrollMessageToBottom()
                self.viewModel.readConversation(conversationId: self.conversation?.id)
            }
        }
    }
    
    @objc private func clickUpdate() {
        self.showSheetAction(title: "", message: "选择要修改的项", actions: [
            UIAlertAction(title: "修改群名", style: .default, handler: { (action) in
                self.updateTitle()
            }),
            UIAlertAction(title: "修改成员", style: .default, handler: { (action) in
                self.updatePeople()
            })
        ])
    }
    
    private func updateTitle() {
        self.showPromptAlert(title: "", message: "修改群名", inputText: "") { (action, result) in
            if result.isEmpty {
                self.showError(title: "请输入群名")
            }else {
                self.showLoading()
                self.viewModel.updateConversationTitle(id: (self.conversation?.id!)!, title: result)
                    .then { (c) in
                        self.title = result
                        self.conversation?.title = result
                        self.showSuccess(title: "修改成功")
                }.catch { (err) in
                    DDLogError(err.localizedDescription)
                    self.showError(title: "修改失败")
                }
            }
        }
    }
    
    private func updatePeople() {
        //选择人员 反选已经存在的成员
        if let users = self.conversation?.personList  {
            self.showContactPicker(modes: [.person], callback: { (result) in
                if let people = result.users  {
                    if people.count >= 3 {
                        var peopleDNs: [String] = []
                        var containMe = false
                        people.forEach { (item) in
                            peopleDNs.append(item.distinguishedName!)
                            if O2AuthSDK.shared.myInfo()?.distinguishedName == item.distinguishedName {
                                containMe = true
                            }
                        }
                        if !containMe {
                            peopleDNs.append((O2AuthSDK.shared.myInfo()?.distinguishedName)!)
                        }
                        self.showLoading()
                        self.viewModel.updateConversationPeople(id: (self.conversation?.id!)!, users: peopleDNs)
                            .then { (c)  in
                                self.conversation?.personList = peopleDNs
                                self.showSuccess(title: "修改成功")
                        }.catch { (err) in
                            DDLogError(err.localizedDescription)
                            self.showError(title: "修改失败")
                        }
                    }else {
                        self.showError(title: "选择人数不足3人")
                    }
                }else {
                    self.showError(title: "请选择人员")
                }
            }, initUserPickedArray: users)
        }else {
            self.showError(title: "成员列表数据错误！")
        }
    }

    //获取消息
    private func loadMsgList() {
        if let c = self.conversation, let id = c.id {
            self.viewModel.myMsgPageList(page: self.page + 1, conversationId: id).then { (list) in
                if !list.isEmpty {
                    self.page += 1
                    self.chatMessageList.insert(contentsOf: list, at: 0)
                    if self.page ==  1 {
                        self.scrollMessageToBottom()
                    }else {
                        DispatchQueue.main.async {
                            self.tableView.reloadData()
                        }
                    }
                }
                if self.tableView.mj_header.isRefreshing(){
                    self.tableView.mj_header.endRefreshing()
                }
                
            }.catch { (error) in
                DDLogError(error.localizedDescription)
                if self.tableView.mj_header.isRefreshing(){
                    self.tableView.mj_header.endRefreshing()
                }
            }
        } else {
            self.showError(title: "参数错误！！！")
        }
    }
    //刷新tableview 滚动到底部
    private func scrollMessageToBottom() {
        DispatchQueue.main.async {
            self.tableView.reloadData()
            if self.chatMessageList.count > 0 {
                self.tableView.scrollToRow(at: IndexPath(row: self.chatMessageList.count - 1, section: 0), at: .bottom, animated: false)
            }
        }
    }

    //发送文本消息
    private func sendTextMessage() {
        guard let msg = self.messageInputView.text else {
            return
        }
        self.messageInputView.text = ""
        let body = IMMessageBodyInfo()
        body.type = o2_im_msg_type_text
        body.body = msg
        sendMessage(body: body)
    }
    //发送表情消息
    private func sendEmojiMessage(emoji: String) {
        let body = IMMessageBodyInfo()
        body.type = o2_im_msg_type_emoji
        body.body = emoji
        sendMessage(body: body)
    }

    //发送地图消息消息
    private func sendLocationMessage(loc: O2LocationData) {
        let body = IMMessageBodyInfo()
        body.type = o2_im_msg_type_location
        body.body = o2_im_msg_body_location
        body.address = loc.address
        body.addressDetail = loc.addressDetail
        body.longitude = loc.longitude
        body.latitude = loc.latitude
        sendMessage(body: body)
    }

    //发送消息到服务器
    private func sendMessage(body: IMMessageBodyInfo) {
        let message = IMMessageInfo()
        message.body = body.toJSONString()
        message.id = UUID().uuidString
        message.conversationId = self.conversation?.id
        message.createPerson = O2AuthSDK.shared.myInfo()?.distinguishedName
        message.createTime = Date().formatterDate(formatter: "yyyy-MM-dd HH:mm:ss")
        //添加到界面
        self.chatMessageList.append(message)
        self.scrollMessageToBottom()
        //发送消息到服务器
        self.viewModel.sendMsg(msg: message)
            .then { (result) in
                DDLogDebug("发送消息成功 \(result)")
                self.viewModel.readConversation(conversationId: self.conversation?.id)
            }.catch { (error) in
                DDLogError(error.localizedDescription)
                self.showError(title: "发送消息失败!")
        }
    }

    //选择照片
    private func chooseImage() {
        let vc = FileBSImagePickerViewController().bsImagePicker()
        vc.settings.fetch.assets.supportedMediaTypes = [.image]

        presentImagePicker(vc, select: { (asset) in
            //选中一个
        }, deselect: { (asset) in
                //取消选中一个
            }, cancel: { (assets) in
                //取消
            }, finish: { (assets) in
                //结果
                if assets.count > 0 {
                    switch assets[0].mediaType {
                    case .image:
                        let options = PHImageRequestOptions()
                        options.isSynchronous = true
                        options.deliveryMode = .fastFormat
                        options.resizeMode = .none
                        PHImageManager.default().requestImageData(for: assets[0], options: options) { (imageData, result, imageOrientation, dict) in
                            guard let data = imageData else {
                                return
                            }
                            var newData = data
                            //处理图片旋转的问题
                            if imageOrientation != UIImage.Orientation.up {
                                let newImage = UIImage(data: data)?.fixOrientation()
                                if newImage != nil {
                                    newData = newImage!.pngData()!
                                }
                            }
                            var fileName = ""
                            if dict?["PHImageFileURLKey"] != nil {
                                let fileURL = dict?["PHImageFileURLKey"] as! URL
                                fileName = fileURL.lastPathComponent
                            } else {
                                fileName = "\(UUID().uuidString).png"
                            }
                            let localFilePath = self.storageLocalImage(imageData: newData, fileName: fileName)
                            let msgId = self.prepareForSendImageMsg(filePath: localFilePath)
                            self.uploadFileAndSendMsg(messageId: msgId, data: newData, fileName: fileName, type: o2_im_msg_type_image)
                        }
                        break
                    default:
                        //
                        DDLogError("不支持的类型")
                        self.showError(title: "不支持的类型！")
                        break
                    }
                }
            }, completion: nil)
    }
    //临时存储本地
    private func storageLocalImage(imageData: Data, fileName: String) -> String {
        let fileTempPath = FileUtil.share.cacheDir().appendingPathComponent(fileName)
        do {
            try imageData.write(to: fileTempPath)
            return fileTempPath.path
        } catch {
            print(error.localizedDescription)
            return fileTempPath.path
        }
    }
    //发送消息前 先载入界面
    private func prepareForSendImageMsg(filePath: String) -> String {
        let body = IMMessageBodyInfo()
        body.type = o2_im_msg_type_image
        body.body = o2_im_msg_body_image
        body.fileTempPath = filePath
        let message = IMMessageInfo()
        let msgId = UUID().uuidString
        message.body = body.toJSONString()
        message.id = msgId
        message.conversationId = self.conversation?.id
        message.createPerson = O2AuthSDK.shared.myInfo()?.distinguishedName
        message.createTime = Date().formatterDate(formatter: "yyyy-MM-dd HH:mm:ss")
        //添加到界面
        self.chatMessageList.append(message)
        self.scrollMessageToBottom()
        return msgId
    }

    //发送消息前 先载入界面
    private func prepareForSendFileMsg(tempMessage: IMMessageBodyInfo) -> String {
        let message = IMMessageInfo()
        let msgId = UUID().uuidString
        message.body = tempMessage.toJSONString()
        message.id = msgId
        message.conversationId = self.conversation?.id
        message.createPerson = O2AuthSDK.shared.myInfo()?.distinguishedName
        message.createTime = Date().formatterDate(formatter: "yyyy-MM-dd HH:mm:ss")
        //添加到界面
        self.chatMessageList.append(message)
        self.scrollMessageToBottom()
        return msgId
    }

     

    //上传图片 音频 等文件到服务器并发送消息
    private func uploadFileAndSendMsg(messageId: String, data: Data, fileName: String, type: String) {
        guard let cId = self.conversation?.id else {
            return
        }
        self.viewModel.uploadFile(conversationId: cId, type: type, fileName: fileName, file: data).then { back in
            DDLogDebug("上传文件成功")
            guard let message = self.chatMessageList.first (where: { (info) -> Bool in
                return info.id == messageId
            }) else {
                DDLogDebug("没有找到对应的消息")
                return
            }
            let body = IMMessageBodyInfo.deserialize(from: message.body)
            body?.fileId = back.id
            body?.fileExtension = back.fileExtension
            body?.fileTempPath = nil
            message.body = body?.toJSONString()
            //发送消息到服务器
            self.viewModel.sendMsg(msg: message)
                .then { (result) in
                    DDLogDebug("消息 发送成功 \(result)")
                    self.viewModel.readConversation(conversationId: self.conversation?.id)
                }.catch { (error) in
                    DDLogError(error.localizedDescription)
                    self.showError(title: "发送消息失败!")
            }
        }.catch { err in
            self.showError(title: "上传错误，\(err.localizedDescription)")
        }
    }


    // MARK: - IBAction
    //点击表情按钮
    @IBAction func clickEmojiBtn(_ sender: UIButton) {
        self.isShowEmoji.toggle()
        self.view.endEditing(true)
        if self.isShowEmoji {
            //audio view 先关闭
            self.isShowAudioView = false
            self.audioBtnView.removeFromSuperview()
            //开始添加emojiBar
            self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat + self.emojiBarHeight.toCGFloat
            self.emojiBar.delegate = self
            self.emojiBar.translatesAutoresizingMaskIntoConstraints = false
            self.bottomBar.addSubview(self.emojiBar)
            let top = NSLayoutConstraint(item: self.emojiBar, attribute: .top, relatedBy: .equal, toItem: self.emojiBar.superview!, attribute: .top, multiplier: 1, constant: CGFloat(self.bottomBarHeight))
            let width = NSLayoutConstraint(item: self.emojiBar, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: SCREEN_WIDTH)
            let height = NSLayoutConstraint(item: self.emojiBar, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: self.emojiBarHeight.toCGFloat)
            NSLayoutConstraint.activate([top, width, height])
        } else {
            self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat
            self.emojiBar.removeFromSuperview()
        }
        self.view.layoutIfNeeded()
    }

    @IBAction func micBtnClick(_ sender: UIButton) {
        DDLogDebug("点击了麦克风按钮")
        self.isShowAudioView.toggle()
        self.view.endEditing(true)
        if self.isShowAudioView {
            //emoji view 先关闭
            self.isShowEmoji = false
            self.emojiBar.removeFromSuperview()
            //开始添加emojiBar
            self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat + self.emojiBarHeight.toCGFloat
            self.audioBtnView.translatesAutoresizingMaskIntoConstraints = false
            self.bottomBar.addSubview(self.audioBtnView)
            let top = NSLayoutConstraint(item: self.audioBtnView, attribute: .top, relatedBy: .equal, toItem: self.audioBtnView.superview!, attribute: .top, multiplier: 1, constant: CGFloat(self.bottomBarHeight))
            let width = NSLayoutConstraint(item: self.audioBtnView, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: SCREEN_WIDTH)
            let height = NSLayoutConstraint(item: self.audioBtnView, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: self.emojiBarHeight.toCGFloat)
            NSLayoutConstraint.activate([top, width, height])
        } else {
            self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat
            self.audioBtnView.removeFromSuperview()
        }
        self.view.layoutIfNeeded()
    }

    @IBAction func imgBtnClick(_ sender: UIButton) {
        DDLogDebug("点击了图片按钮")
        self.chooseImage()
    }
    @IBAction func cameraBtnClick(_ sender: UIButton) {
        DDLogDebug("点击了相机按钮")
        self.takePhoto(delegate: self)
    }
    @IBAction func locationBtnClick(_ sender: UIButton) {
        DDLogDebug("点击了位置按钮")
        let vc = IMLocationChooseController.openChooseLocation { (data) in
            self.sendLocationMessage(loc: data)
        }
        self.navigationController?.pushViewController(vc, animated: false)
    }


}

// MARK: - 录音delegate
extension IMChatViewController: IMChatAudioViewDelegate {
    
    func showAudioRecordingView() {
        if self.voiceIconImage == nil {
            self.voiceImageSuperView = UIView()
            self.view.addSubview(self.voiceImageSuperView!)
            self.voiceImageSuperView?.backgroundColor = UIColor(displayP3Red: 0, green: 0, blue: 0, alpha: 0.6)
             
            self.voiceImageSuperView?.snp_makeConstraints { (make) in
                make.center.equalTo(self.view)
                make.size.equalTo(CGSize(width:140, height:140))
            }
        
            self.voiceIconImage = UIImageView()
            self.voiceImageSuperView?.addSubview(self.voiceIconImage!)
            self.voiceIconImage?.snp_makeConstraints { (make) in
                make.top.left.equalTo(self.voiceImageSuperView!).inset(UIEdgeInsets(top: 20, left: 35, bottom: 0, right: 0))
                make.size.equalTo(CGSize(width: 70, height: 70))
            }
            let voiceIconTitleLabel = UILabel()
            self.voiceIocnTitleLable = voiceIconTitleLabel
            self.voiceIconImage?.addSubview(voiceIconTitleLabel)
            voiceIconTitleLabel.textColor = UIColor.white
            voiceIconTitleLabel.font = .systemFont(ofSize: 12)
            voiceIconTitleLabel.text = "松开发送，上滑取消"
            voiceIconTitleLabel.snp_makeConstraints { (make) in
                make.bottom.equalTo(self.voiceImageSuperView!).offset(-15)
                make.centerX.equalTo(self.voiceImageSuperView!)
            }
        }
        self.voiceImageSuperView?.isHidden = false
        self.voiceIconImage?.image = UIImage(named: "chat_audio_voice")
        self.voiceIocnTitleLable?.text = "松开发送，上滑取消";
       
    }
    
    func hideAudioRecordingView() {
        self.voiceImageSuperView?.isHidden = true
    }
    
    func changeRecordingView2uplide() {
        self.voiceIocnTitleLable?.text = "松开手指，取消发送";
        self.voiceIconImage?.image = UIImage(named: "chat_audio_cancel")
    }
    
    func changeRecordingView2down() {
        self.voiceIconImage?.image = UIImage(named: "chat_audio_voice")
        self.voiceIocnTitleLable?.text = "松开发送，上滑取消";
    }
    
    func sendVoice(path: String, voice: Data, duration: String) {
        let msg = IMMessageBodyInfo()
        msg.fileTempPath = path
        msg.body = o2_im_msg_body_audio
        msg.type = o2_im_msg_type_audio
        msg.audioDuration = duration
        let msgId = self.prepareForSendFileMsg(tempMessage: msg)
        let fileName = path.split("/").last ?? "MySound.ilbc"
        DDLogDebug("音频文件：\(fileName)")
        self.uploadFileAndSendMsg(messageId: msgId, data: voice, fileName: fileName, type: o2_im_msg_type_audio)
    }
    
}

// MARK: - 拍照delegate
extension IMChatViewController: UIImagePickerControllerDelegate & UINavigationControllerDelegate {

    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        if let image = info[.editedImage] as? UIImage {
            let fileName = "\(UUID().uuidString).png"
            let newData = image.pngData()!
            let localFilePath = self.storageLocalImage(imageData: newData, fileName: fileName)
            let msgId = self.prepareForSendImageMsg(filePath: localFilePath)
            self.uploadFileAndSendMsg(messageId: msgId, data: newData, fileName: fileName, type: o2_im_msg_type_image)
        } else {
            DDLogError("没有选择到图片！")
        }
        picker.dismiss(animated: true, completion: nil)
//        var newData = data
//        //处理图片旋转的问题
//        if imageOrientation != UIImage.Orientation.up {
//            let newImage = UIImage(data: data)?.fixOrientation()
//            if newImage != nil {
//                newData = newImage!.pngData()!
//            }
//        }
//        var fileName = ""
//        if dict?["PHImageFileURLKey"] != nil {
//            let fileURL = dict?["PHImageFileURLKey"] as! URL
//            fileName = fileURL.lastPathComponent
//        } else {
//            fileName = "\(UUID().uuidString).png"
//        }
    }

}

// MARK: - 图片消息点击 delegate
extension IMChatViewController: IMChatMessageDelegate {
    
    func openApplication(storyboard: String) {
        if storyboard == "mind" {
            let flutterViewController = O2FlutterViewController()
            flutterViewController.setInitialRoute("mindMap")
            self.present(flutterViewController, animated: false, completion: nil)
        }else {
            let storyBoard = UIStoryboard(name: storyboard, bundle: nil)
            guard let destVC = storyBoard.instantiateInitialViewController() else {
                return
            }
            destVC.modalPresentationStyle = .fullScreen
            if destVC.isKind(of: ZLNavigationController.self) {
                self.show(destVC, sender: nil)
            }else{
                self.navigationController?.pushViewController(destVC, animated: true)
            }
        }
    }
    
    func openWork(workId: String) {
        self.showLoading()
        self.viewModel.isWorkCompleted(work: workId).always {
            self.hideLoading()
        }.then{ result in
            if result {
                self.showMessage(msg: "工作已经完成了！")
            }else {
                self.openWorkPage(work: workId)
            }
        }.catch {_ in
            self.showMessage(msg: "工作已经完成了！")
        }
        
        
    }
    
    private func openWorkPage(work: String) {
        let storyBoard = UIStoryboard(name: "task", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
        let json = """
        {"work":"\(work)", "workCompleted":"", "title":""}
        """
        let todo = TodoTask(JSONString: json)
        destVC.todoTask = todo
        destVC.backFlag = 3 //隐藏就行
        self.show(destVC, sender: nil)
    }
    
    
    func openLocatinMap(info: IMMessageBodyInfo) {
        IMShowLocationViewController.pushShowLocation(vc: self, latitude: info.latitude, longitude: info.longitude,
                                                      address: info.address, addressDetail: info.addressDetail)
    }
    
    func clickImageMessage(info: IMMessageBodyInfo) {
        if let id = info.fileId {
            self.showLoading()
            var ext = info.fileExtension ?? "png"
            if ext.isEmpty {
                ext = "png"
            }
            O2IMFileManager.shared
                .getFileLocalUrl(fileId: id, fileExtension: ext)
                .always {
                    self.hideLoading()
                }.then { (path) in
                    let currentURL = NSURL(fileURLWithPath: path.path)
                    DDLogDebug(currentURL.description)
                    DDLogDebug(path.path)
                    if QLPreviewController.canPreview(currentURL) {
                        self.previewVC.currentFileURLS.removeAll()
                        self.previewVC.currentFileURLS.append(currentURL)
                        self.previewVC.reloadData()
                        self.pushVC(self.previewVC)
                    } else {
                        self.showError(title: "当前文件类型不支持预览！")
                    }
                }
                .catch { (error) in
                    DDLogError(error.localizedDescription)
                    self.showError(title: "获取文件异常！")
            }
        } else if let temp = info.fileTempPath {
            let currentURL = NSURL(fileURLWithPath: temp)
            DDLogDebug(currentURL.description)
            DDLogDebug(temp)
            if QLPreviewController.canPreview(currentURL) {
                self.previewVC.currentFileURLS.removeAll()
                self.previewVC.currentFileURLS.append(currentURL)
                self.previewVC.reloadData()
                self.pushVC(self.previewVC)
            } else {
                self.showError(title: "当前文件类型不支持预览！")
            }
        }
    }
}

// MARK: - 表情点击 delegate
extension IMChatViewController: IMChatEmojiBarClickDelegate {
    func clickEmoji(emoji: String) {
        DDLogDebug("发送表情消息 \(emoji)")
        self.sendEmojiMessage(emoji: emoji)
    }
}

// MARK: - tableview delegate
extension IMChatViewController: UITableViewDelegate, UITableViewDataSource {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.chatMessageList.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let msg = self.chatMessageList[indexPath.row]
        if msg.createPerson == O2AuthSDK.shared.myInfo()?.distinguishedName { //发送者
            if let cell = tableView.dequeueReusableCell(withIdentifier: "IMChatMessageSendViewCell", for: indexPath) as? IMChatMessageSendViewCell {
                cell.setContent(item: self.chatMessageList[indexPath.row])
                cell.delegate = self
                return cell
            }
        } else {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "IMChatMessageViewCell", for: indexPath) as? IMChatMessageViewCell {
                cell.setContent(item: self.chatMessageList[indexPath.row])
                cell.delegate = self
                return cell
            }
        }
        return UITableViewCell()
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let msg = self.chatMessageList[indexPath.row]
        return cellHeight(item: msg)
    }
    
    func cellHeight(item: IMMessageInfo) -> CGFloat {
        if let jsonBody = item.body, let body = IMMessageBodyInfo.deserialize(from: jsonBody){
            if body.type == o2_im_msg_type_emoji {
                 // 上边距 69 + emoji高度 + 内边距 + 底部空白高度
                 return 69 + 36 + 20 + 10
            } else if body.type == o2_im_msg_type_image {
                 // 上边距 69 + 图片高度 + 内边距 + 底部空白高度
                 return 69 + 192 + 20 + 10
            } else if o2_im_msg_type_audio == body.type {
                 // 上边距 69 + audio高度 + 内边距 + 底部空白高度
                 return 69 + IMAudioView.IMAudioView_height + 20 + 10
            } else if o2_im_msg_type_location == body.type {
                 // 上边距 69 + 位置图高度 + 内边距 + 底部空白高度
                 return 69 + IMLocationView.IMLocationViewHeight + 20 + 10
            } else {
                let size = body.body!.getSizeWithMaxWidth(fontSize: 16, maxWidth: messageWidth)
                // 上边距 69 + 文字高度 + 内边距 + 底部空白高度
                return 69 + size.height + 28 + 10
            }
        }
        return 132
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
    }

}

// MARK: - textField delegate
extension IMChatViewController: UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        DDLogDebug("准备开始输入......")
        closeOtherView()
        return true
    }

    private func closeOtherView() {
        self.isShowEmoji = false
        self.isShowAudioView = false
        self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat
        self.view.layoutIfNeeded()
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        DDLogDebug("回车。。。。")
        self.sendTextMessage()
        return true
    }
}

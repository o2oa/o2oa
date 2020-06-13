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
    
    private let emojiBarHeight = 256
    //表情窗口
    private lazy var emojiBar: IMChatEmojiBarView = {
       let view = Bundle.main.loadNibNamed("IMChatEmojiBarView", owner: self, options: nil)?.first as! IMChatEmojiBarView
        view.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: emojiBarHeight.toCGFloat)
       return view
    }()

    private lazy var viewModel: IMViewModel = {
        return IMViewModel()
    }()

    // MARK: - properties
    var conversation: IMConversationInfo? = nil
    private var chatMessageList: [IMMessageInfo] = []
    private var page = 1
    private var isShowEmoji = false
    private var bottomBarHeight = 64


    // MARK: - functions
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib(nibName: "IMChatMessageViewCell", bundle: nil), forCellReuseIdentifier: "IMChatMessageViewCell")
        self.tableView.register(UINib(nibName: "IMChatMessageSendViewCell", bundle: nil), forCellReuseIdentifier: "IMChatMessageSendViewCell")
        self.tableView.separatorStyle = .none
        self.tableView.rowHeight = UITableView.automaticDimension
        self.tableView.estimatedRowHeight = 144
        self.messageInputView.delegate = self

        //底部安全距离 老机型没有
        self.bottomBarHeight = Int(iPhoneX ? 64 + IPHONEX_BOTTOM_SAFE_HEIGHT: 64)
        self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat
        self.bottomBar.topBorder(width: 1, borderColor: base_gray_color.alpha(0.5))
        self.messageInputView.backgroundColor = base_gray_color

        //标题
        if let c = self.conversation {
            var person = ""
            c.personList?.forEach({ (p) in
                if  p != O2AuthSDK.shared.myInfo()?.distinguishedName {
                    person = p
                }
            })
            if !person.isEmpty {
                self.title = person.split("@").first ?? ""
            }
        }
        //获取聊天数据
        self.loadMsgList(page: page)
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

    //获取消息
    private func loadMsgList(page: Int) {
        if let c = self.conversation, let id = c.id {
            self.viewModel.myMsgPageList(page: page, conversationId: id).then { (list) in
                self.chatMessageList = list
                self.scrollMessageToBottom()
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
                self.tableView.scrollToRow(at: IndexPath(row: self.chatMessageList.count-1, section: 0), at: .bottom, animated: true)
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
            .then { (result)  in
                DDLogDebug("发送消息成功 \(result)")
                self.viewModel.readConversation(conversationId: self.conversation?.id)
        }.catch { (error) in
            DDLogError(error.localizedDescription)
            self.showError(title: "发送消息失败!")
        }
    }


    // MARK: - IBAction
    //点击表情按钮
    @IBAction func clickEmojiBtn(_ sender: UIButton) {
        self.isShowEmoji.toggle()
        self.view.endEditing(true)
        if self.isShowEmoji {
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
                return cell
            }
        }else {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "IMChatMessageViewCell", for: indexPath) as? IMChatMessageViewCell {
                cell.setContent(item: self.chatMessageList[indexPath.row])
                return cell
            }
        }
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
    }

}

// MARK: - textField delegate
extension IMChatViewController: UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        DDLogDebug("准备开始输入......")
        closeEmoji()
        return true
    }

    private func closeEmoji() {
        self.isShowEmoji = false
        self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat
        self.view.layoutIfNeeded()
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        DDLogDebug("回车。。。。")
        self.sendTextMessage()
        return true
    }
}

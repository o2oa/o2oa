//
//  IMConversationListViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class IMConversationListViewController: UIViewController {

    fileprivate lazy var tableview: UITableView = {
        var tableview = UITableView(frame: CGRect(x: 0, y: 0, width: self.view.width, height: self.view.height))
        tableview.delegate = self
        tableview.dataSource = self
        tableview.backgroundColor = UIColor(netHex: 0xe8edf3)
        tableview.register(UINib(nibName: "IMConversationItemCell", bundle: nil), forCellReuseIdentifier: "IMConversationItemCell")
        tableview.separatorStyle = .none
        return tableview
    }()

    fileprivate lazy var emptyView: UIView = {
        let view = UIView(frame: CGRect(x: 0, y: 36, width: self.view.width, height: self.view.height - 36))
        view.isHidden = true
        view.backgroundColor = .white
        let tips = UILabel()
        tips.text = "暂无会话"
        tips.textColor = UIColor(netHex: 0x666666)
        tips.sizeToFit()
        tips.center = CGPoint(x: view.centerX, y: view.height / 2 - 60)
        view.addSubview(tips)
        return view
    }()

    private lazy var viewModel: IMViewModel = {
        return IMViewModel()
    }()

    private var conversationList: [IMConversationInfo] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(tableview)
        view.addSubview(emptyView)
        
        NotificationCenter.default.addObserver(self, selector: #selector(receiveMessageFromWs(notice:)), name: OONotification.websocket.notificationName, object: nil)
        
         
    }
    
    override func viewWillAppear(_ animated: Bool) {
        getConversationList()
    }

    func getConversationList() {
        viewModel.myConversationList().then { (list) in
            self.conversationList = list
            DispatchQueue.main.async {
                if self.conversationList.count > 0 {
                    self.emptyView.isHidden = true
                } else {
                    self.emptyView.isHidden = false
                }
                self.tableview.reloadData()
            }

        }.catch { (err) in
            DispatchQueue.main.async { self.emptyView.isHidden = false }
        }
    }

    //接收websocket消息
    @objc private func receiveMessageFromWs(notice: Notification) {
        DDLogDebug("接收到websocket im 消息")
        if let message = notice.object as? IMMessageInfo {
            if self.conversationList.contains(where: { (info) -> Bool in
                return info.id == message.conversationId
            }) {
                DDLogDebug("有对应的会话 刷新列表")
                var newList: [IMConversationInfo]  = []
                self.conversationList.forEach { (info) in
                    if message.conversationId != nil && info.id == message.conversationId {
                        info.lastMessage = message
                        info.unreadNumber = (info.unreadNumber ?? 0) + 1
                    }
                    newList.append(info)
                }
                self.conversationList = newList
                DispatchQueue.main.async {
                    self.tableview.reloadData()
                }
            }else {
                DDLogDebug("没有对应的会话 重新获取会话列表")
                self.getConversationList()
            }
        }else {
            DDLogError("不正确的消息类型。。。")
        }
    }
    
    

}

// MARK: - tableview delegate
extension IMConversationListViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.conversationList.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "IMConversationItemCell", for: indexPath)
    }
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let c = cell as? IMConversationItemCell else {
            return
        }
        c.bindConversation(conversation: self.conversationList[indexPath.row])
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 64
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        DDLogDebug("点击了 row \(indexPath.row)")
        let chatView = IMChatViewController()
        chatView.conversation = self.conversationList[indexPath.row]
        self.navigationController?.pushViewController(chatView, animated: true)
    }
    //todo can edit

}

//
//  JCMoreResultViewController.swift
//  JChat
//
//  Created by deng on 2017/5/8.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCMoreResultViewController: UIViewController {

    var message: JMSGMessage?
    var fromUser: JMSGUser!
    weak var delegate: JCSearchResultViewController?
    
    var searchResultView: JCSearchResultViewController!
    var searchController: UISearchController!
    
    var users: [JMSGUser] = []
    var groups: [JMSGGroup] = []

    fileprivate var selectGroup: JMSGGroup!
    fileprivate var selectUser: JMSGUser!

    //MARK: - life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        navigationController?.navigationBar.isHidden = true
        if searchController != nil {
            searchController.searchBar.isHidden = false
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidAppear(animated)
        navigationController?.navigationBar.isHidden = false
    }
    
    deinit {
        searchResultView.removeObserver(self, forKeyPath: "filteredUsersArray")
        searchResultView.removeObserver(self, forKeyPath: "filteredGroupsArray")
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "filteredUsersArray" {
            users = searchResultView.filteredUsersArray
        }
        if keyPath == "filteredGroupsArray" {
            groups = searchResultView.filteredGroupsArray
        }
        tableView.reloadData()
    }
    
    fileprivate lazy var tableView: UITableView = {
        var tableView = UITableView(frame: CGRect(x: 0, y: 64, width: self.view.width, height: self.view.height - 64))
        tableView.delegate = self
        tableView.dataSource = self
        tableView.keyboardDismissMode = .onDrag
        tableView.register(JCContacterCell.self, forCellReuseIdentifier: "JCContacterCell")
        return tableView
    }()

    //MARK: - private func
    private func _init() {
        navigationController?.automaticallyAdjustsScrollViewInsets = false
        automaticallyAdjustsScrollViewInsets = false
        navigationController?.navigationBar.isHidden = true
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        view.addSubview(tableView)
        searchResultView.addObserver(self, forKeyPath: "filteredUsersArray", options: .new, context: nil)
        searchResultView.addObserver(self, forKeyPath: "filteredGroupsArray", options: .new, context: nil)
    }

    fileprivate func sendBusinessCard() {
        JCAlertView.bulid().setTitle("发送给：\(selectGroup.displayName())")
            .setMessage(fromUser!.displayName() + "的名片")
            .setDelegate(self)
            .addCancelButton("取消")
            .addButton("确定")
            .setTag(10003)
            .show()
    }

    fileprivate func forwardMessage(_ message: JMSGMessage) {

        let alertView = JCAlertView.bulid().setJMessage(message)
            .setDelegate(self)
            .setTag(10001)
        if selectUser == nil {
            alertView.setTitle("发送给：\(selectGroup.displayName())")
        } else {
            alertView.setTitle("发送给：\(selectUser.displayName())")
        }
        alertView.show()
    }
   
}

//Mark: -
extension JCMoreResultViewController: UITableViewDelegate, UITableViewDataSource {
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if users.count > 0 {
            return users.count
        }
        return groups.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 55
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "JCContacterCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let cell = cell as? JCContacterCell else {
            return
        }
        if users.count > 0 {
            cell.bindDate(users[indexPath.row])
        } else {
            cell.bindDateWithGroup(group: groups[indexPath.row])
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if users.count > 0 {
            let user = users[indexPath.row]
            selectUser = user
            if let message = message {
                forwardMessage(message)
                return
            }
            if fromUser != nil {
                sendBusinessCard()
                return
            }

            let vc: UIViewController
            if user.isEqual(to: JMSGUser.myInfo()) {
                vc = JCMyInfoViewController()
            } else {
                let v = JCUserInfoViewController()
                v.user = user
                vc = v
            }
            if searchController != nil {
                searchController.searchBar.resignFirstResponder()
                searchController.searchBar.isHidden = true
            }
            navigationController?.navigationBar.isHidden = false
            navigationController?.pushViewController(vc, animated: true)
        } else {
            let group = groups[indexPath.row]
            selectGroup = group
            if let message = message {
                forwardMessage(message)
                return
            }
            if fromUser != nil {
                sendBusinessCard()
                return
            }

            JMSGConversation.createGroupConversation(withGroupId: group.gid) { (result, error) in
                let conv = result as! JMSGConversation
                let vc = JCChatViewController(conversation: conv)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateConversation), object: nil, userInfo: nil)
                if self.searchController != nil {
                    self.searchController.searchBar.resignFirstResponder()
                    self.searchController.searchBar.isHidden = true
                }
                self.navigationController?.navigationBar.isHidden = false
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
}

extension JCMoreResultViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        if buttonIndex != 1 {
            return
        }
        switch alertView.tag {
        case 10001:
            if selectUser != nil {
                JMSGMessage.forwardMessage(message!, target: selectUser, optionalContent: JMSGOptionalContent.ex.default)
            } else {
                JMSGMessage.forwardMessage(message!, target: selectGroup, optionalContent: JMSGOptionalContent.ex.default)
            }

        case 10003:
            if selectUser != nil {
                JMSGConversation.createSingleConversation(withUsername: selectUser.username) { (result, error) in
                    if let conversation = result as? JMSGConversation {
                        let message = JMSGMessage.ex.createBusinessCardMessage(conversation, self.fromUser.username, self.fromUser.appKey ?? "")
                        JMSGMessage.send(message, optionalContent: JMSGOptionalContent.ex.default)
                    }
                }
            } else {
                let msg = JMSGMessage.ex.createBusinessCardMessage(gid: selectGroup.gid, userName: fromUser!.username, appKey: fromUser!.appKey ?? "")
                JMSGMessage.send(msg, optionalContent: JMSGOptionalContent.ex.default)
            }
        default:
            break
        }
        MBProgressHUD_JChat.show(text: "已发送", view: view, 2)

        let time: TimeInterval = 2
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.1) {
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: kReloadAllMessage), object: nil)
        }
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + time) { [weak self] in
            self?.delegate?.close()
        }
    }
}

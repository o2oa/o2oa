//
//  JCSearchResultViewController.swift
//  JChat
//
//  Created by deng on 2017/5/5.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCSearchResultViewController: UIViewController {

    var message: JMSGMessage?
    var fromUser: JMSGUser!
    weak var delegate: UIViewController?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        navigationController?.isNavigationBarHidden = true
        if searchController != nil {
            searchController.searchBar.isHidden = false
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.isNavigationBarHidden = false
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    var searchController: UISearchController!
    fileprivate lazy var tableView: UITableView = {
        var tableView = UITableView(frame: .zero, style: .grouped)
        tableView.keyboardDismissMode = .onDrag
        tableView.delegate = self
        tableView.dataSource = self
        tableView.sectionIndexBackgroundColor = .clear
        tableView.register(JCContacterCell.self, forCellReuseIdentifier: "JCContacterCell")
        tableView.frame = CGRect(x: 0, y: 64, width: self.view.width, height: self.view.height - 64)
        return tableView
    }()
    fileprivate lazy var tagArray = ["联系人", "群组"]
    fileprivate lazy var users: [JMSGUser] = []
    fileprivate lazy var groups: [JMSGGroup] = []
    
    dynamic lazy var filteredUsersArray: [JMSGUser] = []
    dynamic lazy var filteredGroupsArray: [JMSGGroup] = []
    
    fileprivate var searchString = ""
    
    private lazy var tipsLabel: UILabel = UILabel(frame: CGRect(x: 0, y: 100, width: self.view.width, height: 22.5))
    private lazy var networkErrorView: UIView = {
        let tipsView = UIView(frame: CGRect(x: 0, y: 64, width: self.view.width, height: self.view.height))
        var tipsLabel: UILabel = UILabel(frame: CGRect(x: 0, y: 100, width: tipsView.width, height: 22.5))
        tipsLabel.textColor = UIColor(netHex: 0x999999)
        tipsLabel.textAlignment = .center
        tipsLabel.font = UIFont.systemFont(ofSize: 16)
        tipsLabel.text = "无法连接网络"
        tipsView.addSubview(tipsLabel)
        tipsView.isHidden = true
        tipsView.backgroundColor = .white
        return tipsView
    }()
    fileprivate var selectGroup: JMSGGroup!
    fileprivate var selectUser: JMSGUser!
    
    private func _init() {
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        automaticallyAdjustsScrollViewInsets = false
        navigationController?.automaticallyAdjustsScrollViewInsets = false
        
        tipsLabel.font = UIFont.systemFont(ofSize: 16)
        tipsLabel.textColor = UIColor(netHex: 0x999999)
        tipsLabel.textAlignment = .center
        view.addSubview(tipsLabel)
        
        _getDate()

        view.addSubview(tableView)
        view.addSubview(networkErrorView)
        
        if JCNetworkManager.isNotReachable {
            networkErrorView.isHidden = false
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(note:)), name: NSNotification.Name(rawValue: "kNetworkReachabilityChangedNotification"), object: nil)
    }
    
    func reachabilityChanged(note: NSNotification) {
        if let curReach = note.object as? Reachability {
            let status = curReach.currentReachabilityStatus()
            switch status {
            case NotReachable:
                networkErrorView.isHidden = false
            default :
                networkErrorView.isHidden = true
            }
        }
    }
    
    private func _getDate() {
        users.removeAll()
        groups.removeAll()
        
        JMSGConversation.allConversations { (result, error) in
            if error == nil {
                if let conversations = result as? [JMSGConversation] {
                    for conv in conversations {
                        if !conv.ex.isGroup {
                            let user = conv.target as! JMSGUser
                            self.users.append(user)
                        }
                    }
                    if !self.searchString.isEmpty {
                        self.filter(self.searchString)
                    }
                }
            }
            
            JMSGFriendManager.getFriendList { (result, error) in
                if error == nil {
                    let users = result as! [JMSGUser]
                    for user in users {
                        if !self.users.contains(user) {
                            self.users.append(user)
                        }
                    }
                    
                    if !self.searchString.isEmpty {
                        self.filter(self.searchString)
                    }
                }
            }
            
            JMSGGroup.myGroupArray { (result, error) in
                if error != nil {
                    return
                }
                for item in result as! [NSNumber] {
                    JMSGGroup.groupInfo(withGroupId: "\(item)", completionHandler: { (result, error) in
                        guard let group = result as? JMSGGroup else {
                            return
                        }
                        self.groups.append(group)
                        if !self.searchString.isEmpty {
                            self.filter(self.searchString)
                        }
                    })
                }
            }
        }
        
        
    }
    
    fileprivate func filter(_ searchString: String) {
        if searchString.isEmpty || searchString == "" {
            return
        }
        filteredUsersArray = _JCFilterUsers(users: users, string: searchString)
        filteredGroupsArray = _JCFilterGroups(groups: groups, string: searchString)
        
        if filteredUsersArray.count == 0 && filteredGroupsArray.count == 0 {
            tableView.isHidden = true
            
            let attr = NSMutableAttributedString(string: "没有搜到 ")
            let attrSearchString = NSAttributedString(string: searchString, attributes: [ NSAttributedString.Key.foregroundColor : O2ThemeManager.color(for: "Base.base_color")!, NSAttributedString.Key.font : UIFont.boldSystemFont(ofSize: 16.0)])
            
            attr.append(attrSearchString)
            attr.append(NSAttributedString(string:  " 相关的信息"))
            tipsLabel.attributedText = attr
            return
        } else {
            tableView.isHidden = false
        }
        
        tableView.reloadData()
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

    public func close() {
        searchController.isActive = false
        delegate?.dismiss(animated: true, completion: nil)
    }
}

extension JCSearchResultViewController: UIAlertViewDelegate {
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
            self?.close()
        }
    }
}

//Mark: -
extension JCSearchResultViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        if filteredUsersArray.count > 0 && filteredGroupsArray.count > 0 {
            return 2
        }
        if filteredUsersArray.count == 0 && filteredGroupsArray.count == 0 {
            return 0
        }
        return 1
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let userCount = filteredUsersArray.count > 3 ? 4 : filteredUsersArray.count
        let groupCount = filteredGroupsArray.count > 3 ? 4 : filteredGroupsArray.count
        if section == 0 && userCount > 0 {
            return userCount
        } else if groupCount > 0 {
            return groupCount
        }
        
        if userCount > 0 && groupCount > 0 {
            if section == 0 {
                return userCount
            } else {
                return groupCount
            }
        }
        
        return 0
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if filteredUsersArray.count <= 0 {
            return tagArray.last
        }
        return tagArray[section]
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.row == 3 {
            return 40
        }
        return 55
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 35
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.001
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 3 {
            var cell = tableView.dequeueReusableCell(withIdentifier: "JCMoreCell")
            if cell == nil {
                cell = UITableViewCell(style: .value2, reuseIdentifier: "JCMoreCell")
            }
            return cell!
        }
        return tableView.dequeueReusableCell(withIdentifier: "JCContacterCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        var isUser = false
        if indexPath.section == 0 && filteredUsersArray.count > 0 {
            isUser = true
        }
        if indexPath.row == 3 {
            cell.detailTextLabel?.text = isUser ? "查看更多联系人" : "查看更多群组"
            cell.detailTextLabel?.textColor = UIColor(netHex: 0x999999)
            cell.accessoryType = .disclosureIndicator
            return
        }
        guard let cell = cell as? JCContacterCell else {
            return
        }
        if isUser {
            cell.bindDate(filteredUsersArray[indexPath.row])
        } else {
            cell.bindDateWithGroup(group: filteredGroupsArray[indexPath.row])
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        var isUser = false
        if indexPath.section == 0 && filteredUsersArray.count > 0 {
            isUser = true
        }
        
        if indexPath.row == 3 {
            let vc = JCMoreResultViewController()
            vc.fromUser = fromUser
            vc.message = message
            vc.delegate = self
            if isUser {
                vc.users = filteredUsersArray
            } else {
                vc.groups = filteredGroupsArray
            }
            vc.searchResultView = self
            vc.searchController = self.searchController
            if searchController != nil {
                searchController.searchBar.resignFirstResponder()
            }
            navigationController?.pushViewController(vc, animated: true)
            return
        }
        
        if isUser {
            let user = filteredUsersArray[indexPath.row]
            selectUser = user
            if let message = message {
                forwardMessage(message)
                return
            }
            if fromUser != nil {
                sendBusinessCard()
                return
            }
            let vc = JCUserInfoViewController()
            vc.user = user
            if searchController != nil {
                searchController.searchBar.resignFirstResponder()
                searchController.searchBar.isHidden = true
            }
            navigationController?.pushViewController(vc, animated: true)
        } else {
            let group = filteredGroupsArray[indexPath.row]
            selectGroup = group
            if let message = self.message {
                forwardMessage(message)
                return
            }
            if self.fromUser != nil {
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

                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
}

extension JCSearchResultViewController: UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        self.searchController = searchController
        searchString = searchController.searchBar.text!
        filter(searchString)
    }
}

@inline(__always)
internal func _JCFilterUsers(users: [JMSGUser], string: String) -> [JMSGUser] {
    let filteredUsersArray = users.filter( { (user: JMSGUser) -> Bool in
        let str = string.uppercased()
        let notename = user.noteName?.uppercased().contains(str) ?? false
        let nickname = user.nickname?.uppercased().contains(str) ?? false
        let username = user.username.uppercased().contains(str)
        if notename || nickname || username {
            return true
        } else {
            return false
        }
    })
    return filteredUsersArray
}

@inline(__always)
internal func _JCFilterGroups(groups: [JMSGGroup], string: String) -> [JMSGGroup] {
    let filteredGroupsArray = groups.filter( { (group: JMSGGroup) -> Bool in
        let str = string.uppercased()
        if group.name?.uppercased().contains(str) ?? false ||
            group.gid.uppercased().contains(str) {
            return true
        } else {
            return false
        }
    })
    return filteredGroupsArray
}

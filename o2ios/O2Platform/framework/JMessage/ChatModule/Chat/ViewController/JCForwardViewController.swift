//
//  JCForwardViewController.swift
//  JChat
//
//  Created by deng on 2017/7/17.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCForwardViewController: UIViewController {
    
    var message: JMSGMessage?
    var fromUser: JMSGUser!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    private lazy var cancelButton = UIButton(frame: CGRect(x: 0, y: 0, width: 36, height: 36))
    
    fileprivate lazy var contacterView: UITableView = {
        var contacterView = UITableView(frame: .zero, style: .grouped)
        contacterView.delegate = self
        contacterView.dataSource = self
        contacterView.separatorStyle = .none
        contacterView.sectionIndexColor = UIColor(netHex: 0x2dd0cf)
        contacterView.sectionIndexBackgroundColor = .clear
        contacterView.register(JCContacterCell.self, forCellReuseIdentifier: "JCContacterCell")
        contacterView.frame = CGRect(x: 0, y: 0, width: self.view.width, height: self.view.height)
        return contacterView
    }()
    let searchResultVC = JCSearchResultViewController()
    private lazy var searchController: JCSearchController = JCSearchController(searchResultsController: JCNavigationController(rootViewController: self.searchResultVC))
    private lazy var searchView: UIView = UIView(frame: CGRect(x: 0, y: 0, width: self.view.width, height: 31))
    fileprivate var badgeCount = 0
    
    fileprivate lazy var tagArray = ["群组"]
    fileprivate lazy var users: [JMSGUser] = []
    
    fileprivate lazy var keys: [String] = []
    fileprivate lazy var data: Dictionary<String, [JMSGUser]> = Dictionary()
    
    fileprivate var selectUser: JMSGUser!

    private func _init() {
        if message == nil {
            self.title = "发送名片"
        } else {
            self.title = "转发"
        }

        searchResultVC.message = message
        searchResultVC.fromUser = fromUser
        searchResultVC.delegate = self

        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        _setupNavigation()
        
        let nav = searchController.searchResultsController as! JCNavigationController
        let vc = nav.topViewController as! JCSearchResultViewController
        searchController.delegate = self
        searchController.searchResultsUpdater = vc
        
        searchView.addSubview(searchController.searchBar)
        contacterView.tableHeaderView = searchView

        view.addSubview(contacterView)
        
        _getFriends()
    }
    
    private func _setupNavigation() {
        cancelButton.addTarget(self, action: #selector(_clickNavleftButton), for: .touchUpInside)
        cancelButton.setTitle("取消", for: .normal)
        cancelButton.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        let item = UIBarButtonItem(customView: cancelButton)
        navigationItem.leftBarButtonItem = item
    }
    
    func _clickNavleftButton() {
        dismiss(animated: true, completion: nil)
    }
    
    func _updateUserInfo() {
        let users = self.users
        _classify(users)
        contacterView.reloadData()
    }
    
    func _classify(_ users: [JMSGUser]) {
        self.users = users
        keys.removeAll()
        data.removeAll()
        for item in users {
            var key = item.displayName().firstCharacter()
            if !key.isLetterOrNum() {
                key = "#"
            }
            var array = data[key]
            if array == nil {
                array = [item]
            } else {
                array?.append(item)
            }
            if !keys.contains(key) {
                keys.append(key)
            }
            data[key] = array
        }
        keys = keys.sortedKeys()
    }
    
    func _getFriends() {
        JMSGFriendManager.getFriendList { (result, error) in
            if let users = result as? [JMSGUser] {
                self._classify(users)
                self.contacterView.reloadData()
            }
        }
    }

}

//Mark: -
extension JCForwardViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        if users.count > 0 {
            return keys.count + 1
        }
        return 1
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return tagArray.count
        }
        return data[keys[section - 1]]!.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 0 {
            return ""
        }
        return keys[section - 1]
    }
    
    func sectionIndexTitles(for tableView: UITableView) -> [String]? {
        return keys
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 55
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 {
            return 5
        }
        return 10
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "JCContacterCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let cell = cell as? JCContacterCell else {
            return
        }
        if indexPath.section == 0 {
            switch indexPath.row {
            case 0:
                cell.title = "群组"
                cell.icon = UIImage.loadImage("com_icon_group_36")
                cell.isShowBadge = false
            default:
                break
            }
            return
        }
        let user = data[keys[indexPath.section - 1]]?[indexPath.row]
        cell.isShowBadge = false
        cell.bindDate(user!)
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if indexPath.section == 0 {
            let vc = JCGroupListViewController()
            vc.message = message
            vc.fromUser = fromUser
            navigationController?.pushViewController(vc, animated: true)
            return
        }
        selectUser = data[keys[indexPath.section - 1]]?[indexPath.row]
        if let message = message {
            forwardMessage(message)
        } else {
            sendBusinessCard()
        }
        
    }
    
    private func sendBusinessCard() {
        fromUser = fromUser ?? JMSGUser.myInfo()
        JCAlertView.bulid().setTitle("发送给：\(selectUser.displayName())")
            .setMessage(fromUser.displayName() + "的名片")
            .setDelegate(self)
            .addCancelButton("取消")
            .addButton("确定")
            .setTag(10003)
            .show()
    }
    
    private func forwardMessage(_ message: JMSGMessage) {
        JCAlertView.bulid().setJMessage(message)
            .setTitle("发送给：\(selectUser.displayName())")
            .setDelegate(self)
            .setTag(10001)
            .show()
    }
}

extension JCForwardViewController: UISearchControllerDelegate {
    func willPresentSearchController(_ searchController: UISearchController) {
        contacterView.isHidden = true
    }
    func willDismissSearchController(_ searchController: UISearchController) {
        contacterView.isHidden = false
        let nav = searchController.searchResultsController as! JCNavigationController
        nav.isNavigationBarHidden = true
        nav.popToRootViewController(animated: false)
    }
}

extension JCForwardViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        if buttonIndex != 1 {
            return
        }
        switch alertView.tag {
        case 10001:
            JMSGMessage.forwardMessage(message!, target: selectUser, optionalContent: JMSGOptionalContent.ex.default)
            
        case 10003:
            JMSGConversation.createSingleConversation(withUsername: selectUser.username) { (result, error) in
                if let conversation = result as? JMSGConversation {
                    let message = JMSGMessage.ex.createBusinessCardMessage(conversation, self.fromUser.username, self.fromUser.appKey ?? "")
                    JMSGMessage.send(message, optionalContent: JMSGOptionalContent.ex.default)
                }
            }
        default:
            break
        }
        MBProgressHUD_JChat.show(text: "已发送", view: view, 2)

        let time: TimeInterval = 2
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.2) {
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: kReloadAllMessage), object: nil)
        }
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + time) { [weak self] in
            self?.dismiss(animated: true, completion: nil)
        }
    }
}

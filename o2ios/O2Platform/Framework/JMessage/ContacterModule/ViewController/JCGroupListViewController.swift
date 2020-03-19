//
//  JCGroupListViewController.swift
//  JChat
//
//  Created by deng on 2017/3/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCGroupListViewController: UITableViewController {

    var message: JMSGMessage?
    var fromUser: JMSGUser?

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    var groupList: [JMSGGroup] = []
    private lazy var defaultImage: UIImage? = UIImage.loadImage("com_icon_group_36")
    fileprivate var selectGroup: JMSGGroup!

    // MARK: - private func
    private func _init() {
        self.title = "群组"
        view.backgroundColor = .white
        tableView.separatorStyle = .none
        tableView.register(JCTableViewCell.self, forCellReuseIdentifier: "JCGroupListCell")
        _getGroupList()
    }
    
    private func _getGroupList() {
        MBProgressHUD_JChat.showMessage(message: "加载中...", toView: tableView)
        JMSGGroup.myGroupArray { (result, error) in
            if error == nil {
                self.groupList.removeAll()
                let gids = result as! [NSNumber]
                if gids.count == 0 {
                    MBProgressHUD_JChat.hide(forView: self.tableView, animated: true)
                    return
                }
                for gid in gids {
                    JMSGGroup.groupInfo(withGroupId: "\(gid)", completionHandler: { (result, error) in
                        guard let group = result as? JMSGGroup else {
                            return
                        } 
                        self.groupList.append(group)
                        if self.groupList.count == gids.count {
                            MBProgressHUD_JChat.hide(forView: self.tableView, animated: true)
                            self.groupList = self.groupList.sorted(by: { (g1, g2) -> Bool in
                                return g1.displayName().firstCharacter() < g2.displayName().firstCharacter()
                            })
                            self.tableView.reloadData()
                        }
                    })
                }
            } else {
                MBProgressHUD_JChat.hide(forView: self.tableView, animated: true)
                MBProgressHUD_JChat.show(text: "加载失败", view: self.view)
            }
        }
        
    }

    // MARK: - Table view data source
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return groupList.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "JCGroupListCell", for: indexPath)
    }
    
    override func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        let group = groupList[indexPath.row]
        cell.textLabel?.text = group.displayName()
        cell.imageView?.image = defaultImage
        group.largeAvatarData { (data, _, _) in
            if let data = data {
                cell.imageView?.image = UIImage(data: data)?.resizeImage(CGSize(width: 36, height: 36))
            }
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 55
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let group = groupList[indexPath.row]
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
            if let conv = result as? JMSGConversation {
                let vc = JCChatViewController(conversation: conv)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateConversation), object: nil, userInfo: nil)
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }

    private func sendBusinessCard() {
        JCAlertView.bulid().setTitle("发送给：\(selectGroup.displayName())")
            .setMessage(fromUser!.displayName() + "的名片")
            .setDelegate(self)
            .addCancelButton("取消")
            .addButton("确定")
            .setTag(10003)
            .show()
    }

    private func forwardMessage(_ message: JMSGMessage) {
        JCAlertView.bulid().setJMessage(message)
            .setTitle("发送给：\(selectGroup.displayName())")
            .setDelegate(self)
            .setTag(10001)
            .show()
    }

}

extension JCGroupListViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        if buttonIndex != 1 {
            return
        }
        switch alertView.tag {
        case 10001:
            JMSGMessage.forwardMessage(message!, target: selectGroup, optionalContent: JMSGOptionalContent.ex.default)

        case 10003:
            let msg = JMSGMessage.ex.createBusinessCardMessage(gid: selectGroup.gid, userName: fromUser!.username, appKey: fromUser?.appKey ?? "")
            JMSGMessage.send(msg, optionalContent: JMSGOptionalContent.ex.default)

        default:
            break
        }
        MBProgressHUD_JChat.show(text: "已发送", view: view, 2)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.1) {
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: kReloadAllMessage), object: nil)
        }
        weak var weakSelf = self
        let time: TimeInterval = 2
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + time) {
            weakSelf?.dismiss(animated: true, completion: nil)
        }
    }
}

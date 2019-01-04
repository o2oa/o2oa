//
//  JCGroupSettingViewController.swift
//  JChat
//
//  Created by deng on 2017/4/27.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCGroupSettingViewController: UIViewController, CustomNavigation {
    
    var group: JMSGGroup!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    private var tableView: UITableView = UITableView(frame: .zero, style: .grouped)
    fileprivate var memberCount = 0
    fileprivate lazy var users: [JMSGUser] = []
    fileprivate var isMyGroup = false
    fileprivate var isNeedUpdate = false

    //MARK: - private func
    private func _init() {
        self.title = "群组信息"
        view.backgroundColor = .white

        users = group.memberArray()
        memberCount = users.count
        
        let user = JMSGUser.myInfo()
//        && group.ownerAppKey == user.appKey!  这里group.ownerAppKey == "" 目测sdk bug
        if group.owner == user.username  {
            isMyGroup = true
        }
        
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.sectionIndexColor = UIColor(netHex: 0x2dd0cf)
        tableView.sectionIndexBackgroundColor = .clear
        tableView.register(JCButtonCell.self, forCellReuseIdentifier: "JCButtonCell")
        tableView.register(JCMineInfoCell.self, forCellReuseIdentifier: "JCMineInfoCell")
        tableView.register(GroupAvatorCell.self, forCellReuseIdentifier: "GroupAvatorCell")
        tableView.frame = CGRect(x: 0, y: 0, width: view.width, height: view.height)
        view.addSubview(tableView)
        
        customLeftBarButton(delegate: self)
        
        JMSGGroup.groupInfo(withGroupId: group.gid) { (result, error) in
            if error == nil {
                guard let group = result as? JMSGGroup else {
                    return
                }
                self.group = group
                self.isNeedUpdate = true
                self._updateGroupInfo()
            }
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(_updateGroupInfo), name: NSNotification.Name(rawValue: kUpdateGroupInfo), object: nil)
    }
    
    @objc func _updateGroupInfo() {
        if !isNeedUpdate {
            let conv = JMSGConversation.groupConversation(withGroupId: group.gid)
            group = conv?.target as! JMSGGroup
        }
        if group.memberArray().count != memberCount {
            isNeedUpdate = true
            memberCount = group.memberArray().count
        }
        users = group.memberArray()
        memberCount = users.count
        tableView.reloadData()
    }
    
}

extension JCGroupSettingViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        
        return 4
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        switch section {
        case 0:
            return 1
        case 1:
            return 3
        case 2:
//            return 5
            return 4
        case 3:
            return 1
        default:
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch indexPath.section {
        case 0:
            if isMyGroup {
                if memberCount > 13 {
                    return 314
                }
                if memberCount > 8 {
                    return 260
                }
                if memberCount > 3 {
                    return 200
                }
                return 100
            } else {
                if memberCount > 14 {
                    return 314
                }
                if memberCount > 9 {
                    return 260
                }
                if memberCount > 4 {
                    return 200
                }
                return 100
            }
            
        case 1:
            return 45
        case 2:
            return 40
        default:
            return 45
        }
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 {
            return 0.0001
        }
        return 10
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            var cell = tableView.dequeueReusableCell(withIdentifier: "JCGroupSettingCell") as? JCGroupSettingCell
            if isNeedUpdate {
                cell = JCGroupSettingCell(style: .default, reuseIdentifier: "JCGroupSettingCell", group: self.group)
                isNeedUpdate = false
            }
            if cell == nil {
                cell = JCGroupSettingCell(style: .default, reuseIdentifier: "JCGroupSettingCell", group: self.group)
            }
            return cell!
        }
        if indexPath.section == 3 {
            return tableView.dequeueReusableCell(withIdentifier: "JCButtonCell", for: indexPath)
        }
        if indexPath.section == 1 && indexPath.row == 0 {
            return tableView.dequeueReusableCell(withIdentifier: "GroupAvatorCell", for: indexPath)
        }
        return tableView.dequeueReusableCell(withIdentifier: "JCMineInfoCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        cell.selectionStyle = .none
        if indexPath.section == 3 {
            guard let cell = cell as? JCButtonCell else {
                return
            }
            cell.buttonColor = UIColor(netHex: 0xEB424D)
            cell.buttonTitle = "退出此群"
            cell.delegate = self
            return
        }
        cell.accessoryType = .disclosureIndicator
        if indexPath.section == 0 {
            guard let cell = cell as? JCGroupSettingCell else {
                return
            }
            cell.bindData(self.group)
            cell.delegate = self
            cell.accessoryType = .none
            return
        }

        if let cell = cell as? GroupAvatorCell {
            cell.title = "群头像"
            cell.bindData(group)
        }

        guard let cell = cell as? JCMineInfoCell else {
            return
        }
        if indexPath.section == 2 {
            if indexPath.row == 1 {
                cell.delegate = self
                cell.indexPate = indexPath
                cell.accessoryType = .none
                cell.isSwitchOn = group.isNoDisturb
                cell.isShowSwitch = true
            }
            if indexPath.row == 2 {
                cell.delegate = self
                cell.indexPate = indexPath
                cell.accessoryType = .none
                cell.isSwitchOn = group.isShieldMessage
                cell.isShowSwitch = true
            }
        }
        if indexPath.section == 1 {
            let conv = JMSGConversation.groupConversation(withGroupId: self.group.gid)
            let group = conv?.target as! JMSGGroup
            switch indexPath.row {
            case 1:
                cell.title = "群聊名称"
                cell.detail = group.displayName()
            case 2:
                cell.title = "群描述"
                cell.detail = group.desc
            default:
                break
            }
        } else {
            switch indexPath.row {
            case 0:
                cell.title = "聊天文件"
            case 1:
                cell.title = "消息免打扰"
            case 2:
                cell.title = "消息屏蔽"
//            case 2:
//                cell.title = "清理缓存"
            case 3:
                cell.title = "清空聊天记录"
            default:
                break
            }
        }
        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if indexPath.section == 1 {
            switch indexPath.row {
            case 0:
                let vc = GroupAvatorViewController()
                vc.group = group
                navigationController?.pushViewController(vc, animated: true)
            case 1:
                let vc = JCGroupNameViewController()
                vc.group = group
                navigationController?.pushViewController(vc, animated: true)
            case 2:
                let vc = JCGroupDescViewController()
                vc.group = group
                navigationController?.pushViewController(vc, animated: true)
            default:
                break
            }
        }
        
        if indexPath.section == 2 {
            switch indexPath.row {
//            case 2:
//                let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "清理缓存")
//                actionSheet.tag = 1001
//                actionSheet.show(in: self.view)
            case 0:
                let vc = FileManagerViewController()
                let conv = JMSGConversation.groupConversation(withGroupId: group.gid)
                vc.conversation  = conv
                navigationController?.pushViewController(vc, animated: true)
            case 3:
                let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "清空聊天记录")
                actionSheet.tag = 1001
                actionSheet.show(in: self.view)
            default:
                break
            }
        }
    }
}

extension JCGroupSettingViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        switch buttonIndex {
        case 1:
            MBProgressHUD_JChat.showMessage(message: "退出中...", toView: self.view)
            group.exit({ (result, error) in
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    self.navigationController?.popToRootViewController(animated: true)
                } else {
                    MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
                }
            })
        default:
            break
        }
    }
}

extension JCGroupSettingViewController: JCMineInfoCellDelegate {
    func mineInfoCell(clickSwitchButton button: UISwitch, indexPath: IndexPath?) {
        if indexPath != nil {
            switch (indexPath?.row)! {
            case 1:
                if group.isNoDisturb == button.isOn {
                    return
                }
                // 消息免打扰
                group.setIsNoDisturb(button.isOn, handler: { (result, error) in
                    MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                    if error != nil {
                        button.isOn = !button.isOn
                        MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
                    }
                })
            case 2:
                if group.isShieldMessage == button.isOn {
                    return
                }
                // 消息屏蔽
                group.setIsShield(button.isOn, handler: { (result, error) in
                    MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                    if error != nil {
                        button.isOn = !button.isOn
                        MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
                    }
                })
            default:
                break
            }
        }
    }
}

extension JCGroupSettingViewController: JCButtonCellDelegate {
    func buttonCell(clickButton button: UIButton) {
        let alertView = UIAlertView(title: "退出此群", message: "确定要退出此群？", delegate: self, cancelButtonTitle: "取消", otherButtonTitles: "确定")
        alertView.show()
    }
}

extension JCGroupSettingViewController: JCGroupSettingCellDelegate {
    func clickMoreButton(clickButton button: UIButton) {
        let vc = JCGroupMembersViewController()
        vc.group = self.group
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    //增加群组成员 -- 暂不实现
    func clickAddCell(cell: JCGroupSettingCell) {
//        let vc = JCUpdateMemberViewController()
//        vc.group = group
//        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    //删除群组成员 -- 暂不实现
    func clickRemoveCell(cell: JCGroupSettingCell) {
//        let vc = JCRemoveMemberViewController()
//        vc.group = group
//        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    func didSelectCell(cell: JCGroupSettingCell, indexPath: IndexPath) {
        let index = indexPath.section * 5 + indexPath.row
        let user = users[index]
        if user.isEqual(to: JMSGUser.myInfo()) {
//            navigationController?.pushViewController(JCMyInfoViewController(), animated: true)
            navigationController?.pushViewController(SPersonViewController(), animated: true)
            return
        }
//        let vc = JCUserInfoViewController()
//        vc.user = user
//        navigationController?.pushViewController(vc, animated: true)
        let storyBoard = UIStoryboard(name: "contacts", bundle: nil)
        guard let personVC = storyBoard.instantiateViewController(withIdentifier: "ContactPersonInfoV2") as?  ContactPersonInfoV2ViewController else {
            return
        }
        let person = PersonV2()
        person.id = user.username
        personVC.person = person
        navigationController?.pushViewController(personVC, animated: true)
        
    }
}

extension JCGroupSettingViewController: UIActionSheetDelegate {
    func actionSheet(_ actionSheet: UIActionSheet, clickedButtonAt buttonIndex: Int) {
//        if actionSheet.tag == 1001 {
//            // SDK 暂无该功能
//        }
        
        if actionSheet.tag == 1001 {
            if buttonIndex == 1 {
                let conv = JMSGConversation.groupConversation(withGroupId: group.gid)
                conv?.deleteAllMessages()
                NotificationCenter.default.post(name: Notification.Name(rawValue: kDeleteAllMessage), object: nil)
                MBProgressHUD_JChat.show(text: "成功清空", view: self.view)
            }
        }
    }
}

extension JCGroupSettingViewController: UIGestureRecognizerDelegate {
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        return true
    }
}

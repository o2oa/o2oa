//
//  JCSingleSettingViewController.swift
//  JChat
//
//  Created by deng on 2017/4/5.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCSingleSettingViewController: UIViewController, CustomNavigation {
    
    var user: JMSGUser!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    private lazy var tableView: UITableView = {
        var tableView = UITableView(frame: CGRect(x: 0, y: 0, width: self.view.width, height: self.view.height), style: .grouped)
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.sectionIndexColor = UIColor(netHex: 0x2dd0cf)
        tableView.sectionIndexBackgroundColor = .clear
        tableView.register(JCSingleSettingCell.self, forCellReuseIdentifier: "JCSingleSettingCell")
        tableView.register(JCButtonCell.self, forCellReuseIdentifier: "JCButtonCell")
        tableView.register(JCMineInfoCell.self, forCellReuseIdentifier: "JCMineInfoCell")
        return tableView
    }()
    
    fileprivate lazy var leftButton = UIButton(frame: CGRect(x: 0, y: 0, width: 60, height: 65 / 3))
    
    //MARK: - private func 
    private func _init() {
        self.title = "聊天设置"
        view.backgroundColor = .white

        view.addSubview(tableView)
        customLeftBarButton(delegate: self)
    }
}

extension JCSingleSettingViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        switch section {
        case 0:
            return 1
        case 1:
            return 3
        case 2:
            return 1
        default:
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch indexPath.section {
        case 0:
            return 105
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
            return tableView.dequeueReusableCell(withIdentifier: "JCSingleSettingCell", for: indexPath)
        }
        if indexPath.section == 2 {
            return tableView.dequeueReusableCell(withIdentifier: "JCButtonCell", for: indexPath)
        }
        return tableView.dequeueReusableCell(withIdentifier: "JCMineInfoCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        cell.selectionStyle = .none
        if indexPath.section == 2 {
            guard let cell = cell as? JCButtonCell else {
                return
            }
            if !user.isFriend {
                cell.buttonColor = O2ThemeManager.color(for: "Base.base_color")!
                cell.buttonTitle = "添加好友"
                cell.delegate = self
            } else {
                cell.buttonColor = O2ThemeManager.color(for: "Base.base_color")!
                cell.buttonTitle = "删除好友"
                cell.delegate = self
            }
            return
        }
        cell.accessoryType = .disclosureIndicator
        if indexPath.section == 0 {
            guard let cell = cell as? JCSingleSettingCell else {
                return
            }
            cell.bindData(user)
            cell.delegate = self
            cell.accessoryType = .none
            return
        }
        guard let cell = cell as? JCMineInfoCell else {
            return
        }
        if indexPath.section == 1 && indexPath.row == 1 {
            cell.delegate = self
            cell.accessoryType = .none
            cell.isShowSwitch = true
        }
        switch indexPath.row {
        case 0:
            cell.title = "聊天文件"
        case 1:
            cell.isSwitchOn = user.isNoDisturb
            cell.title = "消息免打扰"
//        case 1:
//            cell.isSwitchOn = JMessage.isSetGlobalNoDisturb()
//            cell.title = "清理缓存"
        case 2:
            cell.title = "清空聊天记录"
        default:
            break
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if indexPath.section == 1 {
            switch indexPath.row {
            case 0:
                let vc = FileManagerViewController()
                let conv = JMSGConversation.singleConversation(withUsername: user.username)
                vc.conversation  = conv
                navigationController?.pushViewController(vc, animated: true)
            case 2:
                let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "清空聊天记录")
                actionSheet.tag = 1001
                actionSheet.show(in: view)
            case 3:
                break
            default:
                break
            }
        }
    }
}

extension JCSingleSettingViewController: UIActionSheetDelegate {
    func actionSheet(_ actionSheet: UIActionSheet, clickedButtonAt buttonIndex: Int) {
        //        if actionSheet.tag == 1001 {
        //            // SDK 暂无该功能
        //        }
        
        if actionSheet.tag == 1001 {
            if buttonIndex == 1 {
                let conv = JMSGConversation.singleConversation(withUsername: user.username)
                conv?.deleteAllMessages()
                NotificationCenter.default.post(name: Notification.Name(rawValue: kDeleteAllMessage), object: nil)
                MBProgressHUD_JChat.show(text: "成功清空", view: view)
            }
        }
    }
    
}

extension JCSingleSettingViewController: JCMineInfoCellDelegate {
    func mineInfoCell(clickSwitchButton button: UISwitch, indexPath: IndexPath?) {
        if user.isNoDisturb != button.isOn {
            MBProgressHUD_JChat.showMessage(message: "修改中", toView: view)
            user.setIsNoDisturb(button.isOn, handler: { (result, error) in
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    MBProgressHUD_JChat.show(text: "修改成功", view: self.view)
                } else {
                    MBProgressHUD_JChat.show(text: "修改失败", view: self.view)
                }
            })
        }
    }
}

extension JCSingleSettingViewController: JCButtonCellDelegate {
    func buttonCell(clickButton button: UIButton) {
        if user.isFriend {
            let alertView = UIAlertView(title: "删除好友", message: "是否确认删除该好友？", delegate: self, cancelButtonTitle: "取消", otherButtonTitles: "删除")
            alertView.show()
        } else {
            let vc = JCAddFriendViewController()
            vc.user = user
            navigationController?.pushViewController(vc, animated: true)
        }
    }
}

extension JCSingleSettingViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        if buttonIndex == 1 {
            JMSGFriendManager.removeFriend(withUsername: user.username, appKey: user.appKey, completionHandler: { (result, error) in
                if error == nil {
                    let conv = JMSGConversation.singleConversation(withUsername: self.user.username)
                    if conv != nil {
                        JMSGConversation.deleteSingleConversation(withUsername: self.user.username)
                    }
                    NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateFriendList), object: nil)
                    self.navigationController?.popToRootViewController(animated: true)
                } else {
                    MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
                }
            })
        }
    }
}

extension JCSingleSettingViewController: JCSingleSettingCellDelegate {
    func singleSettingCell(clickAddButton button: UIButton) {
        let vc = JCUpdateMemberViewController()
        vc.isAddMember = false
        vc.currentUser = user
        navigationController?.pushViewController(vc, animated: true)
    }
    
    func singleSettingCell(clickAvatorButton button: UIButton) {
        let vc = JCUserInfoViewController()
        vc.user = user
        vc.isOnConversation = true
        navigationController?.pushViewController(vc, animated: true)
    }
}

extension JCSingleSettingViewController: UIGestureRecognizerDelegate {
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        return true
    }
}

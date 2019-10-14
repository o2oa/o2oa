//
//  JCFriendSettingViewController.swift
//  JChat
//
//  Created by deng on 2017/5/10.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCFriendSettingViewController: UIViewController {

    var user: JMSGUser!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    fileprivate lazy var tableview: UITableView = {
        var tableview = UITableView(frame: CGRect(x: 0, y: 64, width: self.view.width, height: self.view.height - 64), style: .grouped)
        tableview.delegate = self
        tableview.dataSource = self
        tableview.register(JCMineInfoCell.self, forCellReuseIdentifier: "JCMineInfoCell")
        tableview.register(JCButtonCell.self, forCellReuseIdentifier: "JCButtonCell")
        tableview.separatorStyle = .none
        tableview.backgroundColor = UIColor(netHex: 0xe8edf3)
        return tableview
    }()
    
    //MARK: - private func
    private func _init() {
        self.title = "设置"
        automaticallyAdjustsScrollViewInsets = false
        view.addSubview(tableview)
        
        NotificationCenter.default.addObserver(self, selector: #selector(_updateFriendInfo), name: NSNotification.Name(rawValue: kUpdateFriendInfo), object: nil)
    }
    
    @objc func _updateFriendInfo() {
        tableview.reloadData()
    }
}

//MARK: - UITableViewDataSource & UITableViewDelegate
extension JCFriendSettingViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        if user.isFriend {
            return 2
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            if user.isFriend {
                return 3
            }
            return 2
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 {
            return 45
        }
        return 40
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 20
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.0001
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 1 {
            return tableView.dequeueReusableCell(withIdentifier: "JCButtonCell", for: indexPath)
        }
        return tableView.dequeueReusableCell(withIdentifier: "JCMineInfoCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        cell.selectionStyle = .none
        if indexPath.section == 1 {
            guard let cell = cell as? JCButtonCell else {
                return
            }
            cell.delegate = self
            cell.buttonColor = UIColor(netHex: 0xEB424D)
            cell.buttonTitle = "删除好友"
        }
        
        if indexPath.section == 0 {
            guard let cell = cell as? JCMineInfoCell else {
                return
            }
            if user.isFriend {
                switch indexPath.row {
                case 0:
                    cell.title = "备注名"
                    cell.accessoryType = .disclosureIndicator
                    cell.detail = user.noteName ?? ""
                case 1:
                    cell.title = "发送名片"
                    cell.accessoryType = .disclosureIndicator
                case 2:
                    cell.isSwitchOn = user.isInBlacklist
                    cell.delegate = self
                    cell.accessoryType = .none
                    cell.isShowSwitch = true
                    cell.title = "加入黑名单"
                default:
                    break
                }
            } else {
                switch indexPath.row {
                case 0:
                    cell.title = "发送名片"
                    cell.accessoryType = .disclosureIndicator
                case 1:
                    cell.isSwitchOn = user.isInBlacklist
                    cell.delegate = self
                    cell.accessoryType = .none
                    cell.isShowSwitch = true
                    cell.title = "加入黑名单"
                default:
                    break
                }
            }
            
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 0 {
            switch indexPath.row {
            case 0:
                if user.isFriend {
                    let vc = JCNoteNameViewController()
                    vc.user = user
                    navigationController?.pushViewController(vc, animated: true)
                } else {
                    let vc = JCForwardViewController()
                    vc.fromUser = user
                    let nav = JCNavigationController(rootViewController: vc)
                    present(nav, animated: true)
                }
            case 1:
                if user.isFriend {
                    let vc = JCForwardViewController()
                    vc.fromUser = user
                    let nav = JCNavigationController(rootViewController: vc)
                    present(nav, animated: true)
                }
            default:
                break
            }
            
        }
    }
    
}

extension JCFriendSettingViewController: JCButtonCellDelegate {
    func buttonCell(clickButton button: UIButton) {
        let alertView = UIAlertView(title: "删除好友", message: "是否确认删除该好友？", delegate: self, cancelButtonTitle: "取消", otherButtonTitles: "删除")
        alertView.show()
    }
}

extension JCFriendSettingViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        if buttonIndex == 1 {
            JMSGFriendManager.removeFriend(withUsername: user.username, appKey: user.appKey, completionHandler: { (result, error) in
                if error == nil {
                    if JMSGConversation.singleConversation(withUsername: self.user.username) != nil {
                        JMSGConversation.deleteSingleConversation(withUsername: self.user.username)
                        NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateConversation), object: nil)
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

extension JCFriendSettingViewController: JCMineInfoCellDelegate {
    func mineInfoCell(clickSwitchButton button: UISwitch, indexPath: IndexPath?) {
        MBProgressHUD_JChat.showMessage(message: "修改中", toView: view)
        if button.isOn {
            JMSGUser.addUsers(toBlacklist: [user.username]) { (result, error) in
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    MBProgressHUD_JChat.show(text: "修改成功", view: self.view)
                } else {
                    button.isOn = !button.isOn
                    MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
                }
            }
        } else {
            JMSGUser.delUsers(fromBlacklist: [user.username]) { (result, error) in
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    MBProgressHUD_JChat.show(text: "修改成功", view: self.view)
                } else {
                    button.isOn = !button.isOn
                    MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
                }
            }
        }
        
    }
    
}

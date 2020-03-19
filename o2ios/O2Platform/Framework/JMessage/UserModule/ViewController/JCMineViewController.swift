//
//  JCMineViewController.swift
//  JChat
//
//  Created by deng on 2017/2/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCMineViewController: UIViewController {

    //MARK: - life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
        JMessage.remove(self, with: nil)
    }
    
    fileprivate lazy var tableview: UITableView = {
        let tableview = UITableView(frame: self.view.frame, style: .grouped)
        tableview.delegate = self
        tableview.dataSource = self
        tableview.separatorStyle = .none
        tableview.register(JCMineInfoCell.self, forCellReuseIdentifier: "JCMineInfoCell")
        tableview.register(JCMineAvatorCell.self, forCellReuseIdentifier: "JCMineAvatorCell")
        tableview.register(JCButtonCell.self, forCellReuseIdentifier: "JCButtonCell")
        return tableview
    }()
    
    //MARK: - private func 
    private func _init() {
        view.backgroundColor = .white
        view.addSubview(tableview)
        JMessage.add(self, with: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(_updateUserInfo), name: NSNotification.Name(rawValue: kUpdateUserInfo), object: nil)
    }
    
    @objc func _updateUserInfo() {
        tableview.reloadData()
    }

    func updateCurrentUserAvator() {
        JMSGUser.myInfo().thumbAvatarData({ (data, id, error) in
            if let data = data {
                let imageData = NSKeyedArchiver.archivedData(withRootObject: data)
                UserDefaults.standard.set(imageData, forKey: kLastUserAvator)
            } else {
                UserDefaults.standard.removeObject(forKey: kLastUserAvator)
            }
        })
    }

}

extension JCMineViewController: JMessageDelegate {
    
    func onReceive(_ event: JMSGNotificationEvent!) {
        switch event.eventType.rawValue {
        case JMSGLoginStatusChangeEventType.eventNotificationCurrentUserInfoChange.rawValue:
            updateCurrentUserAvator()
            tableview.reloadData()
        default:
            break
        }
    }
}

extension JCMineViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 {
            return 85
        }
        if indexPath.section == 2 {
            return 40
        }
        return 45
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 || section == 2 {
            return 1
        }
        return 4
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            return tableView.dequeueReusableCell(withIdentifier: "JCMineAvatorCell", for: indexPath)
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
            cell.delegate = self
            return
        }
        cell.accessoryType = .disclosureIndicator
        if indexPath.section == 0 {
            guard let cell = cell as? JCMineAvatorCell else {
                return
            }
            let user = JMSGUser.myInfo()
            cell.baindDate(user: user)
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
            cell.title = "修改密码"
        case 1:
            cell.isSwitchOn = JMessage.isSetGlobalNoDisturb()
            cell.title = "免打扰"
        case 2:
            cell.title = "意见反馈"
        case 3:
            cell.title = "关于JChat"
        default:
            break
        }
        
    }
    
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 0 {
            let vc = JCMyInfoViewController()
            navigationController?.pushViewController(vc, animated: true)
            return
        }
        switch indexPath.row {
        case 0:
            navigationController?.pushViewController(JCUpdatePassworkViewController(), animated: true)
        case 2:
            navigationController?.pushViewController(JCFeedbackViewController(), animated: true)
        case 3:
            navigationController?.pushViewController(JCJChatInfoViewController(), animated: true)
        default:
            break
        }
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.0001
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if section == 0 {
            return 5
        }
        return 15
    }

}

extension JCMineViewController: UIAlertViewDelegate {
    func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        switch buttonIndex {
        case 1:
            JMSGUser.logout({ (result, error) in
                JCVerificationInfoDB.shareInstance.queue = nil
                UserDefaults.standard.removeObject(forKey: kCurrentUserName)
                UserDefaults.standard.removeObject(forKey: kCurrentUserPassword)
//                let appDelegate = UIApplication.shared.delegate
//                let window = appDelegate?.window!
//                window?.rootViewController = JCNavigationController(rootViewController: JCLoginViewController())
            })
        default:
            break
        }
    }
}

extension JCMineViewController: JCMineInfoCellDelegate {
    func mineInfoCell(clickSwitchButton button: UISwitch, indexPath: IndexPath?) {
        JMessage.setIsGlobalNoDisturb(button.isOn) { (result, error) in
            
        }
    }
}

extension JCMineViewController: JCButtonCellDelegate {
    func buttonCell(clickButton button: UIButton) {
        let alertView = UIAlertView(title: "", message: "确定要退出登录？", delegate: self, cancelButtonTitle: "取消", otherButtonTitles: "确定")
        alertView.show()
    }
}

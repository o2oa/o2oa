//
//  JCVerificationDetailViewController.swift
//  JChat
//
//  Created by deng on 2017/5/25.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCVerificationDetailViewController: UIViewController {
    
    var verificationInfo: JCVerificationInfo!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    fileprivate lazy var tableview: UITableView = UITableView(frame: .zero, style: .grouped)
    fileprivate var user: JMSGUser?
    
    //MARK: - private func
    private func _init() {
        
        self.title = "详细信息"
        automaticallyAdjustsScrollViewInsets = false
        
        tableview.delegate = self
        tableview.dataSource = self
        tableview.register(JCUserAvatorCell.self, forCellReuseIdentifier: "JCUserAvatorCell")
        tableview.register(JCUserInfoCell.self, forCellReuseIdentifier: "JCUserInfoCell")
        tableview.register(JCResaonCell.self, forCellReuseIdentifier: "JCResaonCell")
        tableview.register(JCDoubleButtonCell.self, forCellReuseIdentifier: "JCDoubleButtonCell")
        
        tableview.separatorStyle = .none
        tableview.backgroundColor = UIColor(netHex: 0xe8edf3)
        tableview.frame = CGRect(x: 0, y: 64, width: view.width, height: view.height - 64)
        view.addSubview(tableview)
        _getUserInfo()
    }
    
    private func _getUserInfo() {
        JMSGUser.userInfoArray(withUsernameArray: [verificationInfo.username]) { (result, error) in
            if error == nil {
                let users = result as! [JMSGUser]
                self.user = users.first
                self.tableview.reloadData()
            }
        }
    }

}

//MARK: - UITableViewDataSource & UITableViewDelegate
extension JCVerificationDetailViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        if user != nil {
            return 2
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 1 {
            return 1
        }
        return 5
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 && indexPath.row == 0 {
            return 175
        }
        if indexPath.section == 1 {
            return 40
        }
        return 45
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if section == 0 {
            return 15
        }
        return 0.0001
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.0001
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 && indexPath.row == 0 {
            return tableView.dequeueReusableCell(withIdentifier: "JCUserAvatorCell", for: indexPath)
        }
        if indexPath.section == 0 && indexPath.row == 1 {
            return tableView.dequeueReusableCell(withIdentifier: "JCResaonCell", for: indexPath)
        }
        if indexPath.section == 1 {
            return tableView.dequeueReusableCell(withIdentifier: "JCDoubleButtonCell", for: indexPath)
        }
        return tableView.dequeueReusableCell(withIdentifier: "JCUserInfoCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        cell.selectionStyle = .none
        
        if indexPath.section == 0 && indexPath.row == 0 {
            guard let cell = cell as? JCUserAvatorCell else {
                return
            }
            cell.delegate = self
            cell.bindData(user: user!)
        }
        
        if indexPath.section == 0 && indexPath.row == 1 {
            guard let cell = cell as? JCResaonCell else {
                return
            }
            if verificationInfo.resaon.isEmpty {
                cell.resaon = "附加消息：无"
            } else {
                cell.resaon = "附加消息：\(verificationInfo.resaon)"
            }
        }
        
        if indexPath.section == 1 {
            guard let cell = cell as? JCDoubleButtonCell else {
                return
            }
            cell.leftButtonTitle = "拒绝"
            cell.rightButtonTitle = "同意"
            cell.delegate = self
        }
        
        if indexPath.section == 0 {
            guard let cell = cell as? JCUserInfoCell else {
                return
            }
            
            switch indexPath.row {
            case 2:
                cell.title = "用户名"
                cell.detail = user?.username
                cell.icon = UIImage.loadImage("com_icon_username")
            case 3:
                cell.title = "性别"
                cell.icon = UIImage.loadImage("com_icon_gender")
                if user?.gender == .male {
                    cell.detail = "男"
                } else if user?.gender == .female {
                    cell.detail = "女"
                } else {
                    cell.detail = "保密"
                }
                
            case 4:
                cell.title = "生日"
                cell.icon = UIImage.loadImage("com_icon_birthday")
                cell.detail = user?.birthday
            case 5:
                cell.title = "地区"
                cell.icon = UIImage.loadImage("com_icon_region")
                cell.detail = user?.region
            default:
                break
            }
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
    }
    
}

extension JCVerificationDetailViewController: JCDoubleButtonCellDelegate {
    func doubleButtonCell(clickLeftButton button: UIButton) {
        
        JMSGFriendManager.rejectInvitation(withUsername: verificationInfo.username, appKey: verificationInfo.appkey, reason: "") { (result, error) in
            if error == nil {
                self.verificationInfo.state = JCVerificationType.reject.rawValue
                JCVerificationInfoDB.shareInstance.updateData(self.verificationInfo)
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateVerification), object: nil)
                self.navigationController?.popViewController(animated: true)
            }
        }
    }
    func doubleButtonCell(clickRightButton button: UIButton) {
        
        JMSGFriendManager.acceptInvitation(withUsername: verificationInfo.username, appKey: verificationInfo.appkey) { (result, error) in
            if error == nil {
                self.verificationInfo.state = JCVerificationType.accept.rawValue
                JCVerificationInfoDB.shareInstance.updateData(self.verificationInfo)
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateVerification), object: nil)
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateFriendList), object: nil)
                self.navigationController?.popViewController(animated: true)
            }
        }
    }
}

extension JCVerificationDetailViewController: JCUserAvatorCellDelegate {
    func tapAvator(_ image: UIImage?) {
        guard let image = image else {
            return
        }
        let browserImageVC = JCImageBrowserViewController()
        browserImageVC.imageArr = [image]
        browserImageVC.imgCurrentIndex = 0
        present(browserImageVC, animated: true) {
            
        }
    }
}

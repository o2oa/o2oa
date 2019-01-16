//
//  JCUserInfoViewController.swift
//  JChat
//
//  Created by deng on 2017/3/22.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCUserInfoViewController: UIViewController {
    
    var user: JMSGUser!
    var isOnConversation = false
    var isOnAddFriend = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    fileprivate lazy var tableview: UITableView = {
        var tableview = UITableView(frame: CGRect(x: 0, y: 0, width: self.view.width, height: self.view.height), style: .grouped)
        tableview.delegate = self
        tableview.dataSource = self
        tableview.register(JCUserAvatorCell.self, forCellReuseIdentifier: "JCUserAvatorCell")
        tableview.register(JCUserInfoCell.self, forCellReuseIdentifier: "JCUserInfoCell")
        tableview.register(JCButtonCell.self, forCellReuseIdentifier: "JCButtonCell")
        tableview.register(JCDoubleButtonCell.self, forCellReuseIdentifier: "JCDoubleButtonCell")
        tableview.separatorStyle = .none
        tableview.backgroundColor = UIColor(netHex: 0xe8edf3)
        return tableview
    }()
    private lazy var moreButton = UIButton(frame: CGRect(x: 0, y: 0, width: 36, height: 36))
    
    //MARK: - private func
    private func _init() {
        self.title = "详细信息"
        automaticallyAdjustsScrollViewInsets = false
        view.addSubview(tableview)
        _setupNavigation()
        NotificationCenter.default.addObserver(self, selector: #selector(_updateUserInfo), name: NSNotification.Name(rawValue: kUpdateFriendInfo), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(_updateUserInfo), name: NSNotification.Name(rawValue: kUpdateUserInfo), object: nil)
    }
    
    private func _setupNavigation() {
        moreButton.addTarget(self, action: #selector(_clickNavRightButton), for: .touchUpInside)
        moreButton.setImage(UIImage.loadImage("com_icon_more"), for: .normal)
        let item = UIBarButtonItem(customView: moreButton)
        navigationItem.rightBarButtonItem =  item
    }
    
    @objc func _updateUserInfo() {
        tableview.reloadData()
    }
    
    @objc func _clickNavRightButton() {
        let vc = JCFriendSettingViewController()
        vc.user = self.user
        navigationController?.pushViewController(vc, animated: true)
    }
}

//MARK: - UITableViewDataSource & UITableViewDelegate
extension JCUserInfoViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 1 {
            return 1
        }
        return 6
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
        return 0.001
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.0001
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 && indexPath.row == 0 {
            return tableView.dequeueReusableCell(withIdentifier: "JCUserAvatorCell", for: indexPath)
        }
        if indexPath.section == 1  {
            if user.isFriend || isOnAddFriend {
                return tableView.dequeueReusableCell(withIdentifier: "JCButtonCell", for: indexPath)
            } else {
                return tableView.dequeueReusableCell(withIdentifier: "JCDoubleButtonCell", for: indexPath)
            }
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
            cell.bindData(user: user)
        }
        
        if indexPath.section == 1 {
            if user.isFriend || isOnAddFriend {
                guard let cell = cell as? JCButtonCell else {
                    return
                }
                cell.delegate = self
                if isOnAddFriend {
                    cell.buttonTitle = "添加好友"
                } else {
                    cell.buttonTitle = "发送消息"
                }
            } else {
                guard let cell = cell as? JCDoubleButtonCell else {
                    return
                }
                cell.delegate = self
            }
        }
        
        if indexPath.section == 0 {
            guard let cell = cell as? JCUserInfoCell else {
                return
            }
            
            switch indexPath.row {
            case 1:
                cell.title = "昵称"
                cell.detail = user.nickname ?? ""
                cell.icon = UIImage.loadImage("com_icon_nickname")
            case 2:
                cell.title = "用户名"
                cell.detail = user.username
                cell.icon = UIImage.loadImage("com_icon_username")
            case 3:
                cell.title = "性别"
                cell.icon = UIImage.loadImage("com_icon_gender")
                switch user.gender {
                case .male:
                    cell.detail = "男"
                case .female:
                    cell.detail = "女"
                case .unknown:
                    cell.detail = "保密"
                }
            case 4:
                cell.title = "生日"
                cell.icon = UIImage.loadImage("com_icon_birthday")
                cell.detail = user.birthday
            case 5:
                cell.title = "地区"
                cell.icon = UIImage.loadImage("com_icon_region")
                cell.detail = user.region
            default:
                break
            }
        }
    }
    
}

extension JCUserInfoViewController: JCButtonCellDelegate {
    func buttonCell(clickButton button: UIButton) {
        if isOnAddFriend {
            let vc = JCAddFriendViewController()
            vc.user = user
            navigationController?.pushViewController(vc, animated: true)
            return
        }
        if isOnConversation {
            for vc in (navigationController?.viewControllers)! {
                if vc is JCChatViewController {
                    navigationController?.popToViewController(vc, animated: true)
                }
            }
            return
        }
        JMSGConversation.createSingleConversation(withUsername: (user?.username)!, appKey: (user?.appKey)!) { (result, error) in
            if error == nil {
                let conv = result as! JMSGConversation
                let vc = JCChatViewController(conversation: conv)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateConversation), object: nil, userInfo: nil)
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
}

extension JCUserInfoViewController: JCDoubleButtonCellDelegate {
    func doubleButtonCell(clickLeftButton button: UIButton) {
        let vc = JCAddFriendViewController()
        vc.user = user
        navigationController?.pushViewController(vc, animated: true)
    }
    func doubleButtonCell(clickRightButton button: UIButton) {
        JMSGConversation.createSingleConversation(withUsername: (user?.username)!, appKey: (user?.appKey)!) { (result, error) in
            if error == nil {
                let conv = result as! JMSGConversation
                let vc = JCChatViewController(conversation: conv)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateConversation), object: nil, userInfo: nil)
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
}

extension JCUserInfoViewController: JCUserAvatorCellDelegate {
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



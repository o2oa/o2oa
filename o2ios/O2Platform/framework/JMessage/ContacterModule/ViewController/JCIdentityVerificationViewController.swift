//
//  JCIdentityVerificationViewController.swift
//  JChat
//
//  Created by deng on 2017/4/5.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCIdentityVerificationViewController: UITableViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        UserDefaults.standard.set(0, forKey: kUnreadInvitationCount)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    private var infos: [JCVerificationInfo] = []
    
    
    // MARK: - private func
    private func _init() {
        self.title = "验证信息"
        view.backgroundColor = .white
        tableView.separatorStyle = .none
        tableView.register(JCVerificationCell.self, forCellReuseIdentifier: "JCVerificationCell")
        _getData()
        NotificationCenter.default.addObserver(self, selector: #selector(_getData), name: NSNotification.Name(rawValue: kUpdateVerification), object: nil)
    }
    
    func _getData() {
        infos.removeAll()
        infos = JCVerificationInfoDB.shareInstance.quaryData()
        tableView.reloadData()
    }

    
    // MARK: - Table view data source
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return infos.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "JCVerificationCell", for: indexPath)
    }
    override func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let cell = cell as? JCVerificationCell else {
            return
        }
        cell.bindData(infos[indexPath.row])
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 65
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let info = infos[indexPath.row]
        
        switch info.state {
        case JCVerificationType.accept.rawValue,
             JCVerificationType.reject.rawValue,
             JCVerificationType.wait.rawValue:
            JMSGUser.userInfoArray(withUsernameArray: [info.username], completionHandler: { (result, error) in
                if error == nil {
                    let users = result as! [JMSGUser]
                    let user = users.first
                    let vc = JCUserInfoViewController()
                    vc.user = user
                    self.navigationController?.pushViewController(vc, animated: true)
                }
            })
        case JCVerificationType.receive.rawValue:
            let vc = JCVerificationDetailViewController()
            vc.verificationInfo = info
            navigationController?.pushViewController(vc, animated: true)
        default:
            break
        }
    }
    
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    override func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .delete
    }
    
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            let info = infos[indexPath.row]
            JCVerificationInfoDB.shareInstance.delete(info)
            infos.remove(at: indexPath.row)
            tableView.reloadData()
        }
    }

}

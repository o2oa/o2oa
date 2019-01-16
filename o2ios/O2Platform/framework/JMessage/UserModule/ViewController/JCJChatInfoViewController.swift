//
//  JCJChatInfoViewController.swift
//  JChat
//
//  Created by deng on 2017/3/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCJChatInfoViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    fileprivate lazy var tableview: UITableView = UITableView(frame: .zero, style: .grouped)
    fileprivate lazy var tagArray = ["JChat 版本", "SDK 版本", "官方网站"]
    fileprivate lazy var version: String = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as! String
    fileprivate lazy var bundleVersion: String = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as! String

    //MARK: - private func
    private func _init() {
        self.title = "关于JChat"
        view.backgroundColor = .white
        
        tableview.delegate = self
        tableview.dataSource = self
        tableview.backgroundColor = UIColor(netHex: 0xe8edf3)
        tableview.frame = CGRect(x: 0, y: 0, width: view.width, height: view.height)
        tableview.register(JCJChatInfoCell.self, forCellReuseIdentifier: "JCJChatInfoCell")
        tableview.separatorStyle = .none
        view.addSubview(tableview)
    }
}

extension JCJChatInfoViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        }
        return tagArray.count
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 {
            return 140
        }
        return 45
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.0001
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 {
            return 0.0001
        }
        return 4.5
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            return tableView.dequeueReusableCell(withIdentifier: "JCJChatInfoCell", for: indexPath)
        }
        var cell = tableView.dequeueReusableCell(withIdentifier: "JCMineInfoCell")
        if cell == nil {
            cell = JCTableViewCell.init(style: .value1, reuseIdentifier: "JCMineInfoCell")
        }
        return cell!
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        cell.selectionStyle = .none
        if indexPath.section == 1 {
            cell.accessoryType = .none
            cell.detailTextLabel?.font = UIFont.systemFont(ofSize: 14)
            cell.textLabel?.font = UIFont.systemFont(ofSize: 16)
            cell.textLabel?.layer.masksToBounds = true
            switch indexPath.row {
            case 0:
                cell.detailTextLabel?.text = "v\(version).\(bundleVersion)"
            case 1:
                cell.detailTextLabel?.text = "v\(JMESSAGE_VERSION)"
            case 2:
                cell.selectionStyle = .default
                cell.accessoryType = .disclosureIndicator
            default:
                break
            }
            cell.textLabel?.text = tagArray[indexPath.row]
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if indexPath.row == 2 {
            UIApplication.shared.openURL(URL(string: "https://www.jiguang.cn/")!)
        }
    }
}

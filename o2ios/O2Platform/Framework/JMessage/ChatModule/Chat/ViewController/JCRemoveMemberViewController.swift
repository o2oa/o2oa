//
//  JCRemoveMemberViewController.swift
//  JChat
//
//  Created by deng on 2017/5/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCRemoveMemberViewController: UIViewController {
    
    var group: JMSGGroup!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    fileprivate lazy var toolView: UIView = UIView(frame: CGRect(x: 0, y: 64, width: self.view.width, height: 55))
    fileprivate var tableView: UITableView = UITableView(frame: .zero, style: .grouped)
    fileprivate var collectionView: UICollectionView!
    fileprivate lazy var searchView: UISearchBar = UISearchBar.default

    fileprivate lazy var delButton = UIButton(frame: CGRect(x: 0, y: 0, width: 120, height: 28))
    
    fileprivate lazy var users: [JMSGUser] = []
    fileprivate lazy var keys: [String] = []
    fileprivate lazy var data: Dictionary<String, [JMSGUser]> = Dictionary()
    
    fileprivate lazy var filteredUsersArray: [JMSGUser] = []
    
    fileprivate lazy var selectUsers: [JMSGUser] = []
    
    private func _init() {
        self.title = "删除成员"
        view.backgroundColor = .white
        automaticallyAdjustsScrollViewInsets = false

        view.addSubview(toolView)
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.keyboardDismissMode = .onDrag
        tableView.sectionIndexColor = UIColor(netHex: 0x2dd0cf)
        tableView.sectionIndexBackgroundColor = .clear
        tableView.register(JCSelectMemberCell.self, forCellReuseIdentifier: "JCSelectMemberCell")
        tableView.frame = CGRect(x: 0, y: 31 + 64, width: view.width, height: view.height - 55 - 64)
        view.addSubview(tableView)
        
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .horizontal
        flowLayout.minimumInteritemSpacing = 0
        flowLayout.minimumLineSpacing = 0
        collectionView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout)
        collectionView.backgroundColor = .clear
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(JCUpdateMemberCell.self, forCellWithReuseIdentifier: "JCUpdateMemberCell")
        
        searchView.frame = CGRect(x: 15, y: 0, width: toolView.width - 30, height: 31)
        searchView.delegate = self

        users = group.memberArray()
        if group.owner == JMSGUser.myInfo().username  {
            users = users.filter({ (u) -> Bool in
                u.username != JMSGUser.myInfo().username || u.appKey != JMSGUser.myInfo().appKey
            })
        }
        filteredUsersArray = users
        _classify(users)
        
        toolView.addSubview(searchView)
        toolView.addSubview(collectionView)
        
        _setupNavigation()
    }
    
    private func _setupNavigation() {
        delButton.addTarget(self, action: #selector(_clickNavRightButton(_:)), for: .touchUpInside)
        delButton.setTitle("删除", for: .normal)
        delButton.titleLabel?.font = UIFont.systemFont(ofSize: 15)
        delButton.contentHorizontalAlignment = .right
        let item = UIBarButtonItem(customView: delButton)
        navigationItem.rightBarButtonItem =  item
        navigationController?.navigationItem.rightBarButtonItem?.isEnabled = false
    }
    
    private func _classify(_ users: [JMSGUser]) {
        
        filteredUsersArray = users
        
        keys.removeAll()
        data.removeAll()
        
        for item in users {
            let key = item.displayName().firstCharacter()
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
        keys = keys.sorted(by: { (str1, str2) -> Bool in
            return str1 < str2
        })
        tableView.reloadData()
        collectionView.reloadData()
    }
    
    fileprivate func _reloadCollectionView() {
        if selectUsers.count > 0 {
            delButton.alpha = 1.0
            delButton.setTitle("删除(\(selectUsers.count))", for: .normal)
            navigationController?.navigationItem.rightBarButtonItem?.isEnabled = true
        } else {
            delButton.alpha = 0.5
            delButton.setTitle("删除", for: .normal)
            navigationController?.navigationItem.rightBarButtonItem?.isEnabled = false
        }
        switch selectUsers.count {
        case 0:
            collectionView.frame = .zero
            searchView.frame = CGRect(x: 15, y: 0, width: toolView.width - 30, height: 31)
            toolView.frame = CGRect(x: 0, y: 64, width: toolView.width, height: 31)
            tableView.frame = CGRect(x: tableView.x, y: 64 + 31, width: tableView.width, height: view.height - 64 - 31)
        case 1:
            collectionView.frame = CGRect(x: 10, y: 0, width: 46, height: 55)
            searchView.frame = CGRect(x: 5 + 46, y: 0, width: toolView.width - 5 - 46, height: 55)
            toolView.frame = CGRect(x: 0, y: 64, width: toolView.width, height: 55)
            tableView.frame = CGRect(x: tableView.x, y: 64 + 55, width: tableView.width, height: view.height - 64 - 55)
        case 2:
            collectionView.frame = CGRect(x: 10, y: 0, width: 92, height: 55)
            searchView.frame = CGRect(x: 5 + 46 * 2, y: 0, width: toolView.width - 5 - 46 * 2, height: 55)
        case 3:
            collectionView.frame = CGRect(x: 10, y: 0, width: 138, height: 55)
            searchView.frame = CGRect(x: 5 + 46 * 3, y: 0, width: toolView.width - 5 - 46 * 3, height: 55)
        case 4:
            collectionView.frame = CGRect(x: 10, y: 0, width: 184, height: 55)
            searchView.frame = CGRect(x: 5 + 46 * 4, y: 0, width: toolView.width - 5 - 46 * 4, height: 55)
        default:
            collectionView.frame = CGRect(x: 10, y: 0, width: 230, height: 55)
            searchView.frame = CGRect(x: 5 + 46 * 5, y: 0, width: toolView.width - 5 - 46 * 5, height: 55)
        }
        collectionView.reloadData()
    }
    
    func _clickNavRightButton(_ sender: UIButton) {
        var userNames: [String] = []
        for item in selectUsers {
            userNames.append(item.username)
        }
        MBProgressHUD_JChat.showMessage(message: "删除中...", toView: view)
        group.removeMembers(withUsernameArray: userNames) { (result, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateGroupInfo), object: nil)
                self.navigationController?.popViewController(animated: true)
            } else {
                MBProgressHUD_JChat.show(text: "删除失败，请重试", view: self.view)
            }
        }
    }
    
    fileprivate func filter(_ searchString: String) {
        if searchString.isEmpty || searchString == "" {
            _classify(users)
            return
        }
        filteredUsersArray = _JCFilterUsers(users: users, string: searchString)
        _classify(filteredUsersArray)
    }
}

//Mark: - UITableViewDelegate & UITableViewDataSource
extension JCRemoveMemberViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        if filteredUsersArray.count > 0 {
            return keys.count
        }
        return 0
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data[keys[section]]!.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return keys[section]
    }
    
    func sectionIndexTitles(for tableView: UITableView) -> [String]? {
        return keys
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 55
    }
    
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 {
            return 30
        }
        return 10
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "JCSelectMemberCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let cell = cell as? JCSelectMemberCell else {
            return
        }
        let user = data[keys[indexPath.section]]?[indexPath.row]
        cell.bindDate(user!)
        if selectUsers.contains(where: { (u) -> Bool in
            return u.username == user?.username && u.appKey == user?.appKey
        })  {
            cell.selectIcon = UIImage.loadImage("com_icon_select")
        } else {
            cell.selectIcon = UIImage.loadImage("com_icon_unselect")
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        guard let cell = tableView.cellForRow(at: indexPath) as? JCSelectMemberCell else {
            return
        }
        guard let user = data[keys[indexPath.section]]?[indexPath.row] else {
            return
        }
        if selectUsers.contains(where: { (u) -> Bool in
            return u.username == user.username && u.appKey == user.appKey
        })  {
            // remove
            cell.selectIcon = UIImage.loadImage("com_icon_unselect")
            selectUsers = selectUsers.filter({ (u) -> Bool in
                u.username != user.username || u.appKey != user.appKey
            })
            _reloadCollectionView()
        } else {
            selectUsers.append(user)
            cell.selectIcon = UIImage.loadImage("com_icon_select")
            _reloadCollectionView()
        }
        if selectUsers.count > 0 {
            collectionView.scrollToItem(at: IndexPath(row: selectUsers.count - 1, section: 0), at: .right, animated: false)
        }
    }
}

extension JCRemoveMemberViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return selectUsers.count
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        sizeForItemAtIndexPath indexPath: IndexPath) -> CGSize {
        return CGSize(width: 46, height: 55)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        return collectionView.dequeueReusableCell(withReuseIdentifier: "JCUpdateMemberCell", for: indexPath)
    }
    
    func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        guard let cell = cell as? JCUpdateMemberCell else {
            return
        }
        cell.backgroundColor = .white
        cell.bindDate(user: selectUsers[indexPath.row])
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        selectUsers.remove(at: indexPath.row)
        tableView.reloadData()
        _reloadCollectionView()
    }
}

extension JCRemoveMemberViewController: UISearchBarDelegate {
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        filter(searchText)
    }
}

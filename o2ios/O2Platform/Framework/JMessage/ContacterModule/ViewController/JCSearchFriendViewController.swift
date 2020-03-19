//
//  JCSearchFriendViewController.swift
//  JChat
//
//  Created by deng on 2017/4/27.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

class JCSearchFriendViewController: UIViewController {
    
    var isSearchUser: Bool = false

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if isActive {
            searchController.isActive = true
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        if searchController.isActive {
            searchController.isActive = false
            isActive = true
        }
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    private var isActive = false
    
    fileprivate lazy var searchController: JCSearchController = JCSearchController(searchResultsController: nil)
    
    private lazy var searchView: UIView = UIView(frame: CGRect(x: 0, y: self.topOffset, width: self.view.width, height: 31))
    
    fileprivate lazy var bgView: UIView = UIView(frame: CGRect(x: 0, y: self.topOffset + 31 + 5, width: self.view.width, height: self.view.height - 31 - 5))
    
    fileprivate lazy var infoView: UIView = UIView(frame: CGRect(x: 0, y: 0, width: self.view.width, height: 65.5))
    
    fileprivate lazy var avatorView: UIImageView = UIImageView(frame: CGRect(x: 15, y: 7.5, width: 50, height: 50))
    
    fileprivate lazy var nameLabel: UILabel =  UILabel(frame: CGRect(x: 65 + 10, y: 21, width: 200, height: 22.5))
    
    private let width = UIScreen.main.applicationFrame.size.width
    fileprivate lazy var addButton = UIButton()
    
    fileprivate lazy var tipsView: UIView = {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: self.view.width, height: 67))
        view.backgroundColor = .white
        let tips = UILabel()
        tips.frame = view.frame
        tips.font = UIFont.systemFont(ofSize: 16)
        tips.textColor = UIColor(netHex: 0x999999)
        tips.textAlignment = .center
        tips.text = "未搜索到用户"
        view.addSubview(tips)
        return view
    }()
    
    private lazy var networkErrorView: UIView = {
        let tipsView = UIView(frame: CGRect(x: 0, y: self.topOffset + 36, width: self.view.width, height: self.view.height))
        var tipsLabel: UILabel = UILabel(frame: CGRect(x: 0, y: self.topOffset, width: tipsView.width, height: 22.5))
        tipsLabel.textColor = UIColor(netHex: 0x999999)
        tipsLabel.textAlignment = .center
        tipsLabel.font = UIFont.systemFont(ofSize: 16)
        tipsLabel.text = "无法连接网络"
        tipsView.addSubview(tipsLabel)
        tipsView.isHidden = true
        tipsView.backgroundColor = .white
        return tipsView
    }()
    
    fileprivate var user: JMSGUser?

    private var topOffset: CGFloat {
        if #available(iOS 11, *){
            if isIPhoneX {
                return 22
            }
            return 0
        }else{
            if isIPhoneX {
                return 22
            }
            return 0
        }
    }

    private func _init() {
        view.backgroundColor = UIColor(netHex: 0xE8EDF3)
        automaticallyAdjustsScrollViewInsets = false
        if isSearchUser {
            self.title = "发起单聊"
        } else {
            self.title = "添加好友"
            addButton.frame = CGRect(x: self.width - 72 - 15, y: 20, width: 72, height: 30)
            addButton.setTitle("加好友", for: .normal)
            addButton.titleLabel?.font = UIFont.systemFont(ofSize: 14)
            let image = UIImage.createImage(color: UIColor(netHex: 0x2dd0cf), size: CGSize(width: 72, height: 25))
            addButton.setBackgroundImage(image, for: .normal)
            addButton.layer.cornerRadius = 2
            addButton.layer.masksToBounds = true
            addButton.addTarget(self, action: #selector(_addFriend), for: .touchUpInside)
            infoView.backgroundColor = .white
            infoView.addSubview(addButton)
        }

        searchController.searchBar.placeholder = "搜索用户名"
        searchController.hidesNavigationBarDuringPresentation = false
        searchController.searchControllerDelegate = self
        searchController.searchBar.delegate = self
        searchView.addSubview(searchController.searchBar)
        view.addSubview(searchView)
        
        nameLabel.textColor = UIColor(netHex: 0x2C2C2C)
        nameLabel.font = UIFont.systemFont(ofSize: 18)
        
        let line = UILabel(frame: CGRect(x: 15, y: 50 + 15, width: view.width - 30, height: 0.5))
        line.backgroundColor = UIColor(netHex: 0xD9D9D9)
        
        tipsView.isHidden = true
    
        infoView.addSubview(avatorView)
        infoView.addSubview(nameLabel)
        infoView.addSubview(line)
        infoView.addSubview(tipsView)
        
        
        let tapGR = UITapGestureRecognizer(target: self, action: #selector(_tapHandler))
        infoView.addGestureRecognizer(tapGR)
        
        bgView.backgroundColor = .white
        bgView.addSubview(infoView)
        bgView.isHidden = true
        view.addSubview(bgView)
        view.addSubview(networkErrorView)
        
        if JCNetworkManager.isNotReachable {
            networkErrorView.isHidden = false
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(note:)), name: NSNotification.Name(rawValue: "kNetworkReachabilityChangedNotification"), object: nil)
    }
    
    @objc func reachabilityChanged(note: NSNotification) {
        if let curReach = note.object as? Reachability {
            let status = curReach.currentReachabilityStatus()
            switch status {
            case NotReachable:
                networkErrorView.isHidden = false
            default :
                networkErrorView.isHidden = true
            }
        }
    }
    
    @objc func _tapHandler(sender:UITapGestureRecognizer) {
        if (user?.isEqual(to: JMSGUser.myInfo()))! {
            searchController.isActive = false
            navigationController?.pushViewController(JCMyInfoViewController(), animated: true)
            return
        }
        let vc = JCUserInfoViewController()
        vc.user = user
        if !isSearchUser && !(user?.isFriend)! {
            vc.isOnAddFriend = true
        }
        searchController.isActive = false
        navigationController?.pushViewController(vc, animated: true)
    }
    
    @objc func _addFriend() {
        let vc = JCAddFriendViewController()
        vc.user = self.user!
        searchController.isActive = false
        navigationController?.pushViewController(vc, animated: true)
    }
}

extension JCSearchFriendViewController: JCSearchControllerDelegate, UISearchBarDelegate {
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        bgView.isHidden = true
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        searchBar.showsCancelButton = true
        for view in (searchBar.subviews.first?.subviews)! {
            if view is UIButton {
                let cancelButton = view as! UIButton
                cancelButton.setTitleColor(UIColor(netHex: 0x999999), for: .normal)
                break
            }
        }
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        guard let name = searchBar.text else {
            return
        }
        if name.isEmpty {
            return
        }
        MBProgressHUD_JChat.showMessage(message: "查找中", toView: view)
        JMSGUser.userInfoArray(withUsernameArray: [name]) { (result, error) in
            self.bgView.isHidden = false
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                self.tipsView.isHidden = true
                let users = result as! [JMSGUser]
                let user = users.first
                self.user = user
                if (user?.isFriend)! || (user?.isEqual(to: JMSGUser.myInfo()))! {
                    self.addButton.isHidden = true
                } else {
                    self.addButton.isHidden = false
                }
                self.nameLabel.text = user?.displayName()
                self.avatorView.image = UIImage.loadImage("com_icon_user_50")
                user?.thumbAvatarData({ (data, id, error) in
                    if data != nil {
                        let image = UIImage(data: data!)
                        self.avatorView.image = image
                    }
                })
                
            } else {
                self.tipsView.isHidden = false
            }
        }
    }

}

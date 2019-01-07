//
//  UnreadListViewController.swift
//  JChat
//
//  Created by 邓永豪 on 2017/9/14.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class UnreadListViewController: UIViewController, CustomNavigation {

    var message: JMSGMessage!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    fileprivate var unreadList = UnreadListTableView()
    fileprivate var readList = UnreadListTableView()

    private lazy var tabedSlideView: DLTabedSlideView = {
        var tabedSlideView = DLTabedSlideView(frame: CGRect(x: 0, y: 64, width: self.view.width, height: self.view.height - 64))
        tabedSlideView.delegate = self
        tabedSlideView.baseViewController = self
        tabedSlideView.tabItemNormalColor = .black
        tabedSlideView.tabItemSelectedColor =  UIColor(netHex: 0x2DD0CF)
        tabedSlideView.tabbarTrackColor = UIColor(netHex: 0x2DD0CF)
        tabedSlideView.tabbarBackgroundImage = UIImage.createImage(color: .white, size: CGSize(width: self.view.width, height: 39))
        tabedSlideView.tabbarBottomSpacing = 0.0
        return tabedSlideView
    }()

    private func _init() {
        self.title = "消息查看列表"
        view.backgroundColor = .white

        customLeftBarButton(delegate: self)

        MBProgressHUD_JChat.showMessage(message: "获取中...", toView: view)
        message.messageReadDetailHandler { (readUsers, unreadUsers, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                DispatchQueue.main.async {
                    self.unreadList.users = unreadUsers as! [JMSGUser]
                    self.readList.users = readUsers as! [JMSGUser]
                    self.view.addSubview(self.tabedSlideView)
                    var unreadTitle = "未读"
                    if let count = unreadUsers?.count {
                        unreadTitle = "未读(\(count))"
                    }
                    let unreadItem = DLTabedbarItem(title: unreadTitle, image: nil, selectedImage: nil)
                    var readTitle =  "已读"
                    if let count = readUsers?.count {
                        readTitle = "已读(\(count))"
                    }

                    let readItem = DLTabedbarItem(title: readTitle, image: nil, selectedImage: nil)
                    self.tabedSlideView.tabbarItems = [unreadItem!, readItem!]
                    self.tabedSlideView.buildTabbar()
                    self.tabedSlideView.selectedIndex = 0
                }

            } else {
                MBProgressHUD_JChat.show(text: "获取失败", view: self.view)
            }
        }


    }
}

extension UnreadListViewController: DLTabedSlideViewDelegate {
    func numberOfTabs(in sender: DLTabedSlideView!) -> Int {
        return 2
    }

    func dlTabedSlideView(_ sender: DLTabedSlideView!, controllerAt index: Int) -> UIViewController! {
        switch index {
        case 0:
            return unreadList
        default:
             return readList
        }
    }
}


class UnreadListTableView: UITableViewController {

    var users: [JMSGUser] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    private func _init() {
        view.backgroundColor = .white
        tableView.separatorStyle = .none
        tableView.showsVerticalScrollIndicator = false
        tableView.register(JCContacterCell.self, forCellReuseIdentifier: "JCContacterCell")
        let line = UILabel(frame: CGRect(x: 0, y: 0, width: view.width, height: 0.5))
        line.layer.backgroundColor = UIColor(netHex: 0xD9D9D9).cgColor
        view.addSubview(line)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return users.count
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 55
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "JCContacterCell", for: indexPath)
    }

    override func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let cell = cell as? JCContacterCell else {
            return
        }

        let user = users[indexPath.row]
        cell.isShowBadge = false
        cell.bindDate(user)
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let vc = JCUserInfoViewController()
        let user = users[indexPath.row]
        vc.user = user
        navigationController?.pushViewController(vc, animated: true)
    }

}

extension UnreadListViewController: UIGestureRecognizerDelegate {
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        return true
    }
}


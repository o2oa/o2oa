//
//  IMChatViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/8.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class IMChatViewController: UIViewController {

    // MARK: - IBOutlet
    //消息列表
    @IBOutlet weak var tableView: UITableView!
    //消息输入框
    @IBOutlet weak var messageInputView: UITextField!
    //底部工具栏的高度约束
    @IBOutlet weak var bottomBarHeightConstraint: NSLayoutConstraint!
    //底部工具栏
    @IBOutlet weak var bottomBar: UIView!

    private lazy var viewModel: IMViewModel = {
        return IMViewModel()
    }()

    // MARK: - properties
    var conversation: IMConversationInfo? = nil
    private var chatMessageList: [IMMessageInfo] = []
    private var page = 1
    private var isShowEmoji = false
    private var bottomBarHeight = 64


    // MARK: - functions
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib(nibName: "IMChatMessageViewCell", bundle: nil), forCellReuseIdentifier: "IMChatMessageViewCell")
        self.tableView.separatorStyle = .none
        self.messageInputView.delegate = self

        //底部安全距离 老机型没有
        self.bottomBarHeight = Int(iPhoneX ? 64 + IPHONEX_BOTTOM_SAFE_HEIGHT: 64)
        self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat
        self.bottomBar.topBorder(width: 1, borderColor: base_gray_color.alpha(0.5))
        self.messageInputView.backgroundColor = base_gray_color


        self.loadMsgList(page: page)
    }

    //获取消息
    private func loadMsgList(page: Int) {
        if let c = self.conversation, let id = c.id {
            self.viewModel.myMsgPageList(page: page, conversationId: id).then { (list) in
                self.chatMessageList = list
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            }
        } else {
            self.showError(title: "参数错误！！！")
        }
    }


    // MARK: - IBAction
    //点击表情按钮
    @IBAction func clickEmojiBtn(_ sender: UIButton) {
        self.isShowEmoji.toggle()
        self.view.endEditing(true)
        if self.isShowEmoji {
            self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat + 128
        } else {
            self.bottomBarHeightConstraint.constant = self.bottomBarHeight.toCGFloat

        }
        self.view.layoutIfNeeded()
    }



}
// MARK: - tableview delegate
extension IMChatViewController: UITableViewDelegate, UITableViewDataSource {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.chatMessageList.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "IMChatMessageViewCell", for: indexPath)
    }
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let c = cell as? IMChatMessageViewCell else {
            return
        }
        //todo
        c.setContent(item: self.chatMessageList[indexPath.row])
    }


}

// MARK: - textField delegate
extension IMChatViewController: UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        DDLogDebug("准备开始输入......")
        closeEmoji()
        return true
    }

    private func closeEmoji() {
        self.isShowEmoji = false
        self.bottomBarHeightConstraint.constant = 64
        self.view.layoutIfNeeded()
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        DDLogDebug("回车。。。。")
        return true
    }
}

//
//  IMInstantMessageViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/12.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class IMInstantMessageViewController: UITableViewController {
        
    private lazy var viewModel: IMViewModel = {
           return IMViewModel()
       }()
    
    var instantMsgList: [InstantMessage] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "通知消息"
        self.tableView.register(UINib(nibName: "IMChatMessageViewCell", bundle: nil), forCellReuseIdentifier: "IMChatMessageViewCell")
        self.tableView.separatorStyle = .none
//        self.tableView.rowHeight = UITableView.automaticDimension
//        self.tableView.estimatedRowHeight = 144
        self.tableView.backgroundColor = UIColor(hex: "#f3f3f3")
       
    }
    
    override func viewDidAppear(_ animated: Bool) {
        self.scrollMessageToBottom()
    }
    
    //刷新tableview 滚动到底部
    private func scrollMessageToBottom() {
        DispatchQueue.main.async {
            if self.instantMsgList.count > 0 {
                self.tableView.scrollToRow(at: IndexPath(row: self.instantMsgList.count-1, section: 0), at: .bottom, animated: false)
            }
        }
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.instantMsgList.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "IMChatMessageViewCell", for: indexPath) as? IMChatMessageViewCell {
            cell.setInstantContent(item: self.instantMsgList[indexPath.row])
            cell.delegate = self
            return cell
        }
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return cellHeightForInstant(item: self.instantMsgList[indexPath.row])
    }
    
    func cellHeightForInstant(item: InstantMessage) -> CGFloat {
        if let msg = item.title {
            let size = msg.getSizeWithMaxWidth(fontSize: 16, maxWidth: messageWidth)
            // 上边距 69 + 文字高度 + 内边距 + 底部空白高度
            return 69 + size.height + 28 + 10
        }
        return 132
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
    }
}

extension IMInstantMessageViewController : IMChatMessageDelegate {
    func clickImageMessage(info: IMMessageBodyInfo) {
        //无需实现
    }
    
    func openLocatinMap(info: IMMessageBodyInfo) {
        //无需实现
    }
    
    func openApplication(storyboard: String) {
        if storyboard == "mind" {
            let flutterViewController = O2FlutterViewController()
            flutterViewController.setInitialRoute("mindMap")
            self.present(flutterViewController, animated: false, completion: nil)
        }else {
            let storyBoard = UIStoryboard(name: storyboard, bundle: nil)
            guard let destVC = storyBoard.instantiateInitialViewController() else {
                return
            }
            destVC.modalPresentationStyle = .fullScreen
            if destVC.isKind(of: ZLNavigationController.self) {
                self.show(destVC, sender: nil)
            }else{
                self.navigationController?.pushViewController(destVC, animated: true)
            }
        }
    }
    
    func openWork(workId: String) {
        self.showLoading()
        self.viewModel.isWorkCompleted(work: workId).always {
            self.hideLoading()
        }.then{ result in
            if result {
                self.showMessage(msg: "工作已经完成了！")
            }else {
                self.openWorkPage(work: workId)
            }
        }.catch {_ in
            self.showMessage(msg: "工作已经完成了！")
        }
        
        
    }
    
    private func openWorkPage(work: String) {
        let storyBoard = UIStoryboard(name: "task", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
        let json = """
        {"work":"\(work)", "workCompleted":"", "title":""}
        """
        let todo = TodoTask(JSONString: json)
        destVC.todoTask = todo
        destVC.backFlag = 3 //隐藏就行
        self.show(destVC, sender: nil)
    }
    
}

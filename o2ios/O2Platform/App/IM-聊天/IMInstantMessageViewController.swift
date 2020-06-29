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
        self.tableView.rowHeight = UITableView.automaticDimension
        self.tableView.estimatedRowHeight = 144
        self.tableView.backgroundColor = UIColor(hex: "#f3f3f3")
       
    }
    
    override func viewDidAppear(_ animated: Bool) {
        self.scrollMessageToBottom()
    }
    
    //刷新tableview 滚动到底部
    private func scrollMessageToBottom() {
        DispatchQueue.main.async {
            if self.instantMsgList.count > 0 {
                self.tableView.scrollToRow(at: IndexPath(row: self.instantMsgList.count-1, section: 0), at: .bottom, animated: true)
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
            return cell
        }

        return UITableViewCell()
    }
    
}

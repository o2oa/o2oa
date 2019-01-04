//
//  MainPublishContentCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/7.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class MainPublishContentCell: UICollectionViewCell {
    
    var publishInfos:[CMS_PublishInfo] = [] {
        didSet {
            self.contentTableView.reloadData()
        }
    }
    
    @IBOutlet weak var contentTableView: ZLBaseTableView!
    
    override func awakeFromNib() {
        self.contentTableView.emptyTitle = "没有新公告"
        self.contentTableView.delegate = self
        self.contentTableView.dataSource = self
    }
    
    
}

extension MainPublishContentCell:UITableViewDataSource,UITableViewDelegate{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.publishInfos.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "demoCell", for: indexPath)
        let info = self.publishInfos[indexPath.row]
        cell.textLabel?.text = info.title
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let info = self.publishInfos[indexPath.row]
        DDLogDebug(info.description)
        NotificationCenter.default.post(name: NSNotification.Name("SHOW_DETAIL_PUBLISH_INFO"), object: info)
    }
}

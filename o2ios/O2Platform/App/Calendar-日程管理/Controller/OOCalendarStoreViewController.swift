//
//  OOCalendarStoreViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/9/22.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class OOCalendarStoreViewController: UITableViewController {
    
    
    private var publicCalendarList: [OOCalendarInfo] = []
    private lazy var viewModel: OOCalendarViewModel = {
        return OOCalendarViewModel()
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.tableFooterView = UIView(frame: CGRect.zero)
        self.viewModel.getPublicCalendarList().then { (list)  in
            self.publicCalendarList = list
            self.tableView.reloadData()
        }.catch{ err in
            DDLogError("请求错误，\(err.localizedDescription)")
            self.showError(title: "获取日历失败！")
        }
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.publicCalendarList.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "calendarStoreTableCell", for: indexPath) as? CalendarStoreTableViewCell {
            cell.setOOCalendarInfo(calendar: self.publicCalendarList[indexPath.row])
            cell.delegate = self
            return cell
        }else {
            return UITableViewCell()
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 48.0
    }
    

    private func setFollow(follow: Bool, id: String) {
        for (idx, c) in (self.publicCalendarList.enumerated()) {
            if c.id == id {
                c.followed = follow
                self.publicCalendarList[idx] = c
                DDLogDebug("设置了。。。\(follow)")
            }
        }
        self.tableView.reloadData()
    }

}

extension OOCalendarStoreViewController: CalendarStoreCellFollowDelegate {
    func follow(calendar: OOCalendarInfo?) {
        if let c = calendar {
            if c.followed == true {
                self.viewModel.followCalendarCancel(id: c.id!).then { (v)  in
                    self.setFollow(follow: false, id: c.id!)
                }.catch { (err) in
                    DDLogError("请求错误，\(err.localizedDescription)")
                    self.showError(title: "取消失败！")
                }
            }else {
                self.viewModel.followCalendar(id: c.id!).then{ v in
                    self.setFollow(follow: true, id: c.id!)
                }.catch { (err) in
                    DDLogError("请求错误，\(err.localizedDescription)")
                    self.showError(title: "关注失败！")
                }
            }
        }
    }
}

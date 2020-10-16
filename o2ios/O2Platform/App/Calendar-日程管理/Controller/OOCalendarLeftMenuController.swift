//
//  OOCalendarLeftMenuController.swift
//  O2Platform
//
//  Created by FancyLou on 2018/8/3.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class OOCalendarLeftMenuController: UITableViewController {
    
    
    var calendarIds:[String] = []
    
    private var myCalendarList: [OOCalendarInfo] = []
    private var departmentCalendarList: [OOCalendarInfo] = []
    private var followCalendarList: [OOCalendarInfo] = []
    private lazy var viewModel: OOCalendarViewModel = {
        return OOCalendarViewModel()
    }()

    @IBOutlet weak var addCalendarBtnView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "日历"
        self.tableView.tableFooterView = UIView(frame: CGRect.zero)
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "日历广场", style: .plain, target: self, action: #selector(openCalendars))
        
        addCalendarBtnView.addTapGesture { (tap) in
            DDLogInfo("点击了新增日历。。。。。。")
            self.performSegue(withIdentifier: "showCalendarSegue", sender: "add")
        }
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        viewModel.getMyCalendarList().then { (calendars) in
            self.myCalendarList = calendars.myCalendars ?? []
            self.departmentCalendarList = calendars.unitCalendars ?? []
            self.followCalendarList = calendars.followCalendars ?? []
            self.loadCalendarIds()
            self.tableView.reloadData()
            }.catch { (error) in
                DDLogError(error.localizedDescription)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 3
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        switch section {
        case 0:
            return myCalendarList.count
        case 1:
            return departmentCalendarList.count
        case 2:
            return followCalendarList.count
        default:
            return 0
        }
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerBase = UIView.init()
        headerBase.frame = CGRect(x: 0, y: 0, width: tableView.frame.width, height: 21)
        headerBase.backgroundColor = UIColor.init(r: CGFloat(240), g: CGFloat(240), b: CGFloat(240))
        let topLine = UIView.init()
        topLine.frame = CGRect(x: 0, y: 0, width: tableView.frame.width, height: 1)
        topLine.backgroundColor = UIColor.lightGray
        headerBase.addSubview(topLine)
        let header = UILabel.init()
        header.frame = CGRect(x: CGFloat(10), y: 1, width: 100, height: 20)
        header.font = setting_item_textFont
        header.theme_textColor = ThemeColorPicker(keyPath: "Base.base_color")
        var title = ""
        switch section {
        case 0:
            title = "我的日历"
        case 1:
            title = "部门日历"
        case 2:
            title = "关注的日历"
        default:
            title = ""
        }
        header.text = title
        headerBase.addSubview(header)
        return headerBase
    }

    /**/
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "calendarTableCell", for: indexPath) as! CalendarTableViewCell
        
        switch indexPath.section {
            case 0:
                cell.renderCalendar(info: self.myCalendarList[indexPath.row], self.calendarIds)
                break
            case 1:
                cell.renderCalendar(info: self.departmentCalendarList[indexPath.row], self.calendarIds)
                break
            case 2:
                cell.renderCalendar(info: self.followCalendarList[indexPath.row], self.calendarIds)
                break
            default:
                DDLogInfo("没有的事。。。。。")
        }
        
        cell.calendarCellDelegate = self
        return cell
    }
 
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        DDLogInfo("点击了table row ，section: \(indexPath.section) ,row: \(indexPath.row)")
        //修改
        if indexPath.section == 0 {
            self.performSegue(withIdentifier: "showCalendarSegue", sender: "update")
        }
        
        if indexPath.section == 1 {
            self.performSegue(withIdentifier: "showCalendarSegue", sender: "updateDept")
        }
        tableView.deselectRow(at: indexPath, animated: false)
    }

    

    /*
    // MARK: - Navigation
     */
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showCalendarSegue" {
            if let type = (sender as? String) {
                if type == "update" {
                    let cc = segue.destination as! OOCalendarViewController
                    let row  = tableView.indexPathForSelectedRow!.row
                    cc.calendarInfo = self.myCalendarList[row]
                }
                
                if type == "updateDept"{
                    let cc = segue.destination as! OOCalendarViewController
                    let row  = tableView.indexPathForSelectedRow!.row
                    cc.calendarInfo = self.departmentCalendarList[row]
                }

            }
        }
    }
 
    //前一页传过来的ids如果有值 和 这边重新从网络获取的数据进行id比较合并
    private func loadCalendarIds() {
        var newCalendarids:[String] = []
        self.myCalendarList.forEach { (c) in
            newCalendarids.append(c.id!)
        }
        self.followCalendarList.forEach { (c) in
            newCalendarids.append(c.id!)
        }
        self.departmentCalendarList.forEach { (c) in
            newCalendarids.append(c.id!)
        }
        
        self.calendarIds.forEach { (id) in
            if !newCalendarids.contains(id) {
                self.calendarIds.removeFirst(id)
            }
        }
        
    }
    //日历广场
    @objc private func openCalendars() {
        self.performSegue(withIdentifier: "showCalendarStore", sender: nil)
    }
    
}

// extension

extension OOCalendarLeftMenuController: CalendarCellSwithOnDelegate {
    func click(isOn: Bool, calendar: OOCalendarInfo?) {
        
        if !self.calendarIds.isEmpty {
            if isOn {
                self.calendarIds.append(calendar!.id!)
            }else {
                self.calendarIds.removeFirst(calendar!.id!)
            }
        }else { //第一次
            var newCalendarids:[String] = []
            self.myCalendarList.forEach { (c) in
                if c.id == calendar?.id  {
                    if isOn {
                        newCalendarids.append(c.id!)
                    }
                }else {
                    newCalendarids.append(c.id!)
                }
            }
            self.followCalendarList.forEach { (c) in
                if c.id == calendar?.id  {
                    if isOn {
                        newCalendarids.append(c.id!)
                    }
                }else {
                    newCalendarids.append(c.id!)
                }
            }
            self.departmentCalendarList.forEach { (c) in
                if c.id == calendar?.id  {
                    if isOn {
                        newCalendarids.append(c.id!)
                    }
                }else {
                    newCalendarids.append(c.id!)
                }
            }
            self.calendarIds = newCalendarids
        }
        NotificationCenter.default.post(name: OONotification.calendarIds.notificationName, object: self.calendarIds)
    }
}

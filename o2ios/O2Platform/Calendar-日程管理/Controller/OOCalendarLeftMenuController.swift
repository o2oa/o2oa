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
        headerBase.frame = CGRect(x: 0, y: 0, w: tableView.frame.width, h: 21)
        headerBase.backgroundColor = UIColor.init(r: CGFloat(240), g: CGFloat(240), b: CGFloat(240))
        let topLine = UIView.init()
        topLine.frame = CGRect(x: 0, y: 0, w: tableView.frame.width, h: 1)
        topLine.backgroundColor = UIColor.lightGray
        headerBase.addSubview(topLine)
        let header = UILabel.init()
        header.frame = CGRect(x: CGFloat(10), y: 1, w: 100, h: 20)
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
            cell.renderCalendar(info: self.myCalendarList[indexPath.row])
            cell.calendarCellDelegate = self
        case 1:
            cell.renderCalendar(info: self.departmentCalendarList[indexPath.row])
        case 2:
            cell.renderCalendar(info: self.followCalendarList[indexPath.row])
        default:
            DDLogInfo("没有的事。。。。。")
        }
        return cell
    }
 
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        DDLogInfo("点击了table row ，section: \(indexPath.section) ,row: \(indexPath.row)")
        //修改
        if indexPath.section == 0 {
            self.performSegue(withIdentifier: "showCalendarSegue", sender: "update")
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
            }
        }
    }
 

}

// extension

extension OOCalendarLeftMenuController: CalendarCellSwithOnDelegate {
    func click(isOn: Bool, calendar: OOCalendarInfo?) {
        DDLogInfo("点击了切换 ison:\(isOn), 日历名称： \(calendar?.name)")
    }
}

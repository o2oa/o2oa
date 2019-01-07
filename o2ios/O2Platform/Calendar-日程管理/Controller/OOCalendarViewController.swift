//
//  OOCalendarViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2018/8/4.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import O2OA_Auth_SDK

class OOCalendarViewController: UITableViewController {
    
    private let colorOptions = ["#428ffc", "#5bcc61",  "#f9bf24",  "#f75f59", "#f180f7", "#9072f1", "#909090", "#1462be"]
    private lazy var viewModel: OOCalendarViewModel = {
        return OOCalendarViewModel()
    }()
    var calendarInfo: OOCalendarInfo?
    private var colorValue = "#428ffc"

    // MARK: - IB
    @IBOutlet weak var calendarNameField: UITextField!
    @IBOutlet weak var calendarIsOpenSwitch: UISwitch!
    @IBOutlet weak var calendarColorStackView: UIStackView!
    @IBOutlet weak var calendarRemarkField: UITextField!
    @IBOutlet weak var calendarDeleteBtn: UIButton!
    @IBAction func deleteBtnTap(_ sender: UIButton) {
        showDefaultConfirm(title: "删除日历", message: "确定要删除当前日历吗，会同时删除该日历下的日程事件？") { (action) in
            self.deleteCalendar()
        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if calendarInfo != nil && calendarInfo?.id != nil {
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "修改", style: .plain, target: self, action: #selector(tapSave))
            self.navigationItem.title = "修改日历"
        }else {
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "保存", style: .plain, target: self, action: #selector(tapSave))
            self.navigationItem.title = "新增日历"
        }
        self.tableView.tableFooterView = UIView(frame: CGRect.zero)
        
        if calendarInfo != nil && calendarInfo?.id != nil {
            loadCalendarInfoFromNet()
            calendarDeleteBtn.isHidden = false
        }else {
            calendarDeleteBtn.isHidden = true
        }
        
        //隐藏输入法
        calendarNameField.delegate = self
        calendarNameField.returnKeyType = .done
        calendarRemarkField.delegate = self
        calendarRemarkField.returnKeyType = .done
        calendarColorStackView?.subviews.forEach({ (colorView) in
            colorView.isUserInteractionEnabled = true
            colorView.addGestureRecognizer(UITapGestureRecognizer(target: self, action:#selector(tapColorView)))
        })
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - private func
    //隐藏输入法
    private func hideKeyboard() {
        self.view.endEditing(true)
    }
    
    @objc func tapSave() {
        hideKeyboard()
        let name = calendarNameField.text
        let isopen = calendarIsOpenSwitch.isOn
        let type = "PERSON"
        let remark = calendarRemarkField.text
        if name == nil || name == "" {
            showError(title: "日历名称不能为空！")
            return
        }
        MBProgressHUD_JChat.showMessage(message: "正在保存...", toView: self.view)
        let calendar = OOCalendarInfo.init()
        calendar.name = name
        calendar.isPublic = isopen
        if let account = O2AuthSDK.shared.myInfo() {
           calendar.target = account.distinguishedName
        }
        calendar.color = colorValue
        calendar.type = type
        calendar.desc = remark ?? ""
        
        if calendarInfo != nil && calendarInfo?.id != nil { // 修改
            calendar.id = calendarInfo?.id!
        }
        viewModel.saveCalendar(calendar: calendar)
            .then { (result) in
                DDLogInfo("保存日历成功！！！\(result)")
                self.closeWindow()
            }.always{
                MBProgressHUD_JChat.hide(forView: self.view, animated: false)
            }.catch { (error) in
                DDLogError(error.localizedDescription)
                self.showError(title: "保存日历错误！")
        }
    }
    private func deleteCalendar() {
        MBProgressHUD_JChat.showMessage(message: "正在删除...", toView: self.view)
        viewModel.deleteCalendar(id: (calendarInfo?.id!)!).then { (result)  in
            DDLogInfo("删除结果：\(result)")
            self.closeWindow()
            }.always{
                MBProgressHUD_JChat.hide(forView: self.view, animated: false)
            }.catch { (error) in
                DDLogError(error.localizedDescription)
                self.showError(title: "删除日历错误！")
        }
    }
    
    @objc func tapColorView(_ tap: UITapGestureRecognizer) {
        hideKeyboard()
        if let tag = tap.view?.tag {
            selectColorView(tag: tag)
        }
    }
    //选择颜色模块
    private func selectColorView(tag: Int) {
        colorValue = colorOptions[tag]
        calendarColorStackView.subviews.forEach { (colorView) in
            if colorView.tag == tag {
                colorView.subviews[0].isHidden = false
            }else {
                colorView.subviews[0].isHidden = true
            }
        }
    }
    
    
    private func loadCalendarInfoFromNet() {
        viewModel.getCalendar(id: (calendarInfo?.id)!)
            .then { (calendar) in
                self.updateStuffValue(calendar: calendar)
            }.catch { (error) in
                DDLogError("查询日历信息出错，\(error.localizedDescription)")
        }
    }
    private func updateStuffValue(calendar: OOCalendarInfo) {
        calendarNameField.text = calendar.name
        calendarIsOpenSwitch.setOn(calendar.isPublic == true, animated: true)
        if let color = calendar.color {
            if let index = colorOptions.index(where: { (colorItem) -> Bool in
                return colorItem == color
            }) {
                self.selectColorView(tag: index)
            }
        }
        calendarRemarkField.text = calendar.desc
    }
    

    
    private func closeWindow() {
        self.navigationController?.popViewController(animated: true)
    }

    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //取消tableview选中状态
        tableView.deselectRow(at: indexPath, animated: false)
        // 隐藏输入法
        hideKeyboard()
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension OOCalendarViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        return textField.resignFirstResponder()
    }
}

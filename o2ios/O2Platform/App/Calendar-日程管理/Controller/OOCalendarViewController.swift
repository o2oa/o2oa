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

    private let colorOptions = ["#428ffc", "#5bcc61", "#f9bf24", "#f75f59", "#f180f7", "#9072f1", "#909090", "#1462be"]
    private lazy var viewModel: OOCalendarViewModel = {
        return OOCalendarViewModel()
    }()
    var calendarInfo: OOCalendarInfo?
    private var colorValue = "#428ffc"
    private var count = 0

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 3
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        if section == 0 {
            return 5
        } else if section == 1 {
            return count
        } else {
            return 1
        }
    }


    // MARK: - IB
    @IBOutlet weak var calendarNameField: UITextField!
    @IBOutlet weak var calendarIsOpenSwitch: UISwitch!
    @IBOutlet weak var calendarColorStackView: UIStackView!
    @IBOutlet weak var calendarRemarkField: UITextField!
    @IBOutlet weak var calendarDeleteBtn: UIButton!


    @IBOutlet weak var calendarTypeField: UITextField!
    @IBOutlet weak var calendarOrgField: UITextField!
    @IBOutlet weak var calendarManagerField: UITextField!
    @IBOutlet weak var calendarScopeField: UITextField!
    @IBOutlet weak var calendarNewScopeField: UITextField!

    @IBOutlet weak var calendarStatusSwitch: UISwitch!

    @IBOutlet weak var calendarIsOpenBtn: UIButton!

    @IBAction func editRemarkBtn(_ sender: Any) {
        self.performSegue(withIdentifier: "ShowEditRemark", sender: nil)
    }




    @IBAction func deleteBtnTap(_ sender: UIButton) {
        showDefaultConfirm(title: "删除日历", message: "确定要删除当前日历吗，会同时删除该日历下的日程事件？") { (action) in
            self.deleteCalendar()
        }
    }

    //选择是否公开
    @IBAction func selectType(_ sender: Any) {
        let alertController = UIAlertController(title: "请选择类型", message: "", preferredStyle: .actionSheet)
        var selectStyle = UIAlertAction.Style.default
        selectStyle = (calendarTypeField.text == "个人日历") ? UIAlertAction.Style.default : UIAlertAction.Style.destructive
        let personAction = UIAlertAction(title: "个人日历", style: selectStyle, handler: { action in
            self.calendarTypeField.text = action.title
            self.count = 0
            let section = NSIndexSet(index: 1)
            self.tableView.beginUpdates()
            self.tableView.reloadSections(section as IndexSet, with: .none)
            self.tableView.endUpdates()
        })
        selectStyle = (calendarTypeField.text == "组织日历") ? UIAlertAction.Style.default : UIAlertAction.Style.destructive
        let orgAction = UIAlertAction(title: "组织日历", style: selectStyle, handler: { action in
            self.calendarTypeField.text = action.title
            self.count = 4
            let section = NSIndexSet(index: 1)
            self.tableView.beginUpdates()
            self.tableView.reloadSections(section as IndexSet, with: .none)
            self.tableView.endUpdates()
        })
        alertController.addAction(personAction)
        alertController.addAction(orgAction)
        self.present(alertController, animated: true, completion: nil)
    }



    override func viewDidLoad() {
        super.viewDidLoad()
        if calendarInfo != nil && calendarInfo?.id != nil {
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "修改", style: .plain, target: self, action: #selector(tapSave))
            self.navigationItem.title = "修改日历"
        } else {
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "保存", style: .plain, target: self, action: #selector(tapSave))
            self.navigationItem.title = "新增日历"
        }
        self.tableView.tableFooterView = UIView(frame: CGRect.zero)

        if calendarInfo != nil && calendarInfo?.id != nil {
            loadCalendarInfoFromNet()
            calendarDeleteBtn.isHidden = false
        } else {
            self.calendarInfo = OOCalendarInfo.init()
            calendarDeleteBtn.isHidden = true
        }


        //隐藏输入法
        calendarNameField.delegate = self
        calendarNameField.returnKeyType = .done

        calendarTypeField.delegate = self
        calendarTypeField.returnKeyType = .done

        calendarRemarkField.delegate = self
        calendarRemarkField.returnKeyType = .done

        calendarColorStackView?.subviews.forEach({ (colorView) in
            colorView.isUserInteractionEnabled = true
            colorView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(tapColorView)))
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

        var type = "PERSON"
        if let typeValue = calendarTypeField.text {
            if typeValue == "个人日历" {
                type = "PERSON"
            } else {
                type = "UNIT"
            }
        }
        let remark = calendarRemarkField.text
        if name == nil || name == "" {
            showError(title: "日历名称不能为空！")
            return
        }

        MBProgressHUD_JChat.showMessage(message: "正在保存...", toView: self.view)
        /*
      var calendar = self.calendarInfo
      if calendarInfo != nil && calendarInfo?.id != nil { // 修改
          //calendar.id = calendarInfo?.id!
          calendar = calendarInfo ?? OOCalendarInfo.init()
      }*/

        self.calendarInfo!.name = name
        self.calendarInfo!.isPublic = isopen

        if type == "PERSON" {
            if let account = O2AuthSDK.shared.myInfo() {
                self.calendarInfo!.target = account.distinguishedName
            }
        }
        self.calendarInfo!.color = colorValue
        self.calendarInfo!.type = type
        self.calendarInfo!.desc = remark ?? ""

        // calendar.status=="OPEN",
        if calendarStatusSwitch.isOn {
            self.calendarInfo!.status = "OPEN"
        } else {
            self.calendarInfo!.status = "CLOSE"
        }

        viewModel.saveCalendar(calendar: self.calendarInfo!)
            .then { (result) in
                DDLogInfo("保存日历成功！！！\(result)")
                self.closeWindow()
            }.always {
                MBProgressHUD_JChat.hide(forView: self.view, animated: false)
            }.catch { (error) in
                DDLogError(error.localizedDescription)
                self.showError(title: "保存日历错误！")
        }

    }
    private func deleteCalendar() {
        MBProgressHUD_JChat.showMessage(message: "正在删除...", toView: self.view)
        viewModel.deleteCalendar(id: (calendarInfo?.id!)!).then { (result) in
            DDLogInfo("删除结果：\(result)")
            self.closeWindow()
        }.always {
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
            } else {
                colorView.subviews[0].isHidden = true
            }
        }
    }
    //选择所属组织
    @IBAction func selectOrg(_ sender: Any) {
        let arrModes = [ContactPickerType.unit]
        showContactPicker(modes: arrModes, callback: {
            (O2BizContactPickerResult) in
            if let depts = O2BizContactPickerResult.departments {
                var allDept = ""
                var target = ""
                for dept in depts {
                    if allDept == "" {
                        allDept = dept.name ?? ""
                    } else {
                        allDept = allDept + "," + (dept.name ?? "")
                    }
                    target = dept.distinguishedName ?? ""
                }

                self.calendarOrgField.text = allDept
                self.calendarInfo?.target = target
            }
        })
    }
    //选择管理者
    @IBAction func selectManager(_ sender: Any) {
        let arrModes = [ContactPickerType.person]
        showContactPicker(modes: arrModes, callback: {
            (O2BizContactPickerResult) in
            if let users = O2BizContactPickerResult.users {
                var allUser = ""
                var manageablePersonList = [String]()
                for user in users {
                    if allUser == "" {
                        allUser = user.name ?? ""
                    } else {
                        allUser = allUser + "," + (user.name ?? "")

                    }
                    if let distinguishedName = user.distinguishedName {
                        manageablePersonList.append(distinguishedName)
                    }
                }
                self.calendarManagerField.text = allUser
                self.calendarInfo?.manageablePersonList = manageablePersonList
            }
        })
    }


    //选择可见范围
    @IBAction func selectScope(_ sender: Any) {
        var allUser = ""
        var allDept = ""
        var allGroup = ""
        var all = ""
        var viewablePersonList = [String]()
        var viewableUnitList = [String]()
        var viewableGroupList = [String]()
        let arrModes = [ContactPickerType.person, ContactPickerType.unit, ContactPickerType.group]
        showContactPicker(modes: arrModes, callback: {
            (O2BizContactPickerResult) in
            if let users = O2BizContactPickerResult.users {
                for user in users {
                    if allUser == "" {
                        allUser = user.name ?? ""
                    } else {
                        allUser = allUser + "," + (user.name ?? "")
                    }
                    if let distinguishedName = user.distinguishedName {
                        viewablePersonList.append(distinguishedName)
                    }
                }
                all = allUser
            }

            if let depts = O2BizContactPickerResult.departments {
                for dept in depts {
                    if allDept == "" {
                        allDept = dept.name ?? ""
                    } else {
                        allDept = allDept + "," + (dept.name ?? "")
                    }

                    if let distinguishedName = dept.distinguishedName {
                        viewableUnitList.append(distinguishedName)
                    }

                }
                if "" == all {
                    all = allDept
                } else {
                    if "" != allDept {
                        all = all + "," + allDept
                    }
                }
            }

            if let groups = O2BizContactPickerResult.groups {
                for group in groups {
                    if allGroup == "" {
                        allGroup = group.name ?? ""
                    } else {
                        allGroup = allGroup + "," + (group.name ?? "")
                    }

                    if let distinguishedName = group.distinguishedName {
                        viewableGroupList.append(distinguishedName)
                    }
                }
                if "" == all {
                    all = allGroup
                } else {
                    if "" != allGroup {
                        all = all + "," + allGroup
                    }
                }
            }

            self.calendarScopeField.text = all
            self.calendarInfo?.viewablePersonList = viewablePersonList
            self.calendarInfo?.viewableUnitList = viewableUnitList
            self.calendarInfo?.viewableGroupList = viewableGroupList
        })
    }

    //可新建范围
    @IBAction func selectNewScope(_ sender: Any) {
        var allUser = ""
        var allDept = ""
        var allGroup = ""
        var all = ""
        var publishablePersonList = [String]()
        var publishableGroupList = [String]()
        var publishableUnitList = [String]()
        let arrModes = [ContactPickerType.person, ContactPickerType.unit, ContactPickerType.group]
        showContactPicker(modes: arrModes, callback: {
            (O2BizContactPickerResult) in
            if let users = O2BizContactPickerResult.users {
                for user in users {
                    if allUser == "" {
                        allUser = user.name ?? ""
                    } else {
                        allUser = allUser + "," + (user.name ?? "")
                    }

                    if let distinguishedName = user.distinguishedName {
                        publishablePersonList.append(distinguishedName)
                    }
                }
                all = allUser
            }

            if let depts = O2BizContactPickerResult.departments {
                for dept in depts {
                    if allDept == "" {
                        allDept = dept.name ?? ""
                    } else {
                        allDept = allDept + "," + (dept.name ?? "")
                    }

                    if let distinguishedName = dept.distinguishedName {
                        publishableUnitList.append(distinguishedName)
                    }
                }
                if "" == all {
                    all = allDept
                } else {
                    if "" != allDept {
                        all = all + "," + allDept
                    }
                }
            }

            if let groups = O2BizContactPickerResult.groups {
                for group in groups {
                    if allGroup == "" {
                        allGroup = group.name ?? ""
                    } else {
                        allGroup = allGroup + "," + (group.name ?? "")
                    }

                    if let distinguishedName = group.distinguishedName {
                        publishableGroupList.append(distinguishedName)
                    }
                }
                if "" == all {
                    all = allGroup
                } else {
                    if "" != allGroup {
                        all = all + "," + allGroup
                    }
                }
            }
            self.calendarNewScopeField.text = all
            self.calendarInfo?.publishablePersonList = publishablePersonList
            self.calendarInfo?.publishableUnitList = publishableUnitList
            self.calendarInfo?.publishableGroupList = publishableGroupList
        })
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

        if calendar.type == "UNIT" {
            self.count = 4;
        } else {
            self.count = 0;
        }
        let section = NSIndexSet(index: 1)
        self.tableView.beginUpdates()
        self.tableView.reloadSections(section as IndexSet, with: .automatic)
        self.tableView.endUpdates()
        //所属组织
        calendarOrgField.text = calendar.target?.getChinaName()
        //管理员
        calendarManagerField.text = calendar.manageablePersonList?.getChinaName().joined(separator: ",")

        //可见范围
        var viewablePersonList = [String]()
        viewablePersonList += calendar.viewablePersonList ?? []
        viewablePersonList += calendar.viewableUnitList ?? []
        viewablePersonList += calendar.viewableGroupList ?? []
        calendarScopeField.text = viewablePersonList.getChinaName().joined(separator: ",")

        //可新建范围
        var publishableList = [String]()
        publishableList += calendar.publishablePersonList ?? []
        publishableList += calendar.publishableGroupList ?? []
        publishableList += calendar.publishableUnitList ?? []
        calendarNewScopeField.text = publishableList.getChinaName().joined(separator: ",")

        if calendar.type == "PERSON" {
            calendarTypeField.text = "个人日历"
        } else {
            calendarTypeField.text = "组织日历"
        }

        calendarStatusSwitch.setOn(calendar.status == "OPEN", animated: true)
        calendarIsOpenSwitch.setOn(calendar.isPublic == true, animated: true)
        //隐藏类型选择按钮
        calendarIsOpenBtn.isHidden = true

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
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        if let Identifier = textField.accessibilityIdentifier {
            if Identifier == "calendarType" {
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        //return textField.resignFirstResponder()
        let res = textField.resignFirstResponder()
        return false

    }

}

//
//  ICAppealFormViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/25.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import Eureka
import SwiftyJSON

import CocoaLumberjack
import O2OA_Auth_SDK

class ICAppealFormViewController: FormViewController {
    
    
    
    var detailData:AttendanceDetailData?
    
    var entry:AttendanceAppealInfoEntry?
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //读取直接主管
        self.loadProcessPerson()
        self.initFormStyle()
        self.initFormValue()
        
    }
    
    func initFormValue(){
        
        form +++ Section()
            <<< LabelRow("empName"){
                row in
                row.title = "姓名"
                row.value = self.detailData?.empName
            }
            <<< LabelRow("appealDate"){
                row in
                row.title = "考勤日期"
                row.value = self.detailData?.recordDateString
            }
            <<< LabelRow("onDutyTime"){
                row in
                row.title = "上班打卡时间"
                row.value = self.detailData?.onDutyTime
            }
            <<< LabelRow("offDutyTime"){
                row in
                row.title = "下班打卡时间"
                row.value = self.detailData?.offDutyTime
            }
            <<< LabelRow("statusType"){
                row in
                row.title = "考勤状态"
                let t = calcAttendanceStatus(attendance: self.detailData!)
                row.value  = t.statusType.rawValue
            }
            <<< LabelRow("appealStatus"){
                row in
                row.title = "审批状态"
                row.value = "发起"
            }
            <<< SegmentedRow<String>("appealType") {
                $0.title = "申诉类型"
                $0.options = ["临时请假", "出差", "因公外出","其他"]
                $0.value = ""
            }
            +++ Section(header:"临时请假",footer:"") {
                $0.tag = "a1"
                $0.hidden = "$appealType!='临时请假'"
            }
            
            <<< SegmentedRow<String>("selfHolidayType"){
                $0.title = "请假类型"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesOnChangeAfterBlurred
                $0.options = ["带薪年休假","带薪病假","带薪福利假","扣薪事假","其他"]
                $0.value = ""
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.titleLabel?.textColor = .red
                    }
            }
            <<< DateRow("startTime"){
                $0.title = "开始日期"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.textLabel?.textColor = .red
                    }
            }
            <<< DateRow("endTime"){
                $0.title = "结束日期"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.textLabel?.textColor = .red
                    }
            }
            +++ Section(header:"出差",footer:"") {
                $0.tag = "a2"
                $0.hidden = "$appealType!='出差'"
            }
            <<< TextRow("address1"){
                $0.title = "地点"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.titleLabel?.textColor = .red
                    }
            }
            <<< DateRow("startTime1"){
                $0.title = "开始日期"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.textLabel?.textColor = .red
                    }
            }
            
            <<< DateRow("endTime1"){
                $0.title = "结束日期"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.textLabel?.textColor = .red
                    }
            }
            
            
            +++ Section(header:"因公外出",footer:"") {
                $0.tag = "a3"
                $0.hidden = "$appealType!='因公外出'"
            }
            <<< TextRow("address2"){
                $0.title = "地点"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.titleLabel?.textColor = .red
                    }
            }
            <<< DateRow("startTime2"){
                $0.title = "开始日期"
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.textLabel?.textColor = .red
                    }
            }
            <<< DateRow("endTime2"){
                $0.title = "结束日期"
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.textLabel?.textColor = .red
                    }
            }
            <<< TextRow("appealReson2"){
                $0.title = "事由"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.titleLabel?.textColor = .red
                    }
            }
            +++ Section(header:"其他",footer:"") {
                $0.tag = "a4"
                $0.hidden = "$appealType!='其他'"
            }
            <<< TextRow("appealReson3"){
                $0.title = "事由"
                $0.add(rule: RuleRequired())
                $0.validationOptions = .validatesAlways
                }.cellUpdate { cell, row in
                    if !row.isValid {
                        cell.titleLabel?.textColor = .red
                    }
        }
        

    }
    
    func initFormStyle(){
        
        LabelRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            //cell.accessoryType = .disclosureIndicator
        }
        EmailRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            //cell.accessoryType = .disclosureIndicator
            
        }
        
        PhoneRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            //cell.accessoryType = .disclosureIndicator
        }
        
        TextRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        
        DateRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        
        ButtonRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_item_textFont
            cell.textLabel?.theme_textColor = ThemeColorPicker(keyPath: "Base.base_color")
            
        }
        
        ActionSheetRow<String>.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor =  setting_content_textColor
        }
        
        SegmentedRow<String>.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor =  setting_content_textColor
            cell.segmentedControl.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
            cell.segmentedControl.tintColor = UIColor.white
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func loadProcessPerson(){
        let account = O2AuthSDK.shared.myInfo()
        self.entry = AttendanceAppealInfoEntry()
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.personDirectManageQuery, parameter: ["##attribute##":"直接主管" as AnyObject,"##name##":account?.name as AnyObject])
        Alamofire.request(url!, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    let data = JSON(val)["data"]
                    let name = data["attributeList"]
                    self.entry?.processPerson1 = name.array![0].stringValue
                }else{
                    DDLogError(JSON(val).description)
                    self.showError(title: "初始化主管审批人错误")

                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "初始化主管审批人错误")
            }
        }
    }
    
    //返回是否已经获取正常的参数
    func getSubmitEntry() -> Bool {
        let sRow:SegmentedRow<String> = form.rowBy(tag: "appealType")!
        var result = false
        result = sRow.isValid
        if sRow.isValid {
            self.entry?.appealReason = sRow.value
            if sRow.value == "临时请假" {
                let a1Row1:SegmentedRow<String> = form.rowBy(tag:"selfHolidayType")!
                let a1Row2:DateRow = form.rowBy(tag: "startTime")!
                let a1Row3:DateRow = form.rowBy(tag: "endTime")!
                result = a1Row1.isValid && a1Row2.isValid && a1Row3.isValid && a1Row1.value != nil &&  a1Row2.value != nil && a1Row3.value != nil
                if result {
                    entry?.appealDescription = a1Row1.value
                    entry?.startTime = a1Row2.value?.toString(format: "yyyy-MM-dd")
                    entry?.endTime = a1Row3.value?.toString(format: "yyyy-MM-dd")
                }
            }else if sRow.value == "出差" {
                let a2Row1:TextRow = form.rowBy(tag: "address1")!
                let a2Row2:DateRow = form.rowBy(tag: "startTime1")!
                let a2Row3:DateRow = form.rowBy(tag: "endTime1")!
                result = a2Row1.isValid && a2Row2.isValid && a2Row3.isValid && a2Row1.value != nil && a2Row2.value != nil && a2Row3.value != nil
                if result {
                    entry?.address = a2Row1.value
                    entry?.startTime  = a2Row2.value?.toString(format: "yyyy-MM-dd")
                    entry?.endTime = a2Row3.value?.toString(format: "yyyy-MM-dd")
                }
            }else if sRow.value == "因公外出" {
                let a3Row4:TextRow = form.rowBy(tag: "appealReson2")!
                let a3Row1:TextRow = form.rowBy(tag: "address2")!
                let a3Row2:DateRow = form.rowBy(tag: "startTime2")!
                let a4Row3:DateRow = form.rowBy(tag: "endTime2")!
                result = a3Row1.isValid && a3Row2.isValid && a4Row3.isValid && a3Row4.isValid && a3Row1.value != nil && a3Row2.value != nil && a4Row3.value != nil && a3Row4.value != nil
                if result {
                    entry?.appealDescription = a3Row4.value
                    entry?.address = a3Row1.value
                    entry?.startTime = a3Row2.value?.toString(format: "yyyy-MM-dd")
                    entry?.endTime = a4Row3.value?.toString(format: "yyyy-MM-dd")
                }
            }else if sRow.value == "其他" {
                let a4Row1:TextRow = form.rowBy(tag: "appealReson3")!
                result = a4Row1.isValid && a4Row1.value != nil
                if result {
                    entry?.appealDescription = a4Row1.value
                }
            }
        }else{
            
        }
        return result
    }
    
    
    @IBAction func submitAppealAction(_ sender: UIBarButtonItem) {
        if getSubmitEntry() == true {
        //设置参数
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(icContext.icContextKey, query: icContext.attendanceInfoQuery, parameter: ["##id##":detailData?.id as AnyObject])
            Alamofire.request(url!, method: .put, parameters: self.entry?.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
                switch response.result {
                case .success(let val):
                    let type = JSON(val)["type"]
                    if type == "success" {
                        self.showSuccess(title: "提交成功")
                        self.backDetail()
                    }else{
                        DDLogError(JSON(val).description)
                        self.showError(title: "申诉提交错误")
                    }
                case .failure(let err):
                    DDLogError(err.localizedDescription)
                    self.showError(title: "申诉提交错误")
                }
            }
        }else{
            self.showError(title: "请设置相应的参数")
        }
        
    }
    
    func backDetail(){
         self.performSegue(withIdentifier: "unBackDetailSegue", sender: nil)
    }
    

    
}

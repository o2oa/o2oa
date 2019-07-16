//
//  ICDetailDisplayViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/1.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Eureka

///显示指定的考核视图
class ICDetailDisplayViewController: FormViewController {
    
    var detailData:AttendanceDetailData?

    override func viewDidLoad() {
        super.viewDidLoad()
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
                $0.value = self.detailData?.appealReason
            }
            +++ Section(header:"临时请假",footer:"") {
                $0.tag = "a1"
                $0.hidden = "$appealType!='临时请假'"
            }
            
            <<< LabelRow("selfHolidayType"){
                $0.title = "请假类型"
                $0.value = self.detailData?.appealDescription
            }
            
            <<< LabelRow("startTime"){
                $0.title = "开始日期"
                $0.value = self.detailData?.startTime
            }
            
            <<< LabelRow("endTime"){
                $0.title = "结束日期"
                $0.value = self.detailData?.endTime
            }
            
            
            +++ Section(header:"出差",footer:"") {
                $0.tag = "a2"
                $0.hidden = "$appealType!='出差'"
            }
            
            <<< LabelRow("address1"){
                $0.title = "地点"
                $0.value = self.detailData?.address
            }
            
            <<< LabelRow("startTime1"){
                $0.title = "开始日期"
                $0.value = self.detailData?.startTime
            }
            
            <<< LabelRow("endTime1"){
                $0.title = "结束日期"
                $0.value = self.detailData?.endTime
            }
            
            
            +++ Section(header:"因公外出",footer:"") {
                $0.tag = "a3"
                $0.hidden = "$appealType!='因公外出'"
            }
            <<< LabelRow("address2"){
                $0.title = "地点"
                $0.value = self.detailData?.address
            }
            
            <<< LabelRow("startTime2"){
                $0.title = "开始日期"
                $0.value = self.detailData?.startTime
            }
            
            <<< LabelRow("endTime2"){
                $0.title = "结束日期"
                $0.value = self.detailData?.endTime
            }
            
            <<< LabelRow("appealReson2"){
                $0.title = "事由"
                $0.value = self.detailData?.appealDescription
            }

            +++ Section(header:"其他",footer:"") {
                $0.tag = "a4"
                $0.hidden = "$appealType!='其他'"
            }
            
            <<< LabelRow("appealReson3"){
                $0.title = "事由"
                $0.value = self.detailData?.appealDescription
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
    
    @IBAction func closeDisplayAction(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

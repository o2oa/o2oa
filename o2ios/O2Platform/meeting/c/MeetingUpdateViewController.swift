//
//  MeetingUpdateViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/11.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import SwiftyUserDefaults
import Eureka
import CocoaLumberjack


class MeetingUpdateViewController: FormViewController {
    
    
    var persons:[PersonV2] = []
    
    var sRoom:Room?
    
    var meetingForm = MeetingForm()
    
    var meeting:Meeting?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //设置form
        self.meetingForm.subject = self.meeting?.subject
        self.meetingForm.desc  = self.meeting?.desc
        self.meetingForm.meetingDate  = SharedDateUtil.dateFromString(string:(self.meeting?.startTime)!, withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime) as Date
        self.meetingForm.startTime = SharedDateUtil.dateFromString(string:(self.meeting?.startTime)!, withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime) as Date
        self.meetingForm.completedTime = SharedDateUtil.dateFromString(string:(self.meeting?.completedTime)!, withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime) as Date
        //读取会议室及名称
        self.loadRoomInfo((self.meeting?.room)!)
        //读取人员信息
        self.loadPersonInfo((self.meeting?.invitePersonList)!)
        self.meetingForm.invitePersonList = (meeting?.invitePersonList)!
        //初始化UI
        self.setupUI()
        
    }
    
    func loadRoomInfo(_ roomId:String){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.roomItemIdQuery, parameter: ["##id##":roomId as AnyObject])
        Alamofire.request(url!).responseObject(queue: nil, keyPath: "data", mapToObject: nil, context: nil, completionHandler: { (resp:DataResponse<Room>) in
            switch resp.result {
            case .success(let room):
                self.sRoom = room
                self.meetingForm.room = room.id
                self.meetingForm.roomName = room.name
                let row:RowOf<String>  = self.form.rowBy(tag:"showRoomName")!
                row.value = room.name
                row.title = ""
                //row.hidden = false
                row.updateCell()
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        })
    }
    
    func loadPersonInfo(_ names:[String]){
        names.forEachEnumerated { (index,name) in
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personInfoByNameQuery, parameter: ["##name##":name as AnyObject])
            Alamofire.request(url!).responseObject(queue: nil,keyPath:"data", completionHandler: { (resp:DataResponse<PersonV2>) in
                switch resp.result {
                case .success(let person):
                    self.persons.append(person)
                case .failure(let err):
                    DDLogError(err.localizedDescription)
                }
            })
        }
        
    }
    
    func setupUI(){
        ImageRow.defaultCellUpdate = { cell, row in
            cell.accessoryView?.layer.cornerRadius = 17
            cell.accessoryView?.frame = CGRect(x: 0, y: 0, width: 34, height: 34)
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
        }
        
        TextRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            cell.accessoryType = .disclosureIndicator
        }
        
        LabelRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            cell.accessoryType = .disclosureIndicator
        }
        
        ButtonRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            cell.accessoryType = .disclosureIndicator
        }
        
        TextAreaRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            //cell.accessoryType = .disclosureIndicator
        }
        
        EmailRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            cell.accessoryType = .disclosureIndicator
            
        }
        
        DateRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            //cell.accessoryType = .disclosureIndicator
        }
        
        TimeRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            //cell.accessoryType = .disclosureIndicator
        }
        form +++ Section("会议内容")
            <<< TextRow("subject"){ row in
                row.title = "会议主题"
                row.placeholder = "请输入主题"
                row.value = self.meetingForm.subject
                }.onChange({ row in
                    self.meetingForm.subject = row.value
                })
            <<< DateRow("startDate"){ row in
                row.title = "会议日期"
                row.value = self.meetingForm.meetingDate
                }.onChange({ row in
                    self.meetingForm.meetingDate = row.value!
                })
            <<< TimeRow("startTime"){ row in
                row.title = "开始时间"
                row.value = self.meetingForm.startTime
                }.onChange({ row in
                    self.meetingForm.startTime = row.value!
                })
            <<< TimeRow("completeTime") { row in
                row.title = "完成时间"
                row.value = self.meetingForm.completedTime
                }.onChange({ row in
                    self.meetingForm.completedTime = row.value!
                })
            <<< ButtonRow("roomName") { row in
                row.title = "选择会议室"
                row.presentationMode = .segueName(segueName: "showUpdateMeetingRoomSegue",onDismiss:nil)
                
            }
            
            <<< LabelRow("showRoomName"){ row in
                row.title = "请选择会议室"
                }.cellSetup({ (cell, row) in
                    cell.detailTextLabel?.textColor = UIColor.blue
                    cell.detailTextLabel?.font = UIFont.italicSystemFont(ofSize: 10)
                })
            <<< ButtonRow("invitePersonList") { row in
                row.title = "选择人员"
                row.presentationMode = .segueName(segueName:"showUpdatePersonListSegue",onDismiss:nil)
            }
            <<< LabelRow("showPersonList"){ row in
                row.title = "请选择参会人员"
                var pShow:[String] = []
                for p in self.meetingForm.invitePersonList {
                    pShow.append(p.split("@")[0])
                }
                row.value = pShow.joined(separator: ",")
                }
            
            +++ Section("会议说明")
            <<< TextAreaRow("desc") { row in
                row.title = "会议说明"
                row.value = self.meetingForm.desc
                }.onChange({ row in
                    self.meetingForm.desc = row.value
                })
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //push前执行
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        //选择会议室执行
        if segue.identifier == "showUpdateMeetingRoomSegue" {
            let navVC = segue.destination as! UINavigationController
            let destVC = navVC.topViewController as! MeetingRoomListViewController
            destVC.delegate = self
            if let room = sRoom {
                destVC.selectRoom = room
            }
        }else if segue.identifier  == "showUpdatePersonListSegue" {
            //选择人员前执行
            let destVC = segue.destination as! MeetingPersonListViewController
            destVC.delegate = self
            destVC.selectPersons = persons
//
//            if let ps = persons {
//                destVC.selectPersons = ps
//            }
        }
    }
    
    
    @IBAction func submitMeetingAction(_ sender: UIBarButtonItem) {
        let meetingFormBean  = MeetingFormBean(meetingForm: self.meetingForm)
        self.meeting?.subject = meetingFormBean.subject
        self.meeting?.desc = meetingFormBean.description
        self.meeting?.startTime  = meetingFormBean.startTime
        self.meeting?.completedTime = meetingFormBean.completedTime
        self.meeting?.invitePersonList = meetingFormBean.invitePersonList
        self.meeting?.room  = meetingFormBean.room
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.meetingItemIdQuery, parameter: ["##id##":(meeting?.id)! as AnyObject])
        ProgressHUD.show("更新会议中...")
        Alamofire.request(url!,method:.put, parameters: meeting?.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { response in
            switch response.result {
            case .success(let val):
                let json = JSON(val)
                if json["type"] == "success" {
                    DDLogDebug(json.description)
                    ProgressHUD.showSuccess("更新成功")
                    self.performSegue(withIdentifier: "updateBackMainSegue", sender: nil)
                }else {
                    DDLogError(json.description)
                    ProgressHUD.showError("更新失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                ProgressHUD.showError("更新失败")
            }
        }
    }
    
    
}

extension MeetingUpdateViewController:MeetingRoomPassValueDelegate,MeetingPersonListPassValue{
    //选择的会议室
    func selectRoom(_ room: Room) {
        self.sRoom = room
        self.meetingForm.room = room.id
        self.meetingForm.roomName = room.name
        let row:RowOf<String>  = self.form.rowBy(tag:"showRoomName")!
        row.value = room.name
        row.title = ""
        //row.hidden = false
        row.updateCell()
    }
    
    //选择的参会人员
    func selectPersonPassValue(_ persons: [PersonV2]) {
        self.persons = persons
        self.meetingForm.invitePersonList.removeAll(keepingCapacity: true)
        self.persons.forEach({ (p:PersonV2) in
            self.meetingForm.invitePersonList.append(p.distinguishedName!)
        })
        let row:RowOf<String> = self.form.rowBy(tag: "showPersonList")!
        var pShow:[String] = []
        for p in self.meetingForm.invitePersonList {
            pShow.append(p.split("@")[0])
        }
        row.value = pShow.joined(separator: ",")
        if  persons.isEmpty  {
            row.title = "请选择参会人员"
        }else {
            row.title = ""
        }
        row.updateCell()
    }
}

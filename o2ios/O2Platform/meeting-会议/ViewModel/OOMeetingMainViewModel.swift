//
//  OOMeetingMainViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/17.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit
import Promises



class OOMeetingMainViewModel: NSObject {
    //HTTP API
    private let o2MeetingAPI = OOMoyaProvider<O2MeetingAPI>()
    //所有本月所有会议
    private var meetingsByMonth:[OOMeetingInfo] = [] {
        didSet {
            meetingsByMonth.forEach { (item) in
                let startDate = String((item.startTime?.split(separator: " ").first)!)
                if var meetings = meetingsByMonthForDict[startDate] {
                    meetings?.append(item)
                }else{
                    let meetings:[OOMeetingInfo] = [item]
                    meetingsByMonthForDict[startDate] = meetings
                }
            }
        }
    }
    
    //本月所有会议按日期生成key,value
    private var meetingsByMonthForDict:[String:[OOMeetingInfo]?] = [:]
    
    //指定日期的所有会议
    var theMeetingsByDay:[OOMeetingInfo] = []
    
    //回调块类型定义
    typealias CallbackBlockDefine = (_ msg:String?) -> Void
    //回调块定义
    var callbackExecutor:CallbackBlockDefine?
    
    override init() {
        super.init()
    }
    
}



extension OOMeetingMainViewModel {
    
    // MARK:- 读取会议室信息
    func loadMeetingRoomById(_ roomId:String) -> Promise<OOMeetingRoomInfo> {
        return Promise { fulfill,reject in
            self.o2MeetingAPI.request(.roomItemById(roomId)) { (result) in
                let myResult = OOResult<BaseModelClass<OOMeetingRoomInfo>>(result)
                if myResult.isResultSuccess() {
                    if let item = myResult.model?.data {
                        fulfill(item)
                    }else{
                        let customError = OOAppError.common(type: "MeetingRoom load Error", message: "会议室信息读取错误", statusCode: 7001)
                        reject(customError)
                    }
                    
                }else{
                    reject(myResult.error!)
                }
            }
        }
    }
    
    func loadMeetingRoomById(_ roomId:String,completed:@escaping (_ room:OOMeetingRoomInfo?) -> Void){
        o2MeetingAPI.request(.roomItemById(roomId)) { (result) in
            let myResult = OOResult<BaseModelClass<OOMeetingRoomInfo>>(result)
            if myResult.isResultSuccess() {
                let item = myResult.model?.data
                completed(item)
            }else{
                completed(nil)
            }
        }
        
    }
    
    // MARK:- 按月读取会议信息
    func getMeetingsByYearAndMonth(_ theDate:Date) -> Promise<[String:[OOMeetingInfo]?]?> {
        let strYear = String(theDate.year)
        let strMonth = String(theDate.month)
        return Promise { fulfill,reject  in
            self.o2MeetingAPI.request(.meetingListByYearMonth(strYear, strMonth)) { (result) in
                let myResult = OOResult<BaseModelClass<[OOMeetingInfo]>>(result)
                if myResult.isResultSuccess() {
                    if let model = myResult.model?.data {
                        model.forEach({ (item) in
                            self.meetingsByMonth.append(item)
                        })
                    }
                    fulfill(self.meetingsByMonthForDict)
                }else{
                    reject(myResult.error!)
                }
            }
        }
    }
    
    //按月读取会议信息
    func getMeetingsByYearAndMonth(_ theDate:Date,completedCallback:@escaping (_ meetingsDict:[String:[OOMeetingInfo]?]?) -> Void){
        let strYear = String(theDate.year)
        let strMonth = String(theDate.month)
        o2MeetingAPI.request(.meetingListByYearMonth(strYear, strMonth)) { (result) in
            let myResult = OOResult<BaseModelClass<[OOMeetingInfo]>>(result)
            if myResult.isResultSuccess() {
                if let model = myResult.model?.data {
                    model.forEach({ (item) in
                        self.meetingsByMonth.append(item)
                    })
                }
                completedCallback(self.meetingsByMonthForDict)
            }else{
                completedCallback(nil)
            }
        }
        
    }
    
    // MARK:- 读取指定日期的会议列表
    func getMeetingByTheDay(_ theDate:Date) -> Promise<[OOMeetingInfo]> {
        let strYear = String(theDate.year)
        let strMonth = String(theDate.month)
        let strDay = String(theDate.day)
        return Promise { fulfill,reject in
            self.o2MeetingAPI.request(.meetingListByYearMonthDay(strYear, strMonth, strDay)) { (result) in
                let myResult = OOResult<BaseModelClass<[OOMeetingInfo]>>(result)
                if myResult.isResultSuccess() {
                    if let models = myResult.model?.data {
                        fulfill(models)
                    }else{
                        fulfill([])
                    }
                }else{
                    reject(myResult.error!)
                }
            }
        }
    }
    
//    //读取指定日期的会议列表
//    func getMeetingByTheDay(_ theDate:Date){
//        let strYear = String(theDate.year)
//        let strMonth = String(theDate.month)
//        let strDay = String(theDate.day)
//        self.theMeetingsByDay.removeAll()
//        o2MeetingAPI.request(.meetingListByYearMonthDay(strYear, strMonth, strDay)) { (result) in
//            let myResult = OOResult<BaseModelClass<[OOMeetingInfo]>>(result)
//            if myResult.isResultSuccess() {
//                if let model = myResult.model?.data {
//                    model.forEach({ (item) in
//                        self.theMeetingsByDay.append(item)
//                    })
//                }
//            }
//            guard let block = self.callbackExecutor else {
//                return
//            }
//            if myResult.isResultSuccess() {
//                block(nil)
//            }else{
//                block(myResult.error?.errorDescription)
//            }
//        }
//        
//    }
}


// MARK:- UITableView DataSource
extension OOMeetingMainViewModel {
    func numberOfSections() -> Int {
        return 1
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        return theMeetingsByDay.count
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> OOMeetingInfo {
        return theMeetingsByDay[indexPath.row]
    }
}


//
//  OOMeetingAcceptViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/22.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit


class OOMeetingAcceptViewModel: NSObject {
    //HTTP API
    private let o2MeetingAPI = OOMoyaProvider<O2MeetingAPI>()
    //所有文件夹及文件列表
    private var meetings:[OOMeetingInfo] = []
    //回调块类型定义
    typealias CallbackBlockDefine = (_ msg:String?) -> Void
    //回调块定义
    var callbackExecutor:CallbackBlockDefine?
    
    override init() {
        super.init()
    }
    
}

extension OOMeetingAcceptViewModel{
    
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
    
    // MARK:- 读取待同意的会议和我发起的会议
    func loadAcceptListByIndex(_ index: Int){
        self.meetings.removeAll()
        if index == 0{
            //接收到的邀请
            o2MeetingAPI.request(.meetingListByAccept) { (result) in
                let myResult = OOResult<BaseModelClass<[OOMeetingInfo]>>(result)
                if myResult.isResultSuccess() {
                    if let model = myResult.model?.data {
                        model.forEach({ (item) in
                            self.meetings.append(item)
                        })
                    }
                }
                guard let block = self.callbackExecutor else {
                    return
                }
                if myResult.isResultSuccess() {
                    block(nil)
                }else{
                    block(myResult.error?.errorDescription)
                }
            }
            
        }else if index == 1{
            //发送出去的邀请
            o2MeetingAPI.request(.meetingListByApplied) { (result) in
                let myResult = OOResult<BaseModelClass<[OOMeetingInfo]>>(result)
                if myResult.isResultSuccess() {
                    if let model = myResult.model?.data {
                        model.forEach({ (item) in
                            self.meetings.append(item)
                        })
                    }
                }
                guard let block = self.callbackExecutor else {
                    return
                }
                if myResult.isResultSuccess() {
                    block(nil)
                }else{
                    block(myResult.error?.errorDescription)
                }
            }
        }
    }
    
}


// MARK:- UITableView DataSource
extension OOMeetingAcceptViewModel{
    func numberOfSections() -> Int {
        return 1
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        return meetings.count
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> OOMeetingInfo? {
        return meetings[indexPath.row]
    }
    
    
//    func headerHeightOfSection(_ section:Int) -> CGFloat {
//        return <#height#>
//    }
//
//    func footerHeightOfSection(_ section:Int) -> CGFloat {
//        return <#height#>
//    }
//
    
}



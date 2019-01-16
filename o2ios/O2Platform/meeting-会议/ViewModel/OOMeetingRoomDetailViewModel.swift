//
//  OOMeetingRoomDetailViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/22.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit


class OOMeetingRoomDetailViewModel: NSObject {
    //HTTP API
    private let o2MeetingAPI = OOMoyaProvider<O2MeetingAPI>()
    //
    var ooMeetingRoomInfo:OOMeetingRoomInfo?{
        didSet {
            
        }
    }
    //回调块类型定义
    typealias CallbackBlockDefine = (_ msg:String?) -> Void
    //回调块定义
    var callbackExecutor:CallbackBlockDefine?
    
    override init() {
        super.init()
    }
    
    func refreshData(){
        guard let block = callbackExecutor else {
            return
        }
        block(nil)
    }
    
}


// MARK:- UITableView DataSource
extension OOMeetingRoomDetailViewModel{
    func numberOfSections() -> Int {
        return 2
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        if section == 0 {
            return 1
        }else if(section == 1){
            return (ooMeetingRoomInfo?.meetingList?.count) ?? 0
        }
        return 0
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> DataModel? {
        if indexPath.section == 0 {
            return ooMeetingRoomInfo
        }else if(indexPath.section == 1){
            return ooMeetingRoomInfo?.meetingList![indexPath.row]
        }
        return nil
    }
}


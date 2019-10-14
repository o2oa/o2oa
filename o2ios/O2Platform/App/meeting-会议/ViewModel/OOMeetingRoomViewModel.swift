//
//  OOMeetingRoomViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/18.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit
import Promises

class OOMeetingRoomViewModel: NSObject {
    //HTTP API
    private let o2MeetingAPI = OOMoyaProvider<O2MeetingAPI>()
    //所有文件夹及文件列表
    var builds:[OOMeetingBuildInfo] = []
    //回调块类型定义
    typealias CallbackBlockDefine = (_ msg:String?) -> Void
    //回调块定义
    var callbackExecutor:CallbackBlockDefine?
    
    override init() {
        super.init()
    }
    
}


// MARK:- UITableView DataSource
extension OOMeetingRoomViewModel{
    func numberOfSections() -> Int {
        return builds.count
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        return builds[section].roomList?.count ?? 0
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> OOMeetingRoomInfo? {
        return builds[indexPath.section].roomList?[indexPath.row]
    }
    
    
    func headerHeightOfSection(_ section:Int) -> CGFloat {
        return 40
    }
    
    func footerHeightOfSection(_ section:Int) -> CGFloat {
        return 10
    }
    
    func headerViewOfSection(_ section:Int) -> UIView {
        let view = Bundle.main.loadNibNamed("OOMeetingRoomMainSectionHeaderView", owner: self, options: nil)?.first as! OOMeetingRoomMainSectionHeaderView
        
        let build = builds[section]
        let buildName = build.name
        let roomCount = build.roomList?.count ?? 0
        view.titleLabel.text = "\(buildName!)(\(roomCount))"
        return view
    }
    
    func footerViewOfSection(_ section:Int) -> UIView {
        let view = UIView()
        view.frame = CGRect(x: 0, y: 0, width: kScreenW, height: 10)
        view.backgroundColor = UIColor(hex: "#f5f5f5")
        return view
    }
    
}


extension OOMeetingRoomViewModel {
    
    func loadAllBuildByDate(_ startDate:String,_ completedDate:String) -> Promise<[OOMeetingBuildInfo]> {
        return Promise { fulfill,reject in
            self.o2MeetingAPI.request(.buildListByStartAndCompletedDate(startDate, completedDate)) { (result) in
                let myResult = OOResult<BaseModelClass<[OOMeetingBuildInfo]>>(result)
                if myResult.isResultSuccess() {
                    if let models = myResult.model?.data {
                        fulfill(models)
                    }else{
                        let customError = OOAppError.common(type: "MeetingRoom load Error", message: "会议室信息读取错误", statusCode: 7002)
                        reject(customError)
                    }
                }else{
                    reject(myResult.error!)
                }
            }
        }
    }
}

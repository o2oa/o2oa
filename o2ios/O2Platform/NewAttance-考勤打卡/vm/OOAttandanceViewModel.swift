//
//  OOAttandanceViewModel.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/16.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import Foundation
import Moya
import Promises
import O2OA_Auth_SDK

public enum OOAttandanceResultType {
    case ok(Any?)
    case fail(String)
    case reload
}

public enum OOAttandanceCustomError:Error {
    case checkinCycle(OOAppError)
    case checkinTotal(OOAppError)
}

final class OOAttandanceViewModel: NSObject {
    //HTTP API
    private let ooAttanceAPI = OOMoyaProvider<OOAttendanceAPI>()
    //当天我的所有打卡记录
    private var myAttanceDetailList:[OOAttandanceMobileDetail] = []
    //回调块类型定义
    typealias CallbackBlockDefine = (_ resultType:OOAttandanceResultType) -> Void
    //回调块定义
    var callbackExecutor:CallbackBlockDefine?
    
    override init() {
        super.init()
    }
    
}

extension OOAttandanceViewModel{
    // MARK:- 读取配置的打卡位置
    func getLocationWorkPlace(_ completedBlock:@escaping CallbackBlockDefine) {
        ooAttanceAPI.request(.myWorkplace) { (responseResult) in
            let myResult = OOResult<BaseModelClass<[OOAttandanceWorkPlace]>>(responseResult)
            if myResult.isResultSuccess() {
                let workPlaces = myResult.model?.data ?? []
                completedBlock(.ok(workPlaces))
            }else{
                let errorMessage = myResult.error?.errorDescription ?? ""
                completedBlock(.fail(errorMessage))
            }
        }
    }
    
    // MARK:- 删除配置
    func deleteLocationWorkPlace(_ bean:OOAttandanceWorkPlace,_ completedBlock:@escaping CallbackBlockDefine) {
        ooAttanceAPI.request(.delWorkplace(bean.id!)) { (responseResult) in
            let myResult = OOResult<BaseModelClass<OOCommonModel>>(responseResult)
            if myResult.isResultSuccess() {
                completedBlock(.reload)
            }else{
                let errorMessage = myResult.error?.errorDescription ?? ""
                completedBlock(.fail(errorMessage))
            }
        }
    }
    // MARK:- 检测
    // MARK:- 读取本人当天的打卡记录
    func getMyCheckinList(_ pageModel:CommonPageModel,_ bean:OOAttandanceMobileQueryBean,_ completedBlock:@escaping CallbackBlockDefine) {
        myAttanceDetailList.removeAll()
        ooAttanceAPI.request(.myAttendanceDetailMobileByPage(pageModel, bean)) { (responseResult) in
            let myResult = OOResult<BaseModelClass<[OOAttandanceMobileDetail]>>(responseResult)
            if myResult.isResultSuccess() {
                self.myAttanceDetailList.append(contentsOf: myResult.model?.data ?? [])
                completedBlock(.reload)
            }else{
                let errorMessage = myResult.error?.errorDescription ?? ""
                completedBlock(.fail(errorMessage))
            }
        }
    }
    
    
    // MARK:- 提交打卡
    func postMyCheckin(_ bean:OOAttandanceMobileCheckinForm,completedBlock:@escaping CallbackBlockDefine) {
        ooAttanceAPI.request(.attendanceDetailCheckIn(bean)) { (responseResult) in
            let myResult = OOResult<BaseModelClass<[OOAttandanceMobileDetail]>>(responseResult)
            if myResult.isResultSuccess() {
                completedBlock(.ok(nil))
            }else{
                let errorMessage = myResult.error?.errorDescription ?? ""
                completedBlock(.fail(errorMessage))
            }
        }
    }
    
    //MARK:- 提交位置
    func postCheckinLocation(_ bean:OOAttandanceNewWorkPlace,completedBlock:@escaping CallbackBlockDefine){
        ooAttanceAPI.request(.addWorkplace(bean)) { (responseResult) in
            let myResult = OOResult<BaseModelClass<OOCommonModel>>(responseResult)
            if myResult.isResultSuccess() {
                completedBlock(.ok(nil))
            }else{
                let errorMessage = myResult.error?.errorDescription ?? ""
                completedBlock(.fail(errorMessage))
            }
        }
    }
    
    // MARK:- 读取打卡周期
    func getCheckinCycle(_ year:String,_ month:String) -> Promise<OOAttandanceCycleDetail> {
        return Promise { fulfill,reject in
            self.ooAttanceAPI.request(.checkinCycle(year, month), completion: { (result) in
                let myResult = OOResult<BaseModelClass<OOAttandanceCycleDetail>>(result)
                if myResult.isResultSuccess() {
                    fulfill((myResult.model?.data!)!)
                }else{
                    reject(myResult.error!)
                }
            })
            
        }
    }
    
    // MARK:- 读取指定月份及及本月打卡周期的所有打统统计数据
    func getCheckinTotal(_ cycleDetail:OOAttandanceCycleDetail) -> Promise<[OOAttandanceCheckinTotal]> {
       let bean = getRequestBean(cycleDetail)
        return Promise { fulfill,reject in
            self.ooAttanceAPI.request(.checkinTotalForMonth(bean), completion: { (result) in
                let myResult = OOResult<BaseModelClass<[OOAttandanceCheckinTotal]>>(result)
                if myResult.isResultSuccess() {
                    fulfill((myResult.model?.data!)!)
                }else{
                    reject(myResult.error!)
                }
            })
            
        }
    }
    
    private func getRequestBean(_ cycleDetail:OOAttandanceCycleDetail) -> OOAttandanceTotalBean {
        let bean = OOAttandanceTotalBean()
        bean.q_year = cycleDetail.cycleYear
        bean.q_month = cycleDetail.cycleMonth
        bean.cycleYear = cycleDetail.cycleYear
        bean.cycleMonth = cycleDetail.cycleMonth
        bean.q_empName = O2AuthSDK.shared.myInfo()?.distinguishedName
        return bean
    }
    
    // MARK:- 读取考勤分析数据
    func getCheckinAnalyze(_ cycleDetail:OOAttandanceCycleDetail) -> Promise<OOAttandanceAnalyze>{
        let bean = getRequestBean(cycleDetail)
        return Promise { fulfill,reject in
            self.ooAttanceAPI.request(.checkinAnalyze(bean)) { (result) in
                let myResult = OOResult<BaseModelClass<[OOAttandanceAnalyze]>>(result)
                if myResult.isResultSuccess() {
                    if let data =  myResult.model?.data {
                        fulfill((data.first)!)
                    }else{
                        reject(OOAppError.common(type: "checkinError", message: "本月无考勤数据", statusCode: 5001))
                    }
                }else{
                    //let errorMessage = myResult.error?.errorDescription
                    reject(myResult.error!)
                }
            }
        }
    }
    
    // MARK:- 读取配置管理员
    func getAttendanceAdmin() -> Promise<[OOAttandanceAdmin]> {
        return Promise { fulfill,reject in
            self.ooAttanceAPI.request(.attendanceAdmin, completion: { (result) in
                let myResult = OOResult<BaseModelClass<[OOAttandanceAdmin]>>(result)
                if myResult.isResultSuccess() {
                    if let data =  myResult.model?.data {
                        fulfill(data)
                    }else{
                        reject(OOAppError.common(type: "checkinError", message: "没有配置管理员", statusCode: 5002))
                    }
                }else{
                    //let errorMessage = myResult.error?.errorDescription
                    reject(myResult.error!)
                }
            })
            
        }
    }
}


// MARK:- UITableView DataSource
extension OOAttandanceViewModel {
    func numberOfSections() -> Int {
        return 1
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        return myAttanceDetailList.count
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> OOAttandanceMobileDetail? {
        return myAttanceDetailList[indexPath.row]
    }
    
    
    
}


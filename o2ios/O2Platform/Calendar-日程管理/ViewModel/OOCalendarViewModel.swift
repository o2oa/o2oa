//
//  OOCalendarViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2018/7/24.
//  Copyright © 2018 zoneland. All rights reserved.
//



import Promises

class OOCalendarViewModel: NSObject {
    override init() {
        super.init()
    }
    
    private let calendarAPI = OOMoyaProvider<OOCalendarAPI>()
    
}

//MARK: - 服务
extension OOCalendarViewModel {
    // 查询我的日历
    func getMyCalendarList() -> Promise<OOMyCalendarList> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.myCalendarList, completion: { result in
                let response = OOResult<BaseModelClass<OOMyCalendarList>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    }else {
                        reject(OOAppError.jsonMapping(message: "返回数据为空！！", statusCode: 1024, data: nil))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 查询日程事件
    func filterCalendarEventList(filter: OOCalendarEventFilter) -> Promise<OOCalendarEventResponse> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.filterCalendarEventList(filter), completion: { result in
                let response = OOResult<BaseModelClass<OOCalendarEventResponse>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    }else {
                        reject(OOAppError.common(type: "calendarError", message: "查询日程为空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 保存日历
    func saveCalendar(calendar: OOCalendarInfo) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.saveCalendar(calendar), completion: { result in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarError", message: "保存日历返回为空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 日历广场
    func getPublicCalendarList() -> Promise<[OOCalendarInfo]> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.getPublicCalendarList, completion: { result in
                let response = OOResult<BaseModelClass<[OOCalendarInfo]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    } else {
                        fulfill([])
                    }
                } else {
                  reject(response.error!)
                }
            })
        }
    }
    // 关注 日历
    func followCalendar(id: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.followCalendar(id), completion: { result in
                let response = OOResult<BaseModelClass<OOCommonValueBoolModel>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data.value ?? false)
                    } else {
                        fulfill(false)
                    }
                } else {
                    reject(response.error!)
                }
            })
        }
    }
    // 取消关注 日历
    func followCalendarCancel(id:String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.followCalendarCancel(id), completion: { result in
                let response = OOResult<BaseModelClass<OOCommonValueBoolModel>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data.value ?? false)
                    } else {
                        fulfill(false)
                    }
                } else {
                    reject(response.error!)
                }
            })
        }
    }
    // 获取日历对象
    func getCalendar(id:String) -> Promise<OOCalendarInfo> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.getCalendar(id), completion: {result in
                let response = OOResult<BaseModelClass<OOCalendarInfo>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    } else {
                        reject(OOAppError.common(type: "calendarError", message: "日历返回为空", statusCode: 50001))
                    }
                } else {
                    reject(response.error!)
                }
            })
        }
    }
    // 删除日历
    func deleteCalendar(id:String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.deleteCalendar(id), completion: {result in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarError", message: "删除日历返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 保存日程事件
    func saveCalendarEvent(event: OOCalendarEventInfo) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.saveCalendarEvent(event), completion: { result in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarEventError", message: "保存日程事件返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 更新日程事件 单个
    func updateCalendarEventSingle(id:String, event: OOCalendarEventInfo) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.updateCalendarEventSingle(id, event), completion: {result in
                let response = OOResult<BaseModelClass<OOCommonValueIntModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarEventError", message: "更新日程事件单个返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 更新日程事件 之后
    func updateCalendarEventAfter(id: String, event: OOCalendarEventInfo) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.updateCalendarEventAfter(id, event), completion: {result in
                let response = OOResult<BaseModelClass<OOCommonValueIntModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarEventError", message: "更新日程事件之后返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 更新日程事件 全部
    func updateCalendarEventAll(id: String, event: OOCalendarEventInfo) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.updateCalendarEventAll(id, event), completion: {result in
                let response = OOResult<BaseModelClass<OOCommonValueIntModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarEventError", message: "更新日程事件全部返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 删除日程事件 单个
    func deleteCalendarEventSingle(id: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.deleteCalendarEventSingle(id), completion: {result in
                let response = OOResult<BaseModelClass<OOCommonValueIntModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarEventError", message: "删除日程事件单个返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 删除日程事件 之后
    func deleteCalendarEventAfter(id: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.deleteCalendarEventAfter(id), completion: {result in
                let response = OOResult<BaseModelClass<OOCommonValueIntModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarEventError", message: "删除日程事件之后返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    // 删除日程事件 全部
    func deleteCalendarEventAll(id: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.calendarAPI.request(.deleteCalendarEventAll(id), completion: {result in
                let response = OOResult<BaseModelClass<OOCommonValueIntModel>>(result)
                if response.isResultSuccess() {
                    if (response.model?.data) != nil {
                        fulfill(true)
                    }else {
                        reject(OOAppError.common(type: "calendarEventError", message: "删除日程事件全部返回空", statusCode: 50001))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
}




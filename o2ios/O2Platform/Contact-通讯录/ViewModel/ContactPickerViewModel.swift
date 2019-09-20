//
//  ContactPickerViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/13.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Promises


class ContactPickerViewModel: NSObject {
    override init() {
        super.init()
    }
    
    private let orgControlAPI = OOMoyaProvider<OOContactAPI>()
    private let orgExpressAPI = OOMoyaProvider<OOContactExpressAPI>()

}


extension ContactPickerViewModel {
    
    func getPersonInfo(dn: String) -> Promise<OOPersonModel> {
        return Promise { fulfill, reject in
            self.orgControlAPI.request(.getPerson(dn)) { (result) in
                let myResult = OOResult<BaseModelClass<OOPersonModel>>(result)
                if myResult.isResultSuccess() {
                    if let data = myResult.model?.data {
                        fulfill(data)
                    }else {
                       reject(OOAppError.jsonMapping(message: "返回数据为空！！", statusCode: 1024, data: nil))
                    }
                } else {
                    reject(myResult.error!)
                }
            }
        }
    }
    
    //
    // 组织查询
    // @param parent 上级组织id -1就是顶层
    // @param topList 顶级组织id 列表
    // @param unitType 组织类型
    //
    func loadUnitList(parent: String, topList: [String] = [], unitType: String = "") -> Promise<[OOUnitModel]>  {
        if parent == "-1" { //顶层
            if unitType.isEmpty {
                if topList.isEmpty {
                    return self.unitListTop()
                }else {
                    return self.unitListByIds(distinguishedNameList: topList)
                }
            }else {
                if topList.isEmpty {
                    return self.unitListByType(type: unitType, parentUnit: nil)
                }else {//这里没有查询 toplist过滤 组织类型的接口
                    return self.unitListByIds(distinguishedNameList: topList)
                }
            }
        }else {
            if unitType.isEmpty {
                return self.unitSubList(parent: parent)
            }else {
                return self.unitListByType(type: unitType, parentUnit: parent)
            }
        }
    }
    
    //
    // 身份查询
    // @param unit 上级组织id
    // @param dutyList 过滤的职责
    //
    func loadIdentityList(dutyList:[String], unit:String) -> Promise<[OOIdentityModel]>  {
        if dutyList.isEmpty {
            return self.identityListByUnit(unit: unit)
        }else {
            return self.identityListByUnitFilterDutyList(unit: unit, dutyList: dutyList)
        }
    }
    
    //
    // 分页查询群组 每页20条
    // @param lastId
    //
    func loadGroupList(lastId: String) -> Promise<[OOGroupModel]> {
        return Promise{ fulfill, reject in
            self.orgControlAPI.request(.groupListNext(lastId, 20), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOGroupModel]>>(result)
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
    
    //
    // 搜索查询人员
    //
    func searchPersonList(searchText: String) -> Promise<[OOPersonModel]>  {
        return Promise{ fulfill, reject in
            self.orgControlAPI.request(.personLike(searchText), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOPersonModel]>>(result)
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
    
    
    //获取顶级组织列表
    private func unitListTop() -> Promise<[OOUnitModel]> {
        return Promise { fulfill, reject in
            self.orgControlAPI.request(.listTop, completion: { (result) in
                let response = OOResult<BaseModelClass<[OOUnitModel]>>(result)
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
    
    //根据父获取子组织列表
    private func unitSubList(parent: String) -> Promise<[OOUnitModel]>  {
        return Promise { fulfill, reject in
            self.orgControlAPI.request(.listSubDirect(parent), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOUnitModel]>>(result)
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
    
    //根据id列表获取组织对象列表
    private func unitListByIds(distinguishedNameList: [String]) -> Promise<[OOUnitModel]>  {
        return Promise { fulfill, reject in
            self.orgControlAPI.request(.unitList(distinguishedNameList), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOUnitModel]>>(result)
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
    
    //根据组织类型返回组织列表，parentUnit为空就是返回顶级组织列表
    private func unitListByType(type: String, parentUnit: String?)  -> Promise<[OOUnitModel]>  {
        var unitList: [String] = []
        if parentUnit != nil && parentUnit?.isEmpty != true {
            unitList = [parentUnit!]
        }
        return Promise { fulfill, reject in
            self.orgControlAPI.request(.unitListByType(type, unitList), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOUnitModel]>>(result)
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
    
    //e根据组织查询身份列表
    private func identityListByUnit(unit: String) -> Promise<[OOIdentityModel]> {
        return Promise{ fulfill, reject in
            self.orgControlAPI.request(.identityListByUnit(unit), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOIdentityModel]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        //person id 换 DN 人员选择的时候需要
                        let idList = data.map({ (oo) -> String in
                            return oo.person ?? ""
                        })
                        if idList.isEmpty {
                            fulfill(data)
                        }else {
                            self.orgExpressAPI.request(.personListDN(idList), completion: { (dnResult) in
                                let dnResponse = OOResult<BaseModelClass<OOPersonDNModel>>(dnResult)
                                if dnResponse.isResultSuccess() {
                                    if let dnList = dnResponse.model?.data?.personList {
                                        var index = 0
                                        let newData = data.map({ (ooId) -> OOIdentityModel in
                                            ooId.person = dnList[index]
                                            index += 1
                                            return ooId
                                        })
                                        fulfill(newData)
                                    }else {
                                        fulfill(data)
                                    }
                                }else {
                                    fulfill(data)
                                }
                            })
                        }
                        
                    }else {
                        reject(OOAppError.jsonMapping(message: "返回数据为空！！", statusCode: 1024, data: nil))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    private func identityListByUnitFilterDutyList(unit: String, dutyList:[String]) -> Promise<[OOIdentityModel]> {
        return Promise{ fulfill, reject in
            self.orgExpressAPI.request(.identityListByUnitAndDuty(dutyList, unit), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOIdentityModel]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        //person id 换 DN 人员选择的时候需要
                        let idList = data.map({ (oo) -> String in
                            return oo.person ?? ""
                        })
                        if idList.isEmpty {
                            fulfill(data)
                        }else {
                            self.orgExpressAPI.request(.personListDN(idList), completion: { (dnResult) in
                                let dnResponse = OOResult<BaseModelClass<OOPersonDNModel>>(dnResult)
                                if dnResponse.isResultSuccess() {
                                    if let dnList = dnResponse.model?.data?.personList {
                                        var index = 0
                                        let newData = data.map({ (ooId) -> OOIdentityModel in
                                            ooId.person = dnList[index]
                                            index += 1
                                            return ooId
                                        })
                                        fulfill(newData)
                                    }else {
                                        fulfill(data)
                                    }
                                }else {
                                    fulfill(data)
                                }
                            })
                        }
                    }else {
                        reject(OOAppError.jsonMapping(message: "返回数据为空！！", statusCode: 1024, data: nil))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    
    
}

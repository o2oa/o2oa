//
//  OOPersonListViewModel.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/24.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import Foundation
import Moya


class OOPersonListViewModel: NSObject {
    //HTTP API
    private let ooContactAPI = OOMoyaProvider<OOContactAPI>()
    //所有人员列表
    private var allPersons:[OOPersonModel] = []
    
    //搜索所有人员列表
    private var filterPersons:[OOPersonModel] = []
    
    //回调块类型定义
    typealias CallbackBlockDefine = (_ parameter:CommonPageModel,_ msg:String?) -> Void
    //回调块定义
    var callbackExecutor:CallbackBlockDefine?
    //搜索回调块定义
    typealias SearchBackBlockDefine = (_ msg:String?) -> Void
    
    var searchBackExecutor:SearchBackBlockDefine?
    
    var isSearchActive = false
    
    override init() {
        super.init()
    }
    
}

extension OOPersonListViewModel {
    
    func getAllPerson(_ isNextPage:Bool,_ parameters:CommonPageModel,callbackCompleted:@escaping CallbackBlockDefine){
        var currentParameters = parameters
        if !isNextPage {
            allPersons.removeAll()
        }
        ooContactAPI.request(.personListNext(parameters.nextPageId, parameters.pageSize )) { (result) in
            let myResult = OOResult<BaseModelClass<[OOPersonModel]>>(result)
            if myResult.isResultSuccess() {
                myResult.model?.data?.forEach({ (uModel) in
                    self.allPersons.append(uModel)
                })
                currentParameters.setPageTotal((myResult.model?.count)!)
                currentParameters.nextPageId = (self.allPersons.last?.id)!
            }
            if myResult.isResultSuccess() {
                callbackCompleted(currentParameters,nil)
            }else{
                callbackCompleted(currentParameters,myResult.error.debugDescription)
            }
        }
    }
    
    // MARK:- 搜索用户按名字
    func filterPerson(_ filter:String,callbackCompleted:@escaping SearchBackBlockDefine){
        if filter.isEmpty {
            return
        }
        ooContactAPI.request(.personLike(filter)) { (result) in
            self.filterPersons.removeAll()
            let myResult = OOResult<BaseModelClass<[OOPersonModel]>>(result)
            if myResult.isResultSuccess() {
                if let model = myResult.model?.data {
                    model.forEach({ (pModel) in
                       self.filterPersons.append(pModel)
                    })
                }
            }
            if myResult.isResultSuccess() {
                callbackCompleted(nil)
            }else{
                callbackCompleted(myResult.error.debugDescription)
            }
        }
    }

    // MARK: - 获取icon
    func getIconOfPerson(_ person:OOPersonModel,compeletionBlock:@escaping (_ image:UIImage?,_ errMsg:String?) -> Void) {
        ooContactAPI.request(.iconByPerson(person.id!)) { (result) in
            if let err = result.error {
                compeletionBlock(#imageLiteral(resourceName: "icon_men"),err.errorDescription)
            }else{
                let data = result.value?.data
                guard let image = UIImage(data: data!) else {
                    compeletionBlock(#imageLiteral(resourceName: "icon_men"),"image transform error")
                    return
                }
                compeletionBlock(image,nil)
            }
        }
    }
    
}



// MARK:- UITableView DataSource
extension OOPersonListViewModel{
    
    func numberOfSections() -> Int {
        return 2
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        if section == 0 {
            return 1
        }else if section == 1 {
            if isSearchActive == true {
                return filterPersons.count
            }
            return allPersons.count
        }
        return 0
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> OOPersonModel? {
        if indexPath.section == 1 {
            if isSearchActive == true {
                return filterPersons[indexPath.row]
            }
            return allPersons[indexPath.row]
        }
        return nil
    }
    
    
    
}

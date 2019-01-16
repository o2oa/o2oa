//
//  OOContactSearchViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/28.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOContactSearchViewModel: NSObject {
    
    //搜索结果数据
    private var searchContactDatas:[OOContacSearchSectionHeaderType:[DataModel]] = [:]
    
    private let contactAPI = OOMoyaProvider<OOContactAPI>()
    
    typealias AddBlock = (_ msg:String?) -> Void
    
    var updateBlock:AddBlock?
    
    override  init() {
        super.init()
        initData()
    }
    
    func initData(){
        searchContactDatas[.unit] = []
        searchContactDatas[.person] = []
        searchContactDatas[.group] = []
    }

}

// MARK:- 搜索
extension OOContactSearchViewModel{
    func searchRefreshData(_ searchText:String){
        //搜索组织
        //搜索人员
        //组织群组
        //self.initData()
        contactAPI.request(.unitLike(searchText)) { (result) in
            let myResult = OOResult<BaseModelClass<[OOUnitModel]>>(result)
            self.searchContactDatas[.unit] = []
            if myResult.isResultSuccess() {
                if let model = myResult.model?.data {
                    model.forEach({ (uModel) in
                        self.searchContactDatas[.unit]?.append(uModel)
                    })
                }
            }
            guard let block = self.updateBlock else {
                return
            }
            if myResult.isResultSuccess() {
                block(nil)
            }else{
                block(myResult.error.debugDescription)
            }
        }
        
        contactAPI.request(.personLike(searchText)) { (result) in
            let myResult = OOResult<BaseModelClass<[OOPersonModel]>>(result)
            self.searchContactDatas[.person] = []
            if myResult.isResultSuccess() {
                if let model = myResult.model?.data {
                    model.forEach({ (pModel) in
                        self.searchContactDatas[.person]?.append(pModel)
                    })
                }
            }
            guard let block = self.updateBlock else {
                return
            }
            if myResult.isResultSuccess() {
                block(nil)
            }else{
                block(myResult.error.debugDescription)
            }
        }
        
        contactAPI.request(.groupLike(searchText)) { (result) in
             let myResult = OOResult<BaseModelClass<[OOGroupModel]>>(result)
            self.searchContactDatas[.group] = []
            if myResult.isResultSuccess() {
                if let model = myResult.model?.data {
                    model.forEach({ (gModel) in
                        self.searchContactDatas[.group]?.append(gModel)
                    })
                }
            }
            guard let block = self.updateBlock else {
                return
            }
            if myResult.isResultSuccess() {
                block(nil)
            }else{
                block(myResult.error.debugDescription)
            }
        }
        
        
    }
    
    // MARK: - 获取icon
    func getIconOfPerson(_ person:OOPersonModel,compeletionBlock:@escaping (_ image:UIImage?,_ errMsg:String?) -> Void) {
        contactAPI.request(.iconByPerson(person.id!)) { (result) in
            if let err = result.error {
                compeletionBlock(#imageLiteral(resourceName: "icon_？"),err.errorDescription)
            }else{
                let data = result.value?.data
                guard let image = UIImage(data: data!) else {
                    compeletionBlock(#imageLiteral(resourceName: "icon_？"),"image transform error")
                    return
                }
                compeletionBlock(image,nil)
            }
        }
    }
}

extension OOContactSearchViewModel {
    func numberOfSectionsForSearch() -> Int {
        return searchContactDatas.count
    }
    
    func numberOfRowsInSectionForSearch(_ section: Int) -> Int {
        return searchContactDatas[OOContacSearchSectionHeaderType(rawValue:section)!]!.count
    }
    
    func nodeForIndexPathForSearch(_ indexPath:IndexPath) -> DataModel? {
        let type = OOContacSearchSectionHeaderType(rawValue: indexPath.section)
        let item = searchContactDatas[type!]![indexPath.row]
        return item
    }
    
    func headerHeightOfSection(_ section:Int) -> CGFloat {
        return 40.0
    }
    
    func footerHeightOfSection(_ section:Int) -> CGFloat {
        return 10.0
    }
    
    func headerTypeOfSection(_ section:Int) -> OOContacSearchSectionHeaderType {
        return OOContacSearchSectionHeaderType(rawValue: section)!
    }
}

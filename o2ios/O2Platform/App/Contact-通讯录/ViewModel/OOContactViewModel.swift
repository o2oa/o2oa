//
//  OOContactViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/20.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import Promises
import O2OA_Auth_SDK

class OOContactViewModel: NSObject {
    //分组
    private let sectionNums  = 4
    //数据
    private var contactDatas:[OOContactGroupHeaderType:[DataModel]] = [:]
    
    private let contactAPI = OOMoyaProvider<OOContactAPI>()
    
    typealias AddBlock = (_ msg:String?) -> Void
    
    var updateBlock:AddBlock?
    
    override  init() {
        super.init()
        initData()
    }
    
    func initData(){
        contactDatas[.department] = []
        contactDatas[.company] = []
        contactDatas[.linkman] = []
        contactDatas[.group] = []
    }
}

// MARK: - 读取公司、部门及群组和常用联系人
extension OOContactViewModel {
    
    func refreshData() {
        let currentAccount = O2AuthSDK.shared.myInfo()!
        self.initData()
        //请求当前帐号的所有个人信息
        contactAPI.request(.getPerson(currentAccount.id!)) { (result) in
            let myResult = OOResult<BaseModelClass<OOPersonModel>>(result)
            if myResult.isResultSuccess() {
                //数据处理 增加我的部门 从我的身份中取到部门信息
                if  let model = myResult.model?.data {
                    model.woIdentityList?.forEach({ (iModel) in
                        if let unit = iModel.woUnit {
                            self.contactDatas[.department]?.append(unit)
                        }
                    })
                    
                    //我的群组
                    model.woGroupList?.forEach({ (gModel) in
                        self.contactDatas[.group]?.append(gModel)
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
        //请求顶层组织
        contactAPI.request(.listTop) { (result) in
            let myResult = OOResult<BaseModelClass<[OOUnitModel]>>(result)
            if myResult.isResultSuccess() {
                if let model = myResult.model?.data {
                    model.forEach({ (uModel) in
                        self.contactDatas[.company]?.append(uModel)
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
    
    
}



extension OOContactViewModel {
    
    func numberOfSections() -> Int {
        return contactDatas.count
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        return contactDatas[OOContactGroupHeaderType(rawValue:section)!]!.count
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> DataModel? {
        let type = OOContactGroupHeaderType(rawValue: indexPath.section)
        let item = contactDatas[type!]![indexPath.row]
        return item
    }
    
    
    func headerHeightOfSection(_ section:Int) -> CGFloat {
        return 40.0
    }
    
    func footerHeightOfSection(_ section:Int) -> CGFloat {
        return 10.0
    }
    
    func headerTypeOfSection(_ section:Int) -> OOContactGroupHeaderType {
        return OOContactGroupHeaderType(rawValue: section)!
    }
    
}


//
//  OOListUnitViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/21.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import Moya

class OOListUnitViewModel:NSObject {
    
    private var unitDatas:[Int:[DataModel]] = [:]
    
    private let contactAPI = OOMoyaProvider<OOContactAPI>()
    
    typealias AddBlock = (_ msg:String?) -> Void
    
    var updateBlock:AddBlock?
    
    override init() {
        super.init()
        initSetup()
    }
    
    func initSetup()  {
        unitDatas[0] = []
    }
    
    // MARK: - 读取数据
    func refreshData(_ unitFlag:String){
        initSetup()
        //读取下一级
        contactAPI.request(.listSubDirect(unitFlag)) { (result) in
            let myResult = OOResult<BaseModelClass<[OOUnitModel]>>(result)
            if myResult.isResultSuccess() {
                myResult.model?.data?.forEach({ (uModel) in
                    self.unitDatas[0]?.append(uModel)
                })
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
        //读取本级下的所有人员
        contactAPI.request(.getUnit(unitFlag)) { (result) in
            let myResult = OOResult<BaseModelClass<OOUnitModel>>(result)
            if myResult.isResultSuccess() {
                let model = myResult.model?.data
                model?.woSubDirectIdentityList?.forEach({ (iModel) in
                    self.unitDatas[0]?.append(iModel.woPerson!)
                })
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


extension OOListUnitViewModel{
    func numberOfSections() -> Int {
        return unitDatas.count
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        return unitDatas[section]!.count
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> DataModel? {
        return unitDatas[indexPath.section]?[indexPath.row]
    }
    
    func headerHeightOfSection(_ section:Int) -> CGFloat {
        return 40.0
    }
    
    func footerHeightOfSection(_ section:Int) -> CGFloat {
        return 10.0
    }
    
    func headerTypeOfSection(_ section:Int) -> UIView {
        return UIView()
    }
    
}


//
//  OOMeetingCreateViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/26.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOMeetingCreateViewModel: NSObject {
    //Meeting API
    private let ooMeetingAPI = OOMoyaProvider<O2MeetingAPI>()
    //Contact API
    private let ooContactAPI = OOMoyaProvider<OOContactAPI>()
    
    private var persons:[OOPersonModel] = []
    
    var selectedPersons:[OOPersonModel] = []
    
    typealias CallDefine = (_ msg:String?) -> Void
    //人员列表回调
    var contactCallBlock:CallDefine?
    
    //选择人员列表回调
    var selectedContactCallBlock:CallDefine?
    
    override init() {
        super.init()
    }
    
}



extension OOMeetingCreateViewModel{
    
    func getLastPerson() -> OOPersonModel? {
        return self.persons.last ?? nil
    }
    //所有用户列表
    func getAllPerson(_ next:String?){
        if let nextId = next {
            ooContactAPI.request(.personListNext(nextId, 20)) { (result) in
                let myResult = OOResult<BaseModelClass<[OOPersonModel]>>(result)
                if myResult.isResultSuccess() {
                    if let model = myResult.model?.data {
                        model.forEach({ (item) in
                            self.persons.append(item)
                        })
                    }
                }
                guard let block = self.contactCallBlock else {
                    return
                }
                if myResult.isResultSuccess() {
                    block(nil)
                }else{
                    block(myResult.error?.errorDescription)
                }
            }
            
        }else{
            self.persons.removeAll()
            ooContactAPI.request(.personListNext("(0)", 20)) { (result) in
                let myResult = OOResult<BaseModelClass<[OOPersonModel]>>(result)
                if myResult.isResultSuccess() {
                    if let model = myResult.model?.data {
                        model.forEach({ (item) in
                            self.persons.append(item)
                        })
                    }
                }
                guard let block = self.contactCallBlock else {
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
    // MARK: - 获取icon
    func getIconOfPerson(_ person:OOPersonModel,compeletionBlock:@escaping (_ image:UIImage?,_ errMsg:String?) -> Void) {
        ooContactAPI.request(.iconByPerson(person.id!)) { (result) in
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
    //创建会议
    func createMeetingAction(_ meeting:OOMeetingFormBean,completedBlock:@escaping (_ returnMessage:String?) -> Void){
        ooMeetingAPI.request(.meetingItemByCreate(meeting)) { (result) in
            let myResult = OOResult<BaseModelClass<[OOCommonModel]>>(result)
            if myResult.isResultSuccess() {
                completedBlock(nil)
            }else{
                completedBlock(myResult.error?.errorDescription)
            }
        }
        
    }
    
    //表单模型
    func getFormModels() -> [OOFormBaseModel] {
        let titleModel = OOFormBaseModel(titleName: "会议主题", key: "subject", componentType: .textItem, itemStatus: .edit)
        let dateModel = OOFormBaseModel(titleName: "会议日期", key: "date", componentType: .dateItem, itemStatus: .edit)
        let dateIntervalModel = OOFormDateIntervalModel(titleName: "会议时间", key: "dateInterval", componentType: .dateIntervalItem, itemStatus: .edit)
        let segueModel = OOFormSegueItemModel(titleName: "会议室", key: "room", componentType: .segueItem, itemStatus: .edit)
        segueModel.segueIdentifier = "OOMeetingMeetingRoomManageController"
        return [titleModel,dateModel,dateIntervalModel,segueModel]
    }
}
// MARK:- 选择的人员列表
extension OOMeetingCreateViewModel{
    
    func addSelectPerson(_ p:OOPersonModel){
        self.selectedPersons.append(p)
    }
    
    func removeSelectPerson(_ p:OOPersonModel){
        if let i = self.selectedPersons.firstIndex(of: p) {
             self.selectedPersons.remove(at:i)
        }
    }
    
    func refreshData(){
        guard let block = self.selectedContactCallBlock else {
            return
        }
        block(nil)
    }
    
    func collectionViewNumberOfSections() -> Int {
        return 1
    }
    
    func collectionViewNumberOfRowsInSection(_ section: Int) -> Int {
        return selectedPersons.count + 1
    }
    
    func collectionViewNodeForIndexPath(_ indexPath:IndexPath) -> OOPersonModel? {
        if indexPath.row < selectedPersons.count {
            return selectedPersons[indexPath.row]
        }else{
            return nil
        }
    }
}

// MARK:- 人员列表
extension OOMeetingCreateViewModel {
    func tableViewNumberOfSections() -> Int {
        return 1
    }
    
    func tableViewNumberOfRowsInSection(_ section: Int) -> Int {
        return persons.count
    }
    
    func tableViewNodeForIndexPath(_ indexPath:IndexPath) -> OOPersonModel? {
        return persons[indexPath.row]
    }
}

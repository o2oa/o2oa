//
//  OOLinkManViewModel.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/23.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

//定义简易类型
 typealias OOPersonCellModel = (title:String,value:String,actionIconName:String?,actionURL:String?)

class OOLinkManViewModel: NSObject {
    
    private let contactAPI = OOMoyaProvider<OOContactAPI>()
    
    var currentPerson:OOPersonModel?{
        didSet {
            personInfoData.append((title:"姓名",value:(currentPerson?.name)!,nil,nil))
            personInfoData.append((title:"员工号",value:(currentPerson?.employee)!,nil,nil))
            personInfoData.append((title:"唯一编码",value:(currentPerson?.unique)!,nil,nil))
            personInfoData.append((title:"联系电话",value:(currentPerson?.mobile)!,"Shape","tel:\((currentPerson?.mobile)!)"))
            personInfoData.append((title:"电子邮件",value:(currentPerson?.mail)!,"icon_email","mailto:\((currentPerson?.mail)!)"))
            var dept:[String] = []
            currentPerson?.woIdentityList?.forEach({ (iModel) in
                dept.append(iModel.unitName!)
            })
            personInfoData.append((title:"部门",value:dept.joined(separator: ","),nil,nil))
           
        }
    }
    
   
    
    
    private var personInfoData:[OOPersonCellModel] = []
    
    
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

extension OOLinkManViewModel{
    func numberOfSections() -> Int {
        return 1
    }
    
    func numberOfRowsInSection(_ section: Int) -> Int {
        return personInfoData.count
    }
    
    func nodeForIndexPath(_ indexPath:IndexPath) -> OOPersonCellModel? {
        return personInfoData[indexPath.row]
    }
    
    func headerHeightOfSection(_ section:Int) -> CGFloat {
        return 10.0
    }
    
    func footerHeightOfSection(_ section:Int) -> CGFloat {
        return 10.0
    }
    
    func tableHeaderView() -> UIView{
        let headerView = Bundle.main.loadNibNamed("OOLinkManInfoHeader", owner: self, options: nil)![0] as! OOLinkManInfoHeader
        headerView.configHeaderOfPerson(self,currentPerson!)
        return headerView
    }
    
    func headerTypeOfSection(_ section:Int) -> UIView {
        return UIView()
    }
    
}








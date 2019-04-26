//
//  ContactPersonInfoController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/15.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import Eureka
import CocoaLumberjack

class ContactPersonInfoController: FormViewController {
    
    
    var myPersonURL:String?
    
    var identity:IdentityV2? {
        didSet {
            self.myPersonURL = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personInfoByNameQuery, parameter: ["##name##":identity!.person! as AnyObject])
        }
    }
    
    var isLoadPerson:Bool = true
    
    var contact:PersonV2? {
        didSet {
            isLoadPerson = false
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        ImageRow.defaultCellUpdate = { cell, row in
            cell.accessoryView?.layer.cornerRadius = 17
            cell.accessoryView?.frame = CGRect(x: 0, y: 0, width: 34, height: 34)
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
        }
        
        LabelRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            cell.accessoryType = .disclosureIndicator
        }
        EmailRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            cell.accessoryType = .disclosureIndicator
            
        }
        
        PhoneRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = UIFont(name: "PingFangSC-Light", size: 12.0)
            cell.textLabel?.textColor  = RGB(155, g: 155, b: 155)
            cell.accessoryType = .disclosureIndicator
        }
        
        isLoadPerson==true ?loadPersonInfo(nil):loadDataCell()
        
    }
    
    func loadPersonInfo(_ sender: AnyObject?){
        self.showMessage(title: "加载中...")
        Alamofire.request(myPersonURL!).responseJSON {
            response in
            switch response.result {
            case .success( let val):
                let json = JSON(val)["data"]
                self.contact = Mapper<PersonV2>().map(JSONString:json.description)!
                self.loadDataCell()
                self.dismissProgressHUD()
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "加载失败")
            }
            
        }
    }
    
    func loadDataCell(){
        
        form +++ Section()
            <<< ImageRow("icon"){ row in
                row.title = "头像"
                let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":contact?.unique as AnyObject])
                let url = URL(string: urlstr!)
                //从网络获取数据流
                if let data = try? Data(contentsOf: url!){
                    row.value = UIImage(data: data)
                }else{
                    row.value = UIImage(named: "personDefaultIcon")
                }
                
                row.disabled = true
            }
            <<< LabelRow("employee"){
                $0.title = "工号"
                $0.value = contact!.employee
            }
            <<< LabelRow("display"){
                $0.title = "名字"
                $0.value = contact!.name
            }
            <<< LabelRow("genderType"){
                $0.title = "性别"
                $0.value = contact!.genderType == "f" ? "女":"男"
            }
            
            +++ Section()
            <<< LabelRow("mail"){
                $0.title = "Email"
                $0.value = contact!.mail
            }
            <<< LabelRow("mobile"){
                $0.title = "手机"
                $0.value = contact!.mobile
            }.onCellSelection({ (cell, row) in
                if let phone = row.value {
                let alertController = UIAlertController(title: "", message: "呼叫联系人？", preferredStyle: .alert)
                let smsAction = UIAlertAction(title: "发短信", style: .default, handler: { _ in
                    let smsURL = URL(string: "sms://\(phone)")
                    if UIApplication.shared.canOpenURL(smsURL!) {
                        UIApplication.shared.openURL(smsURL!)
                    }else{
                        self.showError(title: "发短信失败")
                    }
                })
                let phoneAction = UIAlertAction(title: "打电话", style: .default, handler: { _ in
                   let phoneURL = URL(string: "tel://\(phone)")
                    if UIApplication.shared.canOpenURL(phoneURL!) {
                        UIApplication.shared.openURL(phoneURL!)
                    }else{
                        self.showError(title: "打电话失败")
                    }
                })
                let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
                alertController.addAction(phoneAction)
                alertController.addAction(smsAction)
                alertController.addAction(cancelAction)
                self.present(alertController, animated: true, completion: nil)
                }
            })
            <<< LabelRow("weixin"){
                $0.title = "微信"
                $0.value = contact!.weixin
            }
            <<< LabelRow("qq"){
                $0.title = "QQ"
                $0.value = contact!.qq
            }
            <<< LabelRow("weibo"){
                $0.title = "微博"
                $0.value = ""
            }
    }
    
    
    


    
}

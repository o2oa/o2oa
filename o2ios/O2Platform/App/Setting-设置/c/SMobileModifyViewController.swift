//
//  SMobileModifyViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Eureka
import Alamofire
import AlamofireImage
import AlamofireObjectMapper

import ObjectMapper

class SMobileModifyViewController: FormViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "更新", style: .plain, target: self, action:#selector(self.modifyButtonAction(sender:)))
        self.navigationItem.rightBarButtonItem?.isEnabled = false
        
        LabelRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            cell.accessoryType = .disclosureIndicator
        }
        
        TextRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        
        PhoneRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        
        
        
        form +++ Section()
            <<< LabelRow() {
                row in
                row.title = "国家和地区"
                row.value = "+86"
        }
        +++ Section()
            <<< PhoneRow("phoneNumber"){
                row in
                row.title = "手机号码"
                row.placeholder = "请输入手机号"
        }.cellUpdate({ (cell, row) in
            self.isEnableAction()
        })
        
            <<< TextRow("vaildCode"){
                row in
                row.title = "确认手机号"
                row.placeholder = "确认手机号"
                
        }.cellUpdate({ (cell, row) in
             self.isEnableAction()
        })
        
    }
    
    private func isEnableAction(){
        let p1 = form.rowBy(tag: "phoneNumber") as! PhoneRow
        let p2 = form.rowBy(tag: "vaildCode") as! TextRow
        if  let _ = p1.value,let _ = p2.value  {
            self.navigationItem.rightBarButtonItem?.isEnabled = true
        }else{
            self.navigationItem.rightBarButtonItem?.isEnabled = false
        }
    }
    
    @objc func modifyButtonAction(sender:Any){
        let p1 = form.rowBy(tag: "phoneNumber") as! PhoneRow
        let p2 = form.rowBy(tag: "vaildCode") as! TextRow
        if  let _ = p1.value,let _ = p2.value  {
            
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    


}

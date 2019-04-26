//
//  SPasswordChangeViewController.swift
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
import SwiftyJSON
import CocoaLumberjack

class SPasswordChangeViewController: FormViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "确认", style: .plain, target: self, action: #selector(self.passwordChangeSubmit))
        
        PasswordRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        
        form +++ Section()
        <<< PasswordRow("oldPassword"){
            $0.title = "原密码"
            $0.add(rule: RuleRequired(msg:"请输入原密码"))
            $0.validationOptions = .validatesOnDemand
        }
        +++ Section()
        <<< PasswordRow("newPassword"){
            $0.title = "新密码"
            $0.add(rule: RuleRequired(msg:"请输入新密码"))
            $0.validationOptions = .validatesOnDemand
        }
        <<< PasswordRow("confirmPassword"){
            $0.title = "确认新密码"
            $0.add(rule: RuleRequired(msg:"请确认新密码"))
            $0.validationOptions = .validatesOnDemand
        }

    }
    
    @objc private func passwordChangeSubmit(){
        let oldRow = form.rowBy(tag: "oldPassword") as! PasswordRow
        let newRow  = form.rowBy(tag: "newPassword") as! PasswordRow
        let newConfirmRow  = form.rowBy(tag: "confirmPassword") as! PasswordRow
        if oldRow.validate().count > 0 {
            self.showError(title: oldRow.validate()[0].msg)
            //oldRow.cell.becomeFirstResponder()
            return
        }
        if newRow.validate().count > 0 {
            self.showError(title: newRow.validate()[0].msg)
            //newRow.cell.becomeFirstResponder()
            return
        }
        if newConfirmRow.validate().count > 0 {
            self.showError(title: newConfirmRow.validate()[0].msg)
            //newConfirmRow.cell.becomeFirstResponder()
            return
        }
        if newRow.value != newConfirmRow.value {
            let alert = UIAlertController(title: "提醒", message: "密码修改提示", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "新密码与确认密码不一致，请修改", style: .default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }else{
            //生成参数
            let parameter = ["oldPassword":oldRow.value!,"newPassword":newRow.value!,"confirmPassword":newConfirmRow.value!]
            //修改密码
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(PersonContext.personContextKey, query: PersonContext.personPasswordUpdateQuery, parameter: nil)
            self.showMessage(title:"提交中...")
            Alamofire.request(url!, method: .put, parameters: parameter, encoding: JSONEncoding.default, headers: nil).responseJSON(completionHandler: { (response) in
                switch response.result {
                case .success(let val):
                    let type = JSON(val)["type"]
                    if type == "success" {
                        self.showSuccess(title: "修改成功")
                    }else{
                        DDLogError(JSON(val).description)
                        self.showError(title: "修改失败")
                    }
                case .failure(let err):
                    DDLogError(err.localizedDescription)
                    self.showError(title: "修改失败")
                }
            })
            
        }
        
    
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

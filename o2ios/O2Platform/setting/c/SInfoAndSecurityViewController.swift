//
//  SInfoAndSecurityViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Eureka

import O2OA_Auth_SDK

class SInfoAndSecurityViewController: FormViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let account = O2AuthSDK.shared.myInfo()
        let isSecurity = AppConfigSettings.shared.accountIsSecurity
        
        LabelRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            cell.detailTextLabel?.font = setting_value_textFont
            cell.detailTextLabel?.textColor = setting_value_textColor
            cell.accessoryType = .disclosureIndicator
        }
        
        form +++ Section()
        <<< LabelRow(){
                $0.title = "帐号保护"
                $0.value = (isSecurity == true ? "已保护":"未保护")
        }.onCellSelection({ (cell, row) in
            self.performSegue(withIdentifier: "showAccountSecSegue", sender: nil)
        })
            
            
        +++ Section()
            <<< LabelRow(){
            $0.title = "登录帐号"
            $0.value = account?.name
        }
            <<< LabelRow(){
            $0.title = "登录密码"
            $0.value = "修改密码"
        }.onCellSelection({ (cell,row) in
            self.performSegue(withIdentifier: "showPassworChangeSegue", sender: nil)
        })
        if O2IsConnect2Collect {
            let mobile = O2AuthSDK.shared.bindDevice()?.mobile
            form +++ Section()
            <<< LabelRow() {
                $0.title = "变更手机号码"
                $0.value = mobile
            }.onCellSelection({ (cell,row) in
                self.performSegue(withIdentifier: "showMobileChangeSegue", sender: nil)
            })
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

//
//  SAccoutSecViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Eureka


class SAccoutSecViewController: FormViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        SwitchRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_value_textFont
            cell.textLabel?.textColor  = setting_value_textColor
            cell.detailTextLabel?.font = setting_value_textFont
            cell.detailTextLabel?.textColor = setting_value_textColor
            cell.accessoryType = .disclosureIndicator
        }
        //设置配置列
        form +++ Section(footer: "开启账号保护后，在不常用的手机上登录智合，需要通过短信验证你的手机号码。")
        <<< SwitchRow("set_none") {
                $0.title = "帐号保护"
                $0.value = AppConfigSettings.shared.accountIsSecurity
                }.onChange { row in
                    AppConfigSettings.shared.accountIsSecurity = row.value!
        }
        

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

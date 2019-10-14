//
//  SAccoutSecViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Eureka
import CocoaLumberjack
import O2OA_Auth_SDK

// 系统生物识别登录 updated by fancylou on 2019-3-11
class SAccoutSecViewController: FormViewController {
    var bioType = O2BiometryType.None
    var typeTitle = "生物识别登录"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let bioAuthUser = AppConfigSettings.shared.bioAuthUser
        var isAuthed = false
        //判断是否当前绑定的服务器的
        if !bioAuthUser.isBlank {
            let array = bioAuthUser.split("^^")
            if array.count == 2 {
                if array[0] == O2AuthSDK.shared.bindUnit()?.id {
                    isAuthed = true
                }
            }
        }
        DDLogDebug("bio user: \(bioAuthUser)")
        bioType = O2BioLocalAuth.shared.checkBiometryType()
        switch bioType {
        case O2BiometryType.FaceID:
            typeTitle = "人脸识别登录"
            break
        case O2BiometryType.TouchID:
            typeTitle = "指纹识别登录"
            break
        case O2BiometryType.None:
            typeTitle = "生物识别登录"
            break
        }
        
        
        SwitchRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_value_textFont
            cell.textLabel?.textColor  = setting_value_textColor
            cell.detailTextLabel?.font = setting_value_textFont
            cell.detailTextLabel?.textColor = setting_value_textColor
            cell.accessoryType = .disclosureIndicator
        }
        //设置配置列
        form +++ Section(footer: "开启\(typeTitle)后，在登录的时候无需输入用户名密码或者短信验证码，一键验证登录，方便快捷！")
        <<< SwitchRow("set_none") {
                $0.title = typeTitle
                $0.value = isAuthed
                $0.cell?.switchControl?.addTarget(self, action: #selector(self.clickSwitch), for: UIControl.Event.touchUpInside)
            }
        

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc private func clickSwitch() {
        let row = self.form.rowBy(tag: "set_none") as? SwitchRow
        if self.bioType != O2BiometryType.None {
            O2BioLocalAuth.shared.auth(reason: "开启\(typeTitle)", selfAuthTitle: "再想想", block: { (result, errorMsg) in
                switch result {
                case O2BioEvaluateResult.SUCCESS:
                    if row?.value == true { //开启
                        //这里需要将unitId和 userId 组合 否则切换登录服务器后 绑定的userId不能正常登录
                        let unitId = O2AuthSDK.shared.bindUnit()?.id ?? ""
                        let userId = O2AuthSDK.shared.myInfo()?.id ?? ""
                        AppConfigSettings.shared.bioAuthUser = "\(unitId)^^\(userId)"
                    }else { //关闭
                        AppConfigSettings.shared.bioAuthUser = ""
                    }
                    break
                case O2BioEvaluateResult.FALLBACK:
                    //还原value
                    if row?.value == true {
                        row?.value = false
                    }else {
                        row?.value = true
                    }
                    row?.updateCell()
                    //self.showError(title: "已取消!")
                    break
                case O2BioEvaluateResult.LOCKED:
                    //还原value
                    if row?.value == true {
                        row?.value = false
                    }else {
                        row?.value = true
                    }
                    row?.updateCell()
                    
                    self.showSystemAlert(title: "提示", message: "多次错误，已被锁定，请到手机解锁界面输入密码!", okHandler: { (action) in
                        //
                    })
                    break
                    
                case O2BioEvaluateResult.FAILURE:
                    //还原value
                    if row?.value == true {
                        row?.value = false
                    }else {
                        row?.value = true
                    }
                    row?.updateCell()
                    DDLogError(errorMsg)
                    self.showError(title: "验证失败！")
                    break
                }
            })
        }else {
            self.showError(title: "手机系统未开启或不支持识别功能")
        }
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

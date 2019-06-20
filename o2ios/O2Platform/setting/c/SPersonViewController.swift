//
//  SPersonViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Eureka
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import BSImagePicker
import Photos
import CocoaLumberjack
import O2OA_Auth_SDK

class SPersonViewController: FormViewController {
    
    var person:O2PersonInfo? = nil
    
    var updateFlag:Bool = false

    var myIconView:UIImageView = UIImageView()
    
    private let viewModel: O2PersonalViewModel = {
        return O2PersonalViewModel()
    }()

    
    override func viewDidLoad() {
        super.viewDidLoad()
        //右边按钮
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "更改", style: .plain, target: self, action: #selector(self.submitPersonUpdateAction(sender:)))
        
        ImageRow.defaultCellUpdate = { cell, row in
            cell.accessoryView?.layer.cornerRadius = 17
            cell.accessoryView?.frame = CGRect(x: 0, y: 0, width: 34, height: 34)
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        LabelRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            cell.accessoryType = .disclosureIndicator
        }
        EmailRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        PhoneRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        TextRow.defaultCellUpdate = {
            cell,row in
            //修改输入法顶部的“完成”按钮的颜色字体
            if cell.inputAccessoryView?.isKind(of: NavigationAccessoryView.self) == true {
                let view = cell.textField.inputAccessoryView as? NavigationAccessoryView
                view?.doneButton.setTitleTextAttributes([
                    NSAttributedString.Key.font:navbar_item_font,
                    NSAttributedString.Key.foregroundColor: base_blue_color
                    ], for:UIControl.State())
            }
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        ButtonRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_item_textFont
            cell.textLabel?.theme_textColor = ThemeColorPicker(keyPath: "Base.base_color")

        }
        ActionSheetRow<String>.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor =  setting_content_textColor
        }
        self.loadCurrentPersonInfo()
    }
    
    
    private func loadCurrentPersonInfo(){
        self.showMessage(title: "加载中...")
        self.viewModel.loadMyInfo().then { (person) in
                DispatchQueue.main.async {
                    self.person = person
                    self.loadAvatar()
                }
            }.catch { (error) in
                DispatchQueue.main.async {
                    self.showError(title: "\(error)\n个人信息载入出错!")
                }
        }
    }
    private func loadAvatar() {
        let avatarUrlString = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":self.person?.id as AnyObject])
        let avatarUrl = URL(string: avatarUrlString!)
        self.myIconView.bounds = CGRect(x: 0, y: 0, width: 34, height: 34)
        self.myIconView.hnk_setImageFromURL(avatarUrl!, placeholder: UIImage(named: "personDefaultIcon"), format: nil, failure: { (error) in
            DDLogError("加载头像异常, \(String(describing: error))")
            self.setupForm(nil)
        }) { (image) in
            self.setupForm(image)
        }
        
    }
    
    private func setupForm(_ avatarImage: UIImage?) {
        self.title = self.person?.name
        if avatarImage == nil {
            DDLogError("没有头像数据！！！！！")
        }
        form +++ Section()
            
            <<< ImageRow("myAvatar"){ row in
                row.title = "头像"
                row.sourceTypes = [.PhotoLibrary,.Camera]
                row.clearAction = .no
                row.value = avatarImage
                }.onChange({ (row:ImageRow) in
                    if let image = row.value {
                        self.viewModel.updateMyIcon(icon: image)
                            .then({ (result) in
                                DDLogInfo("上次头像成功！，result:\(result)")
                                self.notifyReloadAvatar()
                            }).catch({ (error) in
                                DDLogInfo("上传头像失败，\(error)")
                                self.showError(title: "上传头像失败，\(error)")
                            })
                    }else {
                        row.value = UIImage(named: "personDefaultIcon")
                    }
                })
            <<< LabelRow(){
                $0.title = "工号"
                $0.value = person?.employee
            }
            <<< TextRow(){
                $0.title = "名字"
                $0.value = person?.name
                }.onChange({ (row) in
                    self.person?.name = row.value
                    self.updateFlag = true
                })
            
            <<< ActionSheetRow<String>(){
                $0.title = "性别"
                $0.selectorTitle = "请选择性别"
                $0.options = ["男","女"]
                $0.cancelTitle = "取消"
                $0.value = person?.genderType == "f" ? "女":"男"
                }.onChange({ (row:ActionSheetRow<String>) in
                    if row.value == "男" {
                        self.person?.genderType = "m"
                    }else if row.value == "女"{
                        self.person?.genderType = "f"
                    }
                    self.updateFlag = true
                })
            
            +++ Section()
            <<< EmailRow(){
                $0.title = "Email"
                $0.value = person?.mail
                }.onChange({ (row) in
                    self.person?.mail = row.value
                    self.updateFlag = true
                })
            
            <<< PhoneRow(){
                $0.title = "手机"
                $0.value = person?.mobile
                }.onChange({ (row) in
                    self.person?.mobile = row.value
                    self.updateFlag = true
                })
            
            <<< TextRow(){
                $0.title = "微信"
                $0.value = person?.weixin
                }.onChange({ (row) in
                    self.person?.weixin = row.value
                    self.updateFlag = true
                })
            
            
            <<< TextRow(){
                $0.title = "QQ"
                $0.value = person?.qq
                }.onChange({ (row) in
                    self.person?.qq = row.value
                    self.updateFlag = true
                })
            
            +++ Section()
            <<< ButtonRow() {
                $0.title = "退出登录"
                }.onCellSelection({ (cell, row) in
                    let alertController = UIAlertController(title: "退出登录", message: "确定要退出系统吗？", preferredStyle: .actionSheet)
                    let okAction = UIAlertAction(title: "退出", style: .destructive, handler: { _ in
                        self.logout()
                    })
                    let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
                    alertController.addAction(okAction)
                    alertController.addAction(cancelAction)
                    self.present(alertController, animated: true, completion: nil)
                }).cellSetup({ (cell:ButtonCellOf<String>, buttonRow) in
                    cell.textLabel?.font = UIFont(name: "PingFangSC-Regular", size: 14.0)
                    cell.textLabel?.theme_textColor = ThemeColorPicker(keyPath: "Base.base_color")
                })
        self.dismissProgressHUD()
    }
    
    func logout()  {
        O2AuthSDK.shared.logout { (result, msg) in
            DDLogInfo("O2 登出 \(result), msg：\(msg ?? "")")
        }
        JMSGUser.logout { (resultObject, errMsg) in
            if errMsg == nil {
                print("IM成功退出")
            }else{
                print("IM退出失败,error = \(String(describing: errMsg))")
            }
        }
        self.forwardDestVC("login", "loginVC")
    }
    
    //提交更新
    @objc func submitPersonUpdateAction(sender:UIBarButtonItem){
        if updateFlag ==  true {
            self.viewModel.updateMyInfo(person: self.person!).then { (result) in
                DDLogInfo("更新个人信息成功，\(result)")
                self.showSuccess(title: "更新成功！")
                }.catch { (error) in
                    DDLogError("更新个人信息失败，\(error)")
                    self.showError(title: "更新个人信息失败，\(error)")
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    /// 通知更新头像了
    @objc private func notifyReloadAvatar() {
        NotificationCenter.default.post(name: Notification.Name("reloadMyIcon"), object: nil)
    }
}

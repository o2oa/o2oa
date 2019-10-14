//
//  O2JsApiBizUtil.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/19.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import WebKit
import CocoaLumberjack


class O2JsApiBizUtil: O2WKScriptMessageHandlerImplement {
    
    let viewController: BaseWebViewUIViewController
    
    init(viewController: BaseWebViewUIViewController) {
        self.viewController = viewController
    }
    
    
    func userController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.body is NSString {
            let json = message.body as! NSString
            DDLogDebug("message json:\(json)")
            if let jsonData = String(json).data(using: .utf8) {
                let dicArr = try! JSONSerialization.jsonObject(with: jsonData, options: .allowFragments) as! [String:AnyObject]
                if let type = dicArr["type"] as? String {
                    switch type {
                    case "contact.departmentPicker":
                        departmentsPicker(json: String(json))
                        break
                    case "contact.identityPicker":
                        identityPicker(json: String(json))
                        break
                    case "contact.groupPicker":
                        groupPicker(json: String(json))
                        break
                    case "contact.personPicker":
                        personPicker(json: String(json))
                        break
                    case "contact.complexPicker":
                        complexPicker(json: String(json))
                        break
                    default :
                        DDLogError("notification类型不正确, type: \(type)")
                    }
                }else {
                    DDLogError("util类型不存在 json解析异常。。。。。")
                }
            }else {
                DDLogError("消息json解析异常。。。")
            }
        }else {
            DDLogError("message 消息 body 类型不正确。。。")
        }
    }
    
    private func departmentsPicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2BizUnitPickerMessage>.deserialize(from: json) {
            let maxNumber = alert.data?.maxNumber ?? 0
            let pickedDepartments = alert.data?.pickedDepartments ?? []
            let multiple = alert.data?.multiple ?? true
            let orgType = alert.data?.orgType ?? ""
            let topList = alert.data?.topList ?? []
            let callback = alert.callback ?? ""
            self.showPicker(callback: callback,
                            pickMode: ["departmentPicker"],
                            maxNumber: maxNumber,
                            multiple: multiple,
                            orgType: orgType,
                           topList: topList,
                           deptPickedList: pickedDepartments)
        }else {
            DDLogError("departmentsPicker, 解析json失败")
        }
    }
    private func identityPicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2BizIdentityPickerMessage>.deserialize(from: json) {
            let maxNumber = alert.data?.maxNumber ?? 0
            let pickedIdentities = alert.data?.pickedIdentities ?? []
            let multiple = alert.data?.multiple ?? true
            let dutyList = alert.data?.duty ?? []
            let topList = alert.data?.topList ?? []
            let callback = alert.callback ?? ""
            self.showPicker(callback: callback,
                            pickMode: ["identityPicker"],
                            maxNumber: maxNumber,
                            multiple: multiple,
                            dutyList: dutyList,
                            topList: topList,
                            idPickedList: pickedIdentities)
        }else {
            DDLogError("identityPicker, 解析json失败")
        }
    }
    private func groupPicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2BizGroupPickerMessage>.deserialize(from: json) {
            let maxNumber = alert.data?.maxNumber ?? 0
            let pickedGroups = alert.data?.pickedGroups ?? []
            let multiple = alert.data?.multiple ?? true
            let callback = alert.callback ?? ""
            self.showPicker(callback: callback,
                            pickMode: ["groupPicker"],
                            maxNumber: maxNumber,
                            multiple: multiple,
                            groupPickedList: pickedGroups)
        }else {
            DDLogError("groupPicker, 解析json失败")
        }
    }
    private func personPicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2BizPersonPickerMessage>.deserialize(from: json) {
            let maxNumber = alert.data?.maxNumber ?? 0
            let pickedUsers = alert.data?.pickedUsers ?? []
            let multiple = alert.data?.multiple ?? true
            let callback = alert.callback ?? ""
            self.showPicker(callback: callback,
                            pickMode: ["personPicker"],
                            maxNumber: maxNumber,
                            multiple: multiple,
                            userPickedList: pickedUsers)
        }else {
            DDLogError("personPicker, 解析json失败")
        }
    }
    private func complexPicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2BizComplexPickerMessage>.deserialize(from: json) {
            let pickMode = alert.data?.pickMode ?? []
            let maxNumber = alert.data?.maxNumber ?? 0
            let pickedDepartments = alert.data?.pickedDepartments ?? []
            let pickedIdentities = alert.data?.pickedIdentities ?? []
            let pickedGroups = alert.data?.pickedGroups ?? []
            let pickedUsers = alert.data?.pickedUsers ?? []
            let multiple = alert.data?.multiple ?? true
            let orgType = alert.data?.orgType ?? ""
            let dutyList = alert.data?.duty ?? []
            let topList = alert.data?.topList ?? []
            let callback = alert.callback ?? ""
            self.showPicker(callback: callback,
                             pickMode: pickMode,
                            maxNumber: maxNumber,
                            multiple: multiple,
                            orgType: orgType,
                            dutyList: dutyList,
                            topList: topList,
                            deptPickedList: pickedDepartments,
                            idPickedList: pickedIdentities,
                            groupPickedList: pickedGroups,
                            userPickedList: pickedUsers
                           )
        }else {
            DDLogError("complexPicker, 解析json失败")
        }
    }
    
    private func showPicker(callback: String, pickMode:[String], maxNumber: Int = 0, multiple:Bool = true, orgType: String = "", dutyList:[String] = [], topList:[String] = [], deptPickedList:[String] = [], idPickedList:[String] = [], groupPickedList:[String] = [], userPickedList:[String] = []) {
        var modes:[ContactPickerType] = []
        if pickMode.count > 0 {
            pickMode.forEach { (str) in
                switch str {
                case "departmentPicker":
                    modes.append(ContactPickerType.unit)
                    break
                case "identityPicker":
                    modes.append(ContactPickerType.identity)
                    break
                case "groupPicker":
                    modes.append(ContactPickerType.group)
                    break
                case "personPicker":
                    modes.append(ContactPickerType.person)
                    break
                default:
                    break
                }
            }
        }else {
            modes = [ContactPickerType.unit, ContactPickerType.identity, ContactPickerType.group, ContactPickerType.person]
        }
        
        if let v = ContactPickerViewController.providePickerVC(
            pickerModes:modes,
            topUnitList: topList,
            unitType: orgType,
            maxNumber: maxNumber,
            multiple: multiple,
            dutyList: dutyList,
            initDeptPickedArray: deptPickedList,
            initIdPickedArray: idPickedList,
            initGroupPickedArray: groupPickedList,
            initUserPickedArray: userPickedList,
            pickedDelegate: { (result: O2BizContactPickerResult) in
                let json = result.toJSONString() ?? "{}"
                DDLogDebug("返回选择结果：\(json)")
                self.evaluateJs(callBackJs: "\(callback)('\(json)')")
                }
            ) {
            self.viewController.navigationController?.pushViewController(v, animated: true)
        }else {
            self.viewController.showError(title: "选择器生成错误。。。。")
        }
    }
    
    private func evaluateJs(callBackJs: String) {
        DDLogDebug("执行回调js："+callBackJs)
        self.viewController.webView.evaluateJavaScript(callBackJs, completionHandler: { (result, err) in
            DDLogDebug("回调js执行完成！")
        })
    }
}

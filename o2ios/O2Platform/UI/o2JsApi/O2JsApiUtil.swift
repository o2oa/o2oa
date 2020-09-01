//
//  O2JsApiUtil.swift
//  O2Platform
//
//  Created by FancyLou on 2019/5/7.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import WebKit
import CocoaLumberjack


class O2JsApiUtil: O2WKScriptMessageHandlerImplement {
   
    
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
                    case "date.datePicker":
                        datePicker(json: String(json))
                        break
                    case "date.timePicker":
                        timePicker(json: String(json))
                        break
                    case "date.dateTimePicker":
                        dateTimePicker(json: String(json))
                        break
                    case "calendar.chooseOneDay":
                        calendarPickDay(json: String(json))
                        break
                    case "calendar.chooseDateTime":
                        calendarPickerDateTime(json: String(json))
                        break
                    case "calendar.chooseInterval":
                        calendarPickerDateInterval(json: String(json))
                        break
                    case "device.getPhoneInfo":
                        getPhoneInfo(json: String(json))
                        break
                    case "device.scan":
                        scan(json: String(json))
                        break
                    case "device.location":
                        locationSingle(json: String(json))
                        break
                    case "device.openMap":
                        openMap(json: String(json))
                        break
                    case "navigation.setTitle":
                        navigationSetTitle(json: String(json))
                        break
                    case "navigation.close":
                        navigationClose(json: String(json))
                        break
                    case "navigation.goBack":
                        navigationGoBack(json: String(json))
                        break
                    default:
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
   
    
    private func datePicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilPicker>.deserialize(from: json) {
            let title = alert.data?.value ?? ""
            DDLogDebug("value:\(title)")
            let defaultDate: Date!
            if title.isBlank {
                defaultDate = Date()
            }else {
                defaultDate = title.toDate(formatter: "yyyy-MM-dd")
            }
            let picker = QDatePicker{ (date: String) in
                print(date)
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)('{\"value\":\"\(date)\"}')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            picker.themeColor = O2ThemeManager.color(for: "Base.base_color")!
            picker.datePickerStyle = .YMD
            picker.pickerStyle = .datePicker
            picker.showDatePicker(defaultDate: defaultDate)
        }else {
            DDLogError("datePicker, 解析json失败")
        }
    }
    private func timePicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilPicker>.deserialize(from: json) {
            let title = alert.data?.value ?? ""
            let defaultDate: Date!
            if title.isBlank {
                defaultDate = Date()
            }else {
                let ymd = Date().formatterDate(formatter: "yyyy-MM-dd")
                defaultDate = (ymd+" "+title).toDate(formatter: "yyyy-MM-dd HH:mm")
            }
            let picker = QDatePicker{ (date: String) in
                print(date)
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)('{\"value\":\"\(date)\"}')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            picker.themeColor = O2ThemeManager.color(for: "Base.base_color")!
            picker.datePickerStyle = .HM
            picker.pickerStyle = .datePicker
            picker.showDatePicker(defaultDate: defaultDate)
        }else {
            DDLogError("datePicker, 解析json失败")
        }
    }
    private func dateTimePicker(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilPicker>.deserialize(from: json) {
            let title = alert.data?.value ?? ""
            let defaultDate: Date!
            if title.isBlank {
                defaultDate = Date()
            }else {
                defaultDate = title.toDate(formatter: "yyyy-MM-dd HH:mm")
            }
            let picker = QDatePicker{ (date: String) in
                print(date)
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)('{\"value\":\"\(date)\"}')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            picker.themeColor = O2ThemeManager.color(for: "Base.base_color")!
            picker.datePickerStyle = .YMDHM
            picker.pickerStyle = .datePicker
            picker.showDatePicker(defaultDate: defaultDate)
        }else {
            DDLogError("datePicker, 解析json失败")
        }
    }
    
    private func calendarPickDay(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilPicker>.deserialize(from: json) {
            let title = alert.data?.value ?? ""
            DDLogDebug("value:\(title)")
            let defaultDate: Date!
            if title.isBlank {
                defaultDate = Date()
            }else {
                defaultDate = title.toDate(formatter: "yyyy-MM-dd")
            }
            let calendarPicker = QCalendarPicker{ (date: String) in
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)('{\"value\":\"\(date)\"}')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            calendarPicker.calendarPickerStyle = .datePicker
            calendarPicker.showPickerWithDefault(defaultDate: defaultDate)
        }else {
            DDLogError("datePicker, 解析json失败")
        }
    }
    private func calendarPickerDateTime(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilPicker>.deserialize(from: json) {
            let title = alert.data?.value ?? ""
            DDLogDebug("value:\(title)")
            let defaultDate: Date!
            if title.isBlank {
                defaultDate = Date()
            }else {
                defaultDate = title.toDate(formatter: "yyyy-MM-dd HH:mm")
            }
            let calendarPicker = QCalendarPicker{ (date: String) in
                if alert.callback != nil {
                    let callJs = "\(alert.callback!)('{\"value\":\"\(date)\"}')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            calendarPicker.calendarPickerStyle = .dateTimePicker
            calendarPicker.showPickerWithDefault(defaultDate: defaultDate)
        }else {
            DDLogError("datePicker, 解析json失败")
        }
    }
    
    private func calendarPickerDateInterval(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilPicker>.deserialize(from: json) {
            let start = alert.data?.startDate ?? ""
            let end = alert.data?.endDate ?? ""
            DDLogDebug("start:\(start) , end:\(end)")
            let startDate: Date!
            if start.isBlank {
                startDate = Date()
            }else {
                startDate = start.toDate(formatter: "yyyy-MM-dd")
            }
            let endDate: Date!
            if end.isBlank {
                endDate = Date()
            }else {
                endDate = end.toDate(formatter: "yyyy-MM-dd")
            }
            let calendarPicker = QCalendarPicker{ (date: String) in
                if alert.callback != nil {
                    let result = date.split(" ")
                    let callJs = "\(alert.callback!)('{\"startDate\":\"\(result[0])\", \"endDate\":\"\(result[1])\"}')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
            calendarPicker.calendarPickerStyle = .dateIntervalPicker
            calendarPicker.showPickerWithDefault(defaultDate: startDate, endDate: endDate)
        }else {
            DDLogError("calendarPickerDateInterval, 解析json失败")
        }
    }
    
    
    //获取手机信息
    private func getPhoneInfo(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilNavigation>.deserialize(from: json) {
            if alert.callback != nil {
                DeviceUtil.shared.getDeviceInfoForJsApi { (info) in
                    let backData = info.toJSONString(prettyPrint: false) ?? ""
                    let callJs = "\(alert.callback!)('\(backData)')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
        }else {
            DDLogError("getPhoneInfo, 解析json失败")
        }
    }
    //扫二维码返回结果
    private func scan(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilNavigation>.deserialize(from: json) {
            if alert.callback != nil {
                //扫一扫 。。。返回结果
                ScanHelper.openScan(vc: self.viewController, callbackResult: { (result) in
                    let callJs = "\(alert.callback!)('{\"text\": \"\(result)\"}')"
                    self.evaluateJs(callBackJs: callJs)
                })
            }
        }else {
            DDLogError("getPhoneInfo, 解析json失败")
        }
    }
    //单次定位
    private func locationSingle(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilNavigation>.deserialize(from: json) {
            if alert.callback != nil {
                DispatchQueue.main.async {
                    self.viewController.locationCallBack = { result in
                        guard let r = result else {
                            DDLogError("没有获取到位置信息")
                            return
                        }
                        let callJs = "\(alert.callback!)('\(r)')"
                        self.evaluateJs(callBackJs: callJs)
                    }
                    self.viewController.startLocation()
                }
            }
        }else {
            DDLogError("locationSingle, 解析json失败")
        }
    }
    
    //打开地图位置
    private func openMap(json: String) {
        if let map = O2WebViewBaseMessage<O2UtilOpenMap>.deserialize(from: json) {
            if let callback = map.callback, let data = map.data  {
                DispatchQueue.main.async {
                    IMShowLocationViewController.pushShowLocation(vc: self.viewController, latitude: data.latitude,
                                                                  longitude: data.longitude, address: data.address, addressDetail: data.addressDetail)
                    let callJs = "\(callback)('{}')"
                    self.evaluateJs(callBackJs: callJs)
                }
            }
        }else {
            DDLogError("openMap, 解析json失败")
        }
    }
    
    //设置t标题
    private func navigationSetTitle(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilNavigation>.deserialize(from: json) {
            let title = alert.data?.title ?? ""
            if title != "" {
                self.viewController.title = title
            }
            if alert.callback != nil {
                let callJs = "\(alert.callback!)('{}')"
                self.evaluateJs(callBackJs: callJs)
            }
        }else {
            DDLogError("navigationSetTitle, 解析json失败")
        }
    }
    //关闭窗口
    private func navigationClose(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilNavigation>.deserialize(from: json) {
            self.closeSelf()
            if alert.callback != nil {
                let callJs = "\(alert.callback!)('{}')"
                self.evaluateJs(callBackJs: callJs)
            }
        }else {
            DDLogError("navigationClose, 解析json失败")
        }
    }
    //返回上级 html history
    private func navigationGoBack(json: String) {
        if let alert = O2WebViewBaseMessage<O2UtilNavigation>.deserialize(from: json) {
            if self.viewController.webView.canGoBack {
                self.viewController.webView.goBack()
            }else {
                self.closeSelf()
            }
            if alert.callback != nil {
                let callJs = "\(alert.callback!)('{}')"
                self.evaluateJs(callBackJs: callJs)
            }
        }else {
            DDLogError("navigationGoBack, 解析json失败")
        }
    }
    
    private func closeSelf() {
        guard let vcs = self.viewController.navigationController?.viewControllers else {
            self.viewController.navigationController?.dismiss(animated: false, completion: nil)
            return
        }
        if vcs.count > 0 {
            if vcs[vcs.count - 1] == self.viewController {
                self.viewController.navigationController?.popViewController(animated: false)
            }
        }else {
             self.viewController.navigationController?.dismiss(animated: false, completion: nil)
        }
    }
    
    
    private func evaluateJs(callBackJs: String) {
        DDLogDebug("执行回调js："+callBackJs)
        self.viewController.webView.evaluateJavaScript(callBackJs, completionHandler: { (result, err) in
            DDLogDebug("回调js执行完成！")
        })
    }
} 

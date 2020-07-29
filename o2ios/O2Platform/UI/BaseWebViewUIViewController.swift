//
//  BaseWebViewUIViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/11.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import CocoaLumberjack


protocol BaseWebViewUIViewControllerJSDelegate {
    func closeUIViewWindow()
    func actionBarLoaded(show: Bool)
}

protocol O2WKScriptMessageHandlerImplement {
    func userController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage)
}

open class BaseWebViewUIViewController: UIViewController {
    
    var webView:WKWebView!
    var delegate: BaseWebViewUIViewControllerJSDelegate?
    var o2WKScriptHandlers: [String : O2WKScriptMessageHandlerImplement] = [:]
    
    //定位用
    var locationCallBack: (String?) -> Void = {(result) in }
    private var locService: BMKLocationManager?
    private var searchAddress: BMKGeoCodeSearch?
    
    
    //继承的子类如果要添加MessageHandler必须在theWebView方法前使用
    func addScriptMessageHandler(key: String, handler: O2WKScriptMessageHandlerImplement)  {
        o2WKScriptHandlers[key] = handler
    }
    
    
    open func theWebView(){
        let baseJsHandler = O2BaseJsMessageHandler(viewController: self)
        // bbs 回复
        addScriptMessageHandler(key: "ReplyAction", handler: baseJsHandler)
        // protal已经存在ActionBar
        addScriptMessageHandler(key: "actionBarLoaded", handler: baseJsHandler)
        // 打开工作 {"work":"", "workCompleted":"", "title":""}
        addScriptMessageHandler(key: "openO2Work", handler: baseJsHandler)
        // 4个分类 task taskCompleted read readCompleted
        addScriptMessageHandler(key: "openO2WorkSpace", handler: baseJsHandler)
        // 打开cms appId
        addScriptMessageHandler(key: "openO2CmsApplication", handler: baseJsHandler)
        // 打开cms docId docTitle
        addScriptMessageHandler(key: "openO2CmsDocument", handler: baseJsHandler)
        // 打开meeting
        addScriptMessageHandler(key: "openO2Meeting", handler: baseJsHandler)
        // 打开 calendar
        addScriptMessageHandler(key: "openO2Calendar", handler: baseJsHandler)
        // 打开扫一扫
        addScriptMessageHandler(key: "openScan", handler: baseJsHandler)
        
        addScriptMessageHandler(key: "openO2Alert", handler: baseJsHandler)
        // 打开钉钉
        addScriptMessageHandler(key: "openDingtalk", handler: baseJsHandler)
        // 关闭当前UIViewController
        addScriptMessageHandler(key:"closeNativeWindow", handler: baseJsHandler)
        // 上传图片到云盘，放入对应的业务区域
        addScriptMessageHandler(key: "uploadImage2FileStorage", handler: baseJsHandler)
        // 打印日志
        addScriptMessageHandler(key: "o2mLog", handler: baseJsHandler)
        
        //o2m.notification
        let o2Notification = O2JsApiNotification(viewController: self)
        addScriptMessageHandler(key: "o2mNotification", handler: o2Notification)
        //o2m.util
        let o2Util = O2JsApiUtil(viewController: self)
        addScriptMessageHandler(key: "o2mUtil", handler: o2Util)
        // o2m.biz
        let biz = O2JsApiBizUtil(viewController: self)
        addScriptMessageHandler(key: "o2mBiz", handler: biz)
        
        setupWebView()
    }
    
    
    public func setupWebView() {
        let userContentController = WKUserContentController()
        //cookie脚本
        if let cookies = HTTPCookieStorage.shared.cookies {
            let script = getJSCookiesString(cookies: cookies)
            let cookieScript = WKUserScript(source: script, injectionTime: WKUserScriptInjectionTime.atDocumentStart, forMainFrameOnly: false)
            userContentController.addUserScript(cookieScript)
        }
        // console.log 打印到ios log debug用
        let consoleLog = "console.log = (function(oriLogFunc){return function(str){window.webkit.messageHandlers.o2mLog.postMessage(str);oriLogFunc.call(console,str);} })(console.log);"
        let logScript = WKUserScript(source: consoleLog, injectionTime: WKUserScriptInjectionTime.atDocumentStart, forMainFrameOnly: false)
        userContentController.addUserScript(logScript)
        let webViewConfig = WKWebViewConfiguration()
        webViewConfig.userContentController = userContentController
         //加入js-app message handler
        for item in o2WKScriptHandlers {
            userContentController.add(self, name: item.key)
        }
        self.webView = WKWebView(frame: self.view.frame, configuration: webViewConfig)
    }
    
    ///Generates script to create given cookies
    public func getJSCookiesString(cookies: [HTTPCookie]) -> String {
        var result = ""
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = NSTimeZone(abbreviation: "UTC") as TimeZone?
        dateFormatter.dateFormat = "EEE, d MMM yyyy HH:mm:ss zzz"
        
        for cookie in cookies {
            result += "document.cookie='\(cookie.name)=\(cookie.value); domain=\(cookie.domain); path=\(cookie.path); "
            if let date = cookie.expiresDate {
                result += "expires=\(dateFormatter.string(from: date)); "
            }
            if (cookie.isSecure) {
                result += "secure; "
            }
            result += "'; "
        }
        return result
    }
    
    
}

extension BaseWebViewUIViewController: WKScriptMessageHandler {
    
    public func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let name = message.name
        if let handler = o2WKScriptHandlers[name] {
            handler.userController(userContentController, didReceive: message)
        }else {
            let err = "没有找到对应的key，key:\(name)"
            self.showError(title: err)
        }
    }
}

extension BaseWebViewUIViewController: BMKLocationManagerDelegate, BMKGeoCodeSearchDelegate {
    public func startLocation() {
        //开始定位
        locService = BMKLocationManager()
        if locService == nil {
            locService?.desiredAccuracy = kCLLocationAccuracyBest
            //设置返回位置的坐标系类型
            locService?.coordinateType = .BMK09LL
            //设置距离过滤参数
            locService?.distanceFilter = kCLDistanceFilterNone;
            //设置预期精度参数
            locService?.desiredAccuracy = kCLLocationAccuracyBest;
            //设置应用位置类型
            locService?.activityType = .automotiveNavigation
            //设置是否自动停止位置更新
            locService?.pausesLocationUpdatesAutomatically = false
            //定位返回geo地址信息
            locService?.locatingWithReGeocode = true
            //后台定位
            locService?.allowsBackgroundLocationUpdates = true
        }
        if searchAddress == nil {
            searchAddress = BMKGeoCodeSearch()
        }
        searchAddress?.delegate = self
        locService?.delegate = self
        locService?.startUpdatingLocation()
    }
    
    public func stopLocation()  {
        locService?.stopUpdatingLocation()
        locService?.delegate = nil
        searchAddress?.delegate = nil
    }
    
    public func bmkLocationManager(_ manager: BMKLocationManager, didUpdate location: BMKLocation?, orError error: Error?) {
        if let loc = location?.location {
            DDLogDebug("当前位置,\(loc.coordinate.latitude),\(loc.coordinate.longitude)")
            //根据经纬度搜索到地址
            let re = BMKReverseGeoCodeSearchOption()
            re.location = CLLocationCoordinate2D(latitude: loc.coordinate.latitude, longitude: loc.coordinate.longitude)
            let _ = searchAddress?.reverseGeoCode(re)
            
        } else {
            DDLogError("没有获取到定位信息！！！！！")
        }
    }
    
    public func bmkLocationManager(_ manager: BMKLocationManager, didFailWithError error: Error?) {
        DDLogError("定位错误：\(String(describing: error?.localizedDescription))")
    }
    
    public func onGetReverseGeoCodeResult(_ searcher: BMKGeoCodeSearch?, result: BMKReverseGeoCodeSearchResult?, errorCode error: BMKSearchErrorCode) {
        //发送定位的实时位置及名称信息
        if let location = result?.location {
            var r = O2DeviceLocationResult()
            r.latitude = location.latitude
            r.longitude = location.longitude
            r.address = result?.address ?? "没有获取到地址！"
            locationCallBack(r.toJSONString())
        }else {
            var r = O2DeviceLocationResult()
            r.address = "没有获取到地址！"
            locationCallBack(r.toJSONString())
            DDLogError("搜索地址失败， \(error)")
        }
        //结束定位
        self.stopLocation()
    }
    
    public func onGetGeoCodeResult(_ searcher: BMKGeoCodeSearch!, result: BMKGeoCodeSearchResult!, errorCode error: BMKSearchErrorCode) {
        if Int(error.rawValue) == 0 {
            DDLogDebug("result \(String(describing: result))")
        } else {
            DDLogDebug("result error  errorCode = \(Int(error.rawValue))")
        }

    }
}

//
//  AppDelegate.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import AlamofireNetworkActivityIndicator
import UserNotifications
import O2OA_Auth_SDK
import Flutter
import IQKeyboardManagerSwift








let isProduction = true

@UIApplicationMain
class AppDelegate: FlutterAppDelegate, JPUSHRegisterDelegate, UNUserNotificationCenterDelegate {
  
    var _mapManager: BMKMapManager?
    
    //中心服务器节点类
    public static let o2Collect = O2Collect()
    //中心服务器绑定数据信息
    public static var deviceData = CollectDeviceData()
    //网络监听
    public let o2ReachabilityManager = O2ReachabilityManager.sharedInstance
    // flutter engine
    var flutterEngine : FlutterEngine?
    
    override func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        
        let themeName = AppConfigSettings.shared.themeName
        if themeName != "" {
            //主题
            print("主题色：\(themeName)")
            O2ThemeManager.setTheme(plistName: themeName, path: .mainBundle)
        }else {
            O2ThemeManager.setTheme(plistName: "red", path: .mainBundle)
        }
        //搜索框
        UISearchBar.appearance().theme_barTintColor = ThemeColorPicker(keyPath: "Base.base_color")
        UISearchBar.appearance().tintColor = UIColor.white
        UITextField.appearance(whenContainedInInstancesOf: [UISearchBar.self]).theme_tintColor = ThemeColorPicker(keyPath: "Base.base_color")
        
        
        
        //启动日志管理器
        O2Logger.startLogManager()
        //日志文件
        _ = O2Logger.getLogFiles()
        O2Logger.debug("设置运行版本==========,\(PROJECTMODE)")
        //网络检查
        o2ReachabilityManager.startListening()
        //Alamofire
        NetworkActivityIndicatorManager.shared.isEnabled = true
        
        
        
        //设置一个是否第一授权的标志
        if #available(iOS 10.0, *){
            let center = UNUserNotificationCenter.current()
            center.delegate = self
            let options:UNAuthorizationOptions = [.badge,.alert,.sound]
            center.requestAuthorization(options: options, completionHandler: { (granted, err) in
                if granted ==  true {
                    //记录已经打开授权
                    //print("aaaaaaaaaaaa")
                    AppConfigSettings.shared.notificationGranted = true
                    AppConfigSettings.shared.firstGranted = true
                    NotificationCenter.default.post(name: NSNotification.Name.init("SETTING_NOTI"), object: nil)
                }else{
                    //记录禁用授权
                    AppConfigSettings.shared.notificationGranted = false
                    AppConfigSettings.shared.firstGranted = true
                    NotificationCenter.default.post(name: NSNotification.Name.init("SETTING_NOTI"), object: nil)
                }
            })
            
        }else{
            let types:UIUserNotificationType = [.badge,.alert,.sound]
            let setting = UIUserNotificationSettings(types: types, categories: nil)
            UIApplication.shared.registerUserNotificationSettings(setting)
        }
        
        //UMessage.setLogEnabled(true)
        //蒲公英
        let pgyAppId = PGY_APP_ID
        PgyManager.shared().themeColor  = base_color
        PgyManager.shared().feedbackActiveType = KPGYFeedbackActiveType.pgyFeedbackActiveTypeThreeFingersPan
        PgyManager.shared().start(withAppId: pgyAppId)
        PgyUpdateManager.sharedPgy().start(withAppId: pgyAppId)
        if UIDevice.deviceModelReadable() == "Simulator" {
            AppDelegate.deviceData.name = UIDevice.idForVendor()!
        }
        //Buglyy异常上报
        Bugly.start(withAppId: BUGLY_ID)
        
        //JPush
        _setupJPUSH()
        JPUSHService.setup(withOption: launchOptions, appKey: JPUSH_APP_KEY, channel: JPUSH_channel, apsForProduction: isProduction)
        //JMessage
        JMessage.setupJMessage(launchOptions, appKey: JPUSH_APP_KEY, channel: JPUSH_channel, apsForProduction: isProduction, category: nil, messageRoaming: true)
        _setupJMessage()
        
        _mapManager = BMKMapManager()
        BMKMapManager.setCoordinateTypeUsedInBaiduMapSDK(BMK_COORDTYPE_BD09LL)
        _mapManager?.start(BAIDU_MAP_KEY, generalDelegate: nil)
        
        
        JPUSHService.registrationIDCompletionHandler { (resCode, registrationID) in
            if resCode == 0 {
                O2Logger.debug("registrationID获取成功\(registrationID ?? "")")
                 //AppDelegate.deviceData.name = registrationID
                O2AuthSDK.shared.setDeviceToken(token: registrationID ?? "registrationIDerror0x0x")
            }else{
                O2Logger.debug("registrationID获取失败，code:\(resCode)")
                O2AuthSDK.shared.setDeviceToken(token: registrationID ?? "registrationIDerror0x0x")
            }
        }
        
       
//        OOPlusButtonSubclass.register()
        OOTabBarHelper.initTabBarStyle()
        
        //
        IQKeyboardManager.shared.enable = true
        
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    
    // MARK:- private func Jpush
    private func _setupJPUSH() {
        let entity = JPUSHRegisterEntity()
        entity.types = NSInteger(UNAuthorizationOptions.alert.rawValue) |
            NSInteger(UNAuthorizationOptions.sound.rawValue) |
            NSInteger(UNAuthorizationOptions.badge.rawValue)
        JPUSHService.register(forRemoteNotificationConfig: entity, delegate: self)
    }
    
    // MARK: - private func
    private func _setupJMessage() {
        JMessage.setDebugMode()
        JMessage.add(self, with: nil)
        JMessage.register(forRemoteNotificationTypes:
            UNAuthorizationOptions.badge.rawValue |
                            UNAuthorizationOptions.sound.rawValue |
                            UNAuthorizationOptions.alert.rawValue,
                        categories: nil)
    }
    
    //注册 APNs 获得device token
    override func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        super.application(application, didRegisterForRemoteNotificationsWithDeviceToken: deviceToken)
        DDLogDebug("get the deviceToken  \(deviceToken)")
        NotificationCenter.default.post(name: Notification.Name(rawValue: "DidRegisterRemoteNotification"), object: deviceToken)
        JPUSHService.registerDeviceToken(deviceToken)
        JMessage.registerDeviceToken(deviceToken)
    }
    
   
    
    override func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        DDLogDebug("open url :\(url.absoluteString)")
        return true
    }
    
    
    override func application(_ application: UIApplication, didRegister notificationSettings: UIUserNotificationSettings) {
        if notificationSettings.types.rawValue == 0 {
            AppConfigSettings.shared.notificationGranted = false
            AppConfigSettings.shared.firstGranted = true
            NotificationCenter.default.post(name: NSNotification.Name.init("SETTING_NOTI"), object: nil)
        }else{
            AppConfigSettings.shared.notificationGranted = true
            AppConfigSettings.shared.firstGranted = true
            NotificationCenter.default.post(name: NSNotification.Name.init("SETTING_NOTI"), object: nil)
        }
    }
    
    //实现注册 APNs 失败接口
    override func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        DDLogError(error.localizedDescription)
        AppDelegate.deviceData.name = "104C9F7F-7403-4B3E-B6A2-C222C82074FF"
    }
    
    override func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any]) {
        JPUSHService.handleRemoteNotification(userInfo)
        O2Logger.debug("收到通知,\(userInfo)")
        NotificationCenter.default.post(name: Notification.Name(rawValue: "AddNotificationCount"), object: nil)  //把  要addnotificationcount
    }
    
//
//    override func application(_ application: UIApplication, didReceive notification: UILocalNotification) {
//        JPUSHService.showLocalNotification(atFront: notification, identifierKey: nil)
//    }
    
    override func applicationWillEnterForeground(_ application: UIApplication) {
        application.applicationIconBadgeNumber = 0
        application.cancelAllLocalNotifications()
    }
    
    override func applicationDidBecomeActive(_ application: UIApplication) {
        if UIDevice.deviceModelReadable() != "Simulator" {
            PgyUpdateManager.sharedPgy().checkUpdate(withDelegete: self, selector: #selector(updateVersion(_:)))
        }
        
    }
   
    override func applicationDidEnterBackground(_ application: UIApplication) {
        application.applicationIconBadgeNumber = 0
        application.cancelAllLocalNotifications()
    }
    
    
    deinit {
        o2ReachabilityManager.stopListening()
    }
    
   
    // iOS 12 Support
     @available(iOS 12.0, *)
    func jpushNotificationCenter(_ center: UNUserNotificationCenter!, openSettingsFor notification: UNNotification!) {
        // open
        if (notification != nil && (notification?.request.trigger?.isKind(of: UNPushNotificationTrigger.self) == true) ) {
            //从通知界面直接进入应用
            DDLogInfo("从通知界面直接进入应用............")
        }else{
            //从通知设置界面进入应用
            DDLogInfo("从通知设置界面进入应用............")
        }
    }
    
    
//
//    @available(iOS 12.0, *)
//    func userNotificationCenter(_ center: UNUserNotificationCenter, openSettingsFor notification: UNNotification?) {
//        if (notification != nil && (notification?.request.trigger?.isKind(of: UNPushNotificationTrigger.self) == true) ) {
//            //从通知界面直接进入应用
//            DDLogInfo("从通知界面直接进入应用............")
//        }else{
//            //从通知设置界面进入应用
//             DDLogInfo("从通知设置界面进入应用............")
//        }
//    }
    
    @available(iOS 10.0, *)
    func jpushNotificationCenter(_ center: UNUserNotificationCenter!, willPresent notification: UNNotification!,
                                 withCompletionHandler completionHandler: ((Int) -> Void)!) {
        let userInfo = notification.request.content.userInfo
        let request = notification.request // 收到推送的请求
        let content = request.content // 收到推送的消息内容
        let badge = content.badge // 推送消息的角标
        let body = content.body   // 推送消息体
        let sound = content.sound // 推送消息的声音
        let subtitle = content.subtitle // 推送消息的副标题
        let title = content.title // 推送消息的标题
        if (notification.request.trigger?.isKind(of: UNPushNotificationTrigger.self))! {
            JPUSHService.handleRemoteNotification(userInfo)
        }else{
            //判断为本地通知
            O2Logger.debug("iOS10 前台收到本地通知:{\nbody:\(body),\ntitle:\(title),\nsubtitle:\(subtitle),\nbadge:\(badge ?? 0),\nsound:\(sound.debugDescription)")
        }
        UIApplication.shared.applicationIconBadgeNumber = 0
        JPUSHService.setBadge(0)
        completionHandler(Int(UNNotificationPresentationOptions.alert.rawValue|UNNotificationPresentationOptions.badge.rawValue|UNNotificationPresentationOptions.sound.rawValue))
        
    }
    
    
    @available(iOS 10.0, *)
    func jpushNotificationCenter(_ center: UNUserNotificationCenter!, didReceive response: UNNotificationResponse!, withCompletionHandler completionHandler: (() -> Void)!) {
        
        let userInfo = response.notification.request.content.userInfo
        let request = response.notification.request // 收到推送的请求
        let content = request.content // 收到推送的消息内容
        
        let badge = content.badge // 推送消息的角标
        let body = content.body   // 推送消息体
        let sound = content.sound // 推送消息的声音
        let subtitle = content.subtitle // 推送消息的副标题
        let title = content.title // 推送消息的标题
        if (response.notification.request.trigger?.isKind(of: UNPushNotificationTrigger.self))! {
            JPUSHService.handleRemoteNotification(userInfo)
        }else{
            //判断为本地通知
            O2Logger.debug("iOS10 前台收到本地通知:{\nbody:\(body),\ntitle:\(title),\nsubtitle:\(subtitle),\nbadge:\(badge ?? 0),\nsound:\(sound.debugDescription)")
        }
        UIApplication.shared.applicationIconBadgeNumber = 0
        JPUSHService.setBadge(0)
        completionHandler()
    }
    
    
    @objc private func updateVersion(_ response:AnyObject?){
        O2Logger.debug("update be callbacked")
        if let obj = response {
            //ProgressHUD.dismiss()
            //print(obj)
            let appURLString = (obj as! NSDictionary)["downloadURL"]
            if  let appURL = URL(string: appURLString as! String) {
                if UIApplication.shared.canOpenURL(appURL) {
                    if UIApplication.shared.openURL(appURL) {
                        PgyUpdateManager.sharedPgy().updateLocalBuildNumber()
                    }
                }
            }
        }
    }
}

//MARK: - JMessage Delegate
extension AppDelegate: JMessageDelegate {
    func onDBMigrateStart() {

    }
    
    func onDBMigrateFinishedWithError(_ error: Error!) {
       // self.showSuccess(title: "数据库升级完成")
    }
    
    func onReceive(_ event: JMSGUserLoginStatusChangeEvent!) {
        switch event.eventType.rawValue {
//        case JMSGLoginStatusChangeEventType.eventNotificationLoginKicked.rawValue:
//            DDLogInfo("被踢 重新登录")
//            _reLogin()
//            break
        case JMSGLoginStatusChangeEventType.eventNotificationLoginKicked.rawValue,
             JMSGLoginStatusChangeEventType.eventNotificationServerAlterPassword.rawValue,
             JMSGLoginStatusChangeEventType.eventNotificationUserLoginStatusUnexpected.rawValue:
            DDLogInfo("被踢 重新登录")
            _reLogin()
        default:
            break
        }
    }
    func onReceive(_ event: JMSGFriendNotificationEvent!) {
        switch event.eventType.rawValue {
        case JMSGFriendEventType.eventNotificationReceiveFriendInvitation.rawValue,
             JMSGFriendEventType.eventNotificationAcceptedFriendInvitation.rawValue,
             JMSGFriendEventType.eventNotificationDeclinedFriendInvitation.rawValue:
            cacheInvitation(event: event)
        case JMSGFriendEventType.eventNotificationDeletedFriend.rawValue,
             JMSGFriendEventType.eventNotificationReceiveServerFriendUpdate.rawValue:
            NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateFriendList), object: nil)
        default:
            break
        }
    }
    
    private func cacheInvitation(event: JMSGNotificationEvent) {
        let friendEvent =  event as! JMSGFriendNotificationEvent
        let user = friendEvent.getFromUser()
        let reason = friendEvent.getReason()
        let info = JCVerificationInfo.create(username: user!.username, nickname: user?.nickname, appkey: user!.appKey!, resaon: reason, state: JCVerificationType.wait.rawValue)
        switch event.eventType.rawValue {
        case JMSGFriendEventType.eventNotificationReceiveFriendInvitation.rawValue:
            info.state = JCVerificationType.receive.rawValue
            JCVerificationInfoDB.shareInstance.insertData(info)
        case JMSGFriendEventType.eventNotificationAcceptedFriendInvitation.rawValue:
            info.state = JCVerificationType.accept.rawValue
            JCVerificationInfoDB.shareInstance.updateData(info)
            NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateFriendList), object: nil)
        case JMSGFriendEventType.eventNotificationDeclinedFriendInvitation.rawValue:
            info.state = JCVerificationType.reject.rawValue
            JCVerificationInfoDB.shareInstance.updateData(info)
        default:
            break
        }
        if UserDefaults.standard.object(forKey: kUnreadInvitationCount) != nil {
            let count = UserDefaults.standard.object(forKey: kUnreadInvitationCount) as! Int
            UserDefaults.standard.set(count + 1, forKey: kUnreadInvitationCount)
        } else {
            UserDefaults.standard.set(1, forKey: kUnreadInvitationCount)
        }
        NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateVerification), object: nil)
    }
    
    func _logout() {
        JMSGUser.logout(nil)
        JCVerificationInfoDB.shareInstance.queue = nil
        UserDefaults.standard.removeObject(forKey: kCurrentUserName)
        
//        let alertView = UIAlertView(title: "您的账号在其它设备上登录", message: "", delegate: self, cancelButtonTitle: "取消", otherButtonTitles: "重新登录")
//        alertView.show()
    }
    
    func _reLogin() {
        if let account = O2AuthSDK.shared.myInfo() {
            JMSGUser.login(withUsername: account.id!, password: "QazWsxEdc!@#", completionHandler: { (resultObject, errMsg) in
                if errMsg == nil {
                    DDLogInfo("IM登录成功,user = \(String(describing: resultObject))")
                }else{
                    DDLogError("IM登录失改,error = \(String(describing: errMsg))")
                }
            })
        }else {
            DDLogError("_reLogin 。。。。。 IM登录失败,error = 当前登录用户为空！！！")
        }
    }
}


//
//  ZLAppCollectionModel.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/6.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class ZLAppCollectionModel: NSObject {
    
    //APP实例数组
    fileprivate var apps:[O2App] {
        get {
            let app1 = O2App(title: "云盘", appId:"", storyBoard: "file", vcName: nil, segueIdentifier: "showFileCloudSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app2 = O2App(title: "会议管理", appId:"", storyBoard: "meeting", vcName: nil, segueIdentifier: "showMeetingSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app3 = O2App(title: "考勤管理", appId:"", storyBoard: "ic", vcName: nil, segueIdentifier: "showIcSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app4 = O2App(title: "BBS", appId:"", storyBoard: "bbs", vcName: nil, segueIdentifier: "showBBSSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app5 = O2App(title: "内容管理", appId:"", storyBoard: "cms", vcName: nil, segueIdentifier: "showCMSSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app6 = O2App(title: "待办", appId:"", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app7 = O2App(title: "待阅", appId:"", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app8 = O2App(title: "语音助手", appId: "", storyBoard: "file", vcName: nil, segueIdentifier: "showFileCloudSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app9 = O2App(title: "日程安排", appId: "", storyBoard: "calendar", vcName: nil, segueIdentifier: "", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            return [app1,app2,app3,app4,app5,app6,app7, app8, app9]
        }
    }
    
    
    /// Item最小的尺寸
    private static let MIN_ITEM_SIZE = 80.0
    
    /// Item实际尺寸存储值
    private var itemSize = ZLAppCollectionModel.MIN_ITEM_SIZE
    
    /// 每行Item数量
    var itemNumberByLine:Int  = 0 {
        willSet {
            if newValue > apps.count {
                itemNumberByLine = apps.count
            }else{
                itemNumberByLine = newValue
            }
        }
        didSet {
            ///设置实际大小
            itemRectSize = max(ZLAppCollectionModel.MIN_ITEM_SIZE,Double(SCREEN_WIDTH)/Double(itemNumberByLine))
            
            
        }
    }
    
    
    
    /// Item实际大小
    var itemRectSize:Double = 0.0
    
    /// 根据宽度对齐后的总的item数量
    var appTotalItemNumber:Int {
        return 0
    }
    
    /// 总行数
    var totalLineNumber:Int {
        return 0
    }
    
    required override init() {
        super.init()
    }
    

}

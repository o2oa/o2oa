//
//  OOTaskPageParameters.swift
//  o2app
//
//  Created by 刘振兴 on 2018/3/7.
//  Copyright © 2018年 zone. All rights reserved.
//

import Foundation
class OOTaskPageParameter: NSObject {
    
    var pageParameter:O2TaskPageParameter?
    
    override init() {
        super.init()
        pageParameter = O2TaskPageParameter()
    }
}

class O2TaskPageParameter {
    var currentPageId:String = "(0)"
    //当前页
    var currentPageNo:Int = 1
    //每页行数
    let countByPage:Int = 20
    //总页数
    private var totalPageCount = 1
    
    //总行数
    var totalLineCount:Int = -1 {
        didSet {
            if totalLineCount > 0 && totalLineCount > countByPage * currentPageNo {
                //总页数
                totalPageCount = Int(ceil(Double(totalLineCount) / Double(countByPage)))
            }
        }
    }
    
    init() {
        
    }
    
    func calcNextPageNo() -> Bool {
        if currentPageNo < totalPageCount {
            currentPageNo += 1
            return true
        }else{
            return false
        }
    }
}

//
//  O2InformationParameters.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/28.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
// MARK:- 读取Section列表参数
class O2InformationCategoryListParameter {
    var categoryIdList:[String]?
    var pageParameter:CommonPageParameter?
    
    func getParamDict() -> Dictionary<String,Any> {
        return ["categoryIdList":self.categoryIdList!]
    }
}

class O2InformationHomeParameter {
    
    var pageParameter:CommonPageParameter?
    
    init() {
        pageParameter = CommonPageParameter()
    }
}

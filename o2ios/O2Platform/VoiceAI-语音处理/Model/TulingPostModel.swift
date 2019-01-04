//
//  TulingPostModel.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/30.
//  Copyright © 2018 zoneland. All rights reserved.
//

import HandyJSON


class TulingPostModel: NSObject, DataModel {
    @objc var key: String? //api的key
    @objc var info: String? //问答内容
    
   required override init(){}
    
    
}

class TulingResponseModel: NSObject, DataModel {
    @objc var code: String? //类型代码
    @objc var text: String? //返回结果
    
    required override init(){}
}

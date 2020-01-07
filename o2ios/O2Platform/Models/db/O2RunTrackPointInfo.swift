//
//  O2RunTrackPointInfo.swift
//  O2OA
//
//  Created by FancyLou on 2019/9/15.
//  Copyright © 2019 O2OA. All rights reserved.
//

import Foundation
class O2RunTrackPointInfo {
    var id: String? //数据库id UUID防止冲突
    var runId: String? //O2RunTrackInfo 的 id
    var createTime: Date?//数据库插入时间
    
    var latitude: Double?
    var longitude: Double?
    var pointTime: Date?

    
}

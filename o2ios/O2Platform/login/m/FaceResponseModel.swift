//
//  FaceResponseModel.swift
//  O2Platform
//
//  Created by FancyLou on 2018/10/17.
//  Copyright © 2018 zoneland. All rights reserved.
//

import HandyJSON

class FaceSearchResponse: NSObject, DataModel {
    @objc var data: FaceSearchData?
    @objc var type: String?
    required override init() {
    }
}

class FaceSearchData: NSObject, DataModel {
    var time_used: Int?
    @objc var image_id: String?
    @objc var request_id: String?
    @objc var results: [FaceResult]?
    required override init() {
    }
}

class FaceResult: NSObject, DataModel {
    var confidence: Double?
    @objc var user_id: String?
    @objc var face_token: String?
    
    required override init() {
    }
}

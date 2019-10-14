//
//  Tuling123API.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/30.
//  Copyright © 2018 zoneland. All rights reserved.
//

import Moya

enum Tuling123API {
    case openapi(TulingPostModel)
}

extension Tuling123API: TargetType {
    
    var baseURL: URL {
        let baseURLString = "http://www.tuling123.com/openapi"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        return "/api"
    }
    
    var method: Moya.Method {
        switch self {
        case .openapi(_):
            return .post
        }
        
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .openapi(let model):
            return .requestParameters(parameters: model.toJSON() ?? [:], encoding: JSONEncoding.default)
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
   
    
}

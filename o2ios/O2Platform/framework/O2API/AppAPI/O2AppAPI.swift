//
//  O2AppAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/21.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

// MARK:- 所有调用的API枚举
enum O2AppAPI {
    case getNativeAppList
    case getPortalAppList
}

// MARK:- 上下文实现
extension O2AppAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return ""
    }
}


// MARK: - 是否需要加入x-token访问头
extension O2AppAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

extension O2AppAPI:TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.centerServerInfo()?.webServer
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .getNativeAppList:
            return "/application.json"
        case .getPortalAppList:
            return ""
        }
    }
    
    var method: Moya.Method {
        return .get
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        return .requestPlain
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}



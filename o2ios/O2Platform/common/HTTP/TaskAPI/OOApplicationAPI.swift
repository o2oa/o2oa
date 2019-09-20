//
//  OOApplicationAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2018/3/12.
//  Copyright © 2018年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

// MARK:- 所有调用的API枚举
enum OOApplicationAPI {
    case applicationList
    case applicationOnlyList
    case applicationItem(String)
    case availableIdentityWithProcess(String)
    case startProcess(String, String, String) // processId identity title
}

// MARK:- 上下文实现
extension OOApplicationAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_processplatform_assemble_surface"
    }
}


// MARK: - 是否需要加入x-token访问头
extension OOApplicationAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

extension OOApplicationAPI:TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_processplatform_assemble_surface)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .applicationList:
            return "/jaxrs/application/list/complex"
        case .applicationOnlyList:
            return "/jaxrs/application/list"
        case .applicationItem(let appId):
            return "/jaxrs/process/list/application/\(appId)"
        case .availableIdentityWithProcess(let processId):
            return "/jaxrs/process/list/available/identity/process/\(processId)"
        case .startProcess(let processId, _, _):
            return "/jaxrs/work/process/\(processId)"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .startProcess(_, _, _):
            return .post
        default:
            return .get
        }
    }
    
    var sampleData: Data {
        return  "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .startProcess(_, let identity, let title):
            return .requestParameters(parameters: ["identity": identity, "title": title], encoding: JSONEncoding.default)
        default:
            return .requestPlain
        }
        
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}


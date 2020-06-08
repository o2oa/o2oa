//
//  CommunicateAPI.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Moya
import O2OA_Auth_SDK

// MARK: - 消息模块

enum CommunicateAPI {
    case myConversationList
    
    
}

extension CommunicateAPI: OOAPIContextCapable {
    var apiContextKey: String {
           return "x_message_assemble_communicate"
       }
}

// 是否需要xtoken
extension CommunicateAPI: OOAccessTokenAuthorizable {
    var shouldAuthorize: Bool {
        return true
    }
}

extension CommunicateAPI: TargetType {
    var baseURL: URL {
        let model  = O2AuthSDK.shared.centerServerInfo()?.assembles?["x_message_assemble_communicate"]
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    
    var path: String {
        switch self {
        case .myConversationList:
            return "/jaxrs/im/conversation/list/my"
        
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .myConversationList:
            return .get
         
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .myConversationList:
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
   
}

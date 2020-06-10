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
    case msgListByPaging(Int, Int, String)
    
    
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
        case .msgListByPaging(let page, let size, _):
            return "/jaxrs/im/msg/list/\(page)/size/\(size)"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .myConversationList:
            return .get
        case .msgListByPaging(_, _, _):
            return .post
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .myConversationList:
            return .requestPlain
        case .msgListByPaging(_, _, let conversationId):
            let form = IMMessageRequestForm()
            form.conversationId = conversationId
            return .requestParameters(parameters: form.toJSON()!, encoding: JSONEncoding.default)
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
   
}

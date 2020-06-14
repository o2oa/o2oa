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
    case sendMsg(IMMessageInfo)
    case readConversation(String)
    case instantMessageList(Int)
    case createConversation(IMConversationInfo)
    
    
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
        case .sendMsg(_):
            return "/jaxrs/im/msg"
        case .readConversation(let conversationId):
            return "/jaxrs/im/conversation/\(conversationId)/read"
        case .instantMessageList(let count):
            return "/jaxrs/instant/list/currentperson/noim/count/\(count)/desc"
        case .createConversation(_):
            return "/jaxrs/im/conversation"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .myConversationList, .instantMessageList(_):
            return .get
        case .msgListByPaging(_, _, _), .sendMsg(_), .createConversation(_):
            return .post
        case .readConversation(_):
            return .put
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .myConversationList, .instantMessageList(_), .readConversation(_):
            return .requestPlain
        case .msgListByPaging(_, _, let conversationId):
            let form = IMMessageRequestForm()
            form.conversationId = conversationId
            return .requestParameters(parameters: form.toJSON()!, encoding: JSONEncoding.default)
        case .sendMsg(let msg):
            return .requestParameters(parameters: msg.toJSON()!, encoding: JSONEncoding.default)
        case .createConversation(let conv):
            return .requestParameters(parameters: conv.toJSON()!, encoding: JSONEncoding.default)
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
   
}

//
//  OOWorkAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2018/3/13.
//  Copyright © 2018年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

// MARK:- 所有调用的API枚举
enum OOWorkAPI {
    case createTask(String,Dictionary<String,String>)
    case getWork(String)//获取工作对象 如果工作已经结束了 500错误
}

// MARK:- 上下文实现
extension OOWorkAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_processplatform_assemble_surface"
    }
}


// MARK: - 是否需要加入x-token访问头
extension OOWorkAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

extension OOWorkAPI:TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_processplatform_assemble_surface)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .createTask(let processId,_):
            return "/jaxrs/work/process/\(processId)"
        case .getWork(let workId):
            return "/jaxrs/work/\(workId)"
        }
    }
    
    var method: Moya.Method {
        switch self {
            case .createTask(_,_):
                return .post
            case .getWork(_):
                return .get
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .createTask(_,let param):
            return .requestParameters(parameters: param, encoding: JSONEncoding.default)
        default:
           return .requestPlain
       }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}




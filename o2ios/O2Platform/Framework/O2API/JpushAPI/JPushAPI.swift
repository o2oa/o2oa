//
//  JPushAPI.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Moya
import O2OA_Auth_SDK


// x_jpush_assemble_control 极光推送模块

enum JPushAPI {
    case bindDevice(JPushDevice)
    case unBindDevice(String)
    
}
// 上下文根
extension JPushAPI: OOAPIContextCapable {
    var apiContextKey: String {
        return "x_jpush_assemble_control"
    }
}
// 是否需要xtoken
extension JPushAPI: OOAccessTokenAuthorizable {
    var shouldAuthorize: Bool {
        return true
    }
}

extension JPushAPI: TargetType {
    
    
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_jpush_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    
    var path: String {
        switch self {
        case .bindDevice(_):
            return "/jaxrs/device/bind"
        case .unBindDevice(let deviceName):
            return "/jaxrs/device/unbind/\(deviceName)/ios"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .bindDevice(_):
            return .post
        case .unBindDevice(_):
             return .delete
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .bindDevice(let device):
            return .requestParameters(parameters: device.toJSON()!, encoding: JSONEncoding.default)
        case .unBindDevice:
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
   
}

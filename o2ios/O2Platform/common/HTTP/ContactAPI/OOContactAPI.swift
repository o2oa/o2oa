//
//  OOContactAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/20.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

// MARK: - 所有调用的API
enum OOContactAPI {
    //所有顶层单元
    case listTop
    //所有顶层单元子单元
    case listSubDirect(String)
    //单元信息
    case getUnit(String)
    //个人信息(包括部门，群组等)
    case getPerson(String)
    //查找
    case iconByPerson(String)
    
    case unitLike(String)
    
    case groupLike(String)
    
    case personLike(String)
    
    case personListNext(String,Int)
    
}

// MARK: - 通讯录上下文
extension OOContactAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_organization_assemble_control"
    }
}


// MARK: - 是否需要加入x-token访问头
extension OOContactAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

// MARK: - 扩展API
extension OOContactAPI:TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_organization_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .getPerson(let flag):
            return "/jaxrs/person/\(flag.urlEscaped)"
        case .getUnit(let unitFlag):
            return "/jaxrs/unit/\(unitFlag.urlEscaped)"
        case .listSubDirect(let unitFlag):
            return "/jaxrs/unit/list/\(unitFlag.urlEscaped)/sub/direct"
        case .listTop:
            return "/jaxrs/unit/list/top"
        case .iconByPerson(let pid):
            return "/jaxrs/person/\(pid)/icon"
        case .unitLike(_):
            return "/jaxrs/unit/list/like"
        case .groupLike(_):
            return "/jaxrs/group/list/like"
        case .personLike(_):
            return "/jaxrs/person/list/like"
        case .personListNext(let flag, let count):
            return "jaxrs/person/list/\(flag)/next/\(count)"
        }
    }
    
    var method: Moya.Method {
        switch self{
        case .getPerson(_),.getUnit(_),.listTop,.listSubDirect(_),.iconByPerson(_),.personListNext(_, _):
            return .get
        case .unitLike(_),.groupLike(_),.personLike(_):
            return .put
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self{
        case .getPerson(_),.getUnit(_),.listTop,.listSubDirect(_),.personListNext(_,_):
            return .requestPlain
        case .iconByPerson(_):
            return .requestPlain
        case .groupLike(let searchText):
            return .requestParameters(parameters: ["key":searchText], encoding: JSONEncoding.default)
        case .unitLike(let searchText):
            return .requestParameters(parameters: ["key":searchText], encoding: JSONEncoding.default)
        case .personLike(let searchText):
            return .requestParameters(parameters: ["key":searchText], encoding: JSONEncoding.default)
        }
    }
    
    public var headers: [String : String]? {
        return nil
    }
    
    
}



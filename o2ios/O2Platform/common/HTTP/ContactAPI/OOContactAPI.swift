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
    
    //根据组织ID返回组织对象
    case unitList([String])
    //所有顶层单元
    case listTop
    //所有顶层单元子单元
    case listSubDirect(String)
    //根据组织类型查询组织，第一个参数是type 第二个参数是上级组织 可以多值
    case unitListByType(String, [String])
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
    
    case groupListNext(String, Int)
    //根据组织查询身份列表
    case identityListByUnit(String)
   
    
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
            return "/jaxrs/person/\(flag)"
        case .getUnit(let unitFlag):
            return "/jaxrs/unit/\(unitFlag)"
        case .listSubDirect(let unitFlag):
            return "/jaxrs/unit/list/\(unitFlag)/sub/direct"
        case .listTop:
            return "/jaxrs/unit/list/top"
        case .unitList(_):
            return "/jaxrs/unit/list"
        case .unitListByType(_,_):
            return "/jaxrs/unit/list/unit/type"
        case .iconByPerson(let pid):
            return "/jaxrs/person/\(pid)/icon"
        case .unitLike(_):
            return "/jaxrs/unit/list/like"
        case .groupLike(_):
            return "/jaxrs/group/list/like"
        case .personLike(_):
            return "/jaxrs/person/list/like"
        case .personListNext(let flag, let count):
            return "/jaxrs/person/list/\(flag)/next/\(count)"
        case .groupListNext(let flag, let count):
            return "/jaxrs/group/list/\(flag)/next/\(count)"
        case .identityListByUnit(let unit):
            return "/jaxrs/identity/list/unit/\(unit)"
       
        }
    }
    
    var method: Moya.Method {
        switch self{
        case .getPerson(_),.getUnit(_),.listTop,.listSubDirect(_),.iconByPerson(_),.personListNext(_, _),.groupListNext(_, _),.identityListByUnit(_):
            return .get
        case .unitLike(_),.groupLike(_),.personLike(_),.unitListByType(_, _):
            return .put
        case .unitList(_):
            return .post
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self{
        case .getPerson(_),.getUnit(_),.listTop,.listSubDirect(_),.personListNext(_,_),.groupListNext(_, _),.identityListByUnit(_):
            return .requestPlain
        case .iconByPerson(_):
            return .requestPlain
        case .groupLike(let searchText):
            return .requestParameters(parameters: ["key":searchText], encoding: JSONEncoding.default)
        case .unitLike(let searchText):
            return .requestParameters(parameters: ["key":searchText], encoding: JSONEncoding.default)
        case .unitListByType(let type, let parentList):
            return .requestParameters(parameters: ["type": type, "unitList": parentList], encoding: JSONEncoding.default)
        case .unitList(let idList):
            return .requestParameters(parameters: ["unitList": idList], encoding: JSONEncoding.default)
        case .personLike(let searchText):
            return .requestParameters(parameters: ["key":searchText], encoding: JSONEncoding.default)
       
        }
    }
    
    public var headers: [String : String]? {
        return nil
    }
    
    
}



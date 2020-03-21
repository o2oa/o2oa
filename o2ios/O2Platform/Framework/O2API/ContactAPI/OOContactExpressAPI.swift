//
//  OOContactExpressAPI.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/13.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Moya
import O2OA_Auth_SDK

//x_organization_assemble_express

enum OOContactExpressAPI {
    //根据职务列表和组织查询 组织下对应的身份列表
    case identityListByUnitAndDuty([String], String)
    //查询人员person的dn
    case personListDN([String])
}


extension OOContactExpressAPI: OOAPIContextCapable {
    var apiContextKey: String {
        return "x_organization_assemble_express"
    }
}

extension OOContactExpressAPI: OOAccessTokenAuthorizable {
    var shouldAuthorize: Bool {
        return true
    }
}

extension OOContactExpressAPI: TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_organization_assemble_express)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .identityListByUnitAndDuty(_, _):
            return "/jaxrs/unitduty/list/identity/unit/name/object"
        case .personListDN(_):
            return "/jaxrs/person/list"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .identityListByUnitAndDuty(_, _), .personListDN(_):
            return .post
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .identityListByUnitAndDuty(let dutyList, let unit):
            return .requestParameters(parameters: ["nameList": dutyList, "unit": unit], encoding: JSONEncoding.default)
        case .personListDN(let idList):
            return.requestParameters(parameters: ["personList": idList], encoding: JSONEncoding.default)
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}


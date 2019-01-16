//
//  HotpicAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/6.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

// MARK:- 所有调用的API枚举
enum OOHotpicAPI {
    case allHotpic(page:Int,count:Int)
    case allHotpicHome(CommonPageParameter)
    case hotpicImageSize(id:String,size:Int)
    case hotpicImageInfo(id:String)
}

// MARK:- 上下文实现
extension OOHotpicAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_hotpic_assemble_control"
    }
}


// MARK: - 是否需要加入x-token访问头
extension OOHotpicAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

// MARK: - 扩展API Moya实现
extension OOHotpicAPI:TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_hotpic_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .allHotpic(let page,let count):
            return "/jaxrs/user/hotpic/filter/list/page/\(page)/count/\(count)"
        case .allHotpicHome(let parameter):
            return "/jaxrs/user/hotpic/filter/list/page/\(parameter.currentPageNo)/count/\(parameter.countByPage)"
        case .hotpicImageInfo(let imageId):
            return "servlet/picture/\(imageId)"
        case .hotpicImageSize(let imageId, let size):
            return "/servlet/picture/\(imageId)/size/\(size)"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .allHotpicHome(_),.allHotpic(_,_):
            return .put
        default:
            return .get
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .allHotpic(_,_):
            return .requestPlain
        case .allHotpicHome(_):
            return .requestPlain
        case .hotpicImageInfo(_):
            return .requestPlain
        case .hotpicImageSize(_,_):
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}

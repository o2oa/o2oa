//
//  OOError.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/24.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya

public enum OOAppError:Swift.Error {
    case imageMapping(message:String,statusCode:Int,data:Data?)
    case jsonMapping(message:String,statusCode:Int,data:Data?)
    case stringMapping(message:String,statusCode:Int,data:Data?)
    case statusCode(message:String,statusCode:Int,data:Data?)
    case underlying(Swift.Error)
    case requestMapping(String)
    case common(type:String,message:String,statusCode:Int)
    case objectMapping(Swift.Error,Response)
    case encodableMapping(Swift.Error)
    case parameterEncoding(Swift.Error)
    case apiResponseError(String)
    case apiEmptyResultError
}

extension OOAppError:LocalizedError {
    
    public var errorDescription: String? {
        switch self {
        case .imageMapping(let msg, let statusCode, _):
            return "Moya.Error:imageMapping,msg=\(msg),statusCode=\(statusCode)"
        case .jsonMapping(let msg, let statusCode, _):
            return "Moya.Error:jsonMapping,msg=\(msg),statusCode=\(statusCode)"
        case .stringMapping(let msg, let statusCode, _):
            return "Moya.Error:stringMapping,msg=\(msg),statusCode=\(statusCode)"
        case .statusCode(let msg, let statusCode, _):
            return "Moya.Error:statusCode,msg=\(msg),statusCode=\(statusCode)"
        case .underlying(let err):
            return "Moya.Error:underlying,error=\(err)"
        case .requestMapping(let msg):
            return "Moya.Error:requestMapping,msg=\(msg)"
        case .common(let type, let msg, let statusCode):
            return "App.Error:type=\(type),msg=\(msg),statusCode=\(statusCode)"
        case .objectMapping(let err, let resp):
            return "Moya.Error:objectMapping,error=\(err),resp=\(resp)"
        case .encodableMapping(let err):
            return "Moya.Error:encodableMapping,error=\(err)"
        case .parameterEncoding(let err):
            return "Moay.Error:parameterEncoding,error=\(err)"
        case .apiResponseError(let err):
            return "API.Error:apiResponseError,error=\(err)"
        case .apiEmptyResultError:
            return "API 返回结果为空"
        }
    }
    
    public var failureReason: String? {
        switch self {
        case .imageMapping(let msg,_,_):
            return msg
        case .jsonMapping(let msg, _,_):
            return msg
        case .stringMapping(let msg,_,_):
            return msg
        case .statusCode(let msg,_,_):
            return msg
        case .underlying(let err):
            return err.localizedDescription
        case .requestMapping(let msg):
            return msg
        case .common(_,let msg,_):
            return msg
        case .objectMapping(let err, let resp):
            return "error=\(err.localizedDescription),resp=\(resp)"
        case .encodableMapping(let err):
            return err.localizedDescription
        case .parameterEncoding(let err):
            return err.localizedDescription
        case .apiResponseError(let err):
            return err
        case .apiEmptyResultError:
            return "API 返回结果为空"
        }
    }
    
}

public enum OOError:Swift.Error {
    case systemError(Swift.Error)
    case appError(OOAppError)
}

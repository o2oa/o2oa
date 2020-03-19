//
//  OOTaskAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/6.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

// MARK:- 所有调用的API枚举
enum OOTaskAPI {
    
    // MARK:- 应用
    case getApplication
    case listApplicationWithPersonComplex
    case listApplicationWithPersonLike
    
    // MARK:- Task
    case taskDataSave(String,Dictionary<String,AnyObject>)
    case taskSaveAndSubmit(String,Dictionary<String,AnyObject>)
    case taskSubmitNeural(String, Dictionary<String, String>)
    case taskList(OOTaskPageParameter)
    case taskListFilter(String,String)
    case taskSubmit(String)
    case taskGetAttachmentInfo(String,String)
    case taskDownloadAttachment(String,String)
    case taskUpReplaceAttachment(String,String)
    case taskUploadAttachment(String)
    case taskCreateAvaiableIdentityById(String)
    
    // MARK:- TaskCompleted
    case taskedList(String,String)
    case taskedListFilter(String,String)
    case taskedDataById(String)
    case taskedGetAttachmentInfo(String,String)
    case taskedDownloadAttachment(String,String)
    
    // MARK:- Read
    case readSubmit(String)
    case readProcessing(String)
    case readList(String,String)
    case readListFilter(String,String)
    
    // MARK:- ReadCompleted
    case readedList(String,String)
    case readedListFilter(String,String)

}

// MARK:- 上下文实现
extension OOTaskAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_processplatform_assemble_surface"
    }
}


// MARK: - 是否需要加入x-token访问头
extension OOTaskAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

// MARK: - Moya实现
extension OOTaskAPI:TargetType{
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_processplatform_assemble_surface)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .taskList(let parameter):
            return "/jaxrs/task/list/\(parameter.pageParameter?.currentPageId ?? "(0)")/next/\(parameter.pageParameter?.countByPage ?? 20)"
        case .taskDataSave(let id,_):
            return "/jaxrs/data/work/\(id)"
        case .taskSaveAndSubmit(let id,_):
            return "/jaxrs/task/\(id)/processing"
        case .taskSubmitNeural(let id, _):
            return "jaxrs/task/\(id)/processing/neural"
        case .taskListFilter(let id, let count):
            return "/jaxrs/task/list/\(id)/next/\(count)/filter"
        case .taskSubmit(let id):
            return "/jaxrs/task/\(id)"
        case .taskGetAttachmentInfo(let attachId,let workId):
            return "/jaxrs/attachment/\(attachId)/work/\(workId)"
        case .taskDownloadAttachment(let attachId, let workId):
            return "/jaxrs/attachment/download/\(attachId)/work/\(workId)/stream/true"
        case .taskUpReplaceAttachment(let attachId, let workId):
            return "/jaxrs/attachment/update/\(attachId)/work/\(workId)"
        case .taskUploadAttachment(let workId):
            return "/jaxrs/attachment/upload/work/\(workId)"
        case .taskCreateAvaiableIdentityById(let processId):
            return "/jaxrs/process/list/available/identity/process/\(processId)"
        case .readList(let id, let count):
            return "/jaxrs/read/list/\(id)/next/\(count)"
        case .readListFilter(let id, let count):
            return "/jaxrs/read/list/\(id)/next/\(count)/filter"
        case .taskedList(let id, let count):
            return "/jaxrs/taskcompleted/list/\(id)/next/\(count)"
        case .taskedListFilter(let id, let count):
            return "/jaxrs/taskcompleted/list/\(id)/next/\(count)/filter"
        case .taskedDataById(let id):
            return "/jaxrs/taskcompleted/\(id)/reference"
        case .taskedGetAttachmentInfo(let attachId,let workCompletedId):
            return "/jaxrs/attachment/\(attachId)/work/\(workCompletedId)"
        case .taskedDownloadAttachment(let attachId, let workCompletedId):
            return "/jaxrs/attachment/download/\(attachId)/workcompleted/\(workCompletedId)"
        
        default:
            return ""
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .taskDataSave(_, _):
            return .put
        case .taskSaveAndSubmit(_, _):
            return .post
        case .taskSubmitNeural(_,_):
            return .post
        default:
            return .get
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .taskDataSave(_, let param):
            return .requestParameters(parameters: param, encoding: JSONEncoding.default)
        case .taskSaveAndSubmit(_, let param):
            return .requestParameters(parameters: param, encoding: JSONEncoding.default)
        case .taskSubmitNeural(_, let param):
            return .requestParameters(parameters: param, encoding: JSONEncoding.default)
        default:
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}

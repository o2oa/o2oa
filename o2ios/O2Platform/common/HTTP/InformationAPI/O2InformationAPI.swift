//
//  O2InformationAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/27.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

//static let cmsCategoryQuery  = "jaxrs/appinfo/list/user/view"
//static let cmsAttachmentDownloadQuery = "servlet/download/##id##/stream"
//static let cmsCategoryDetailQuery = "jaxrs/document/filter/list/##id##/next/##count##"
//static let cmsAttachmentListQuery = "jaxrs/fileinfo/list/document/##documentId##"

// MARK:- 所有调用的API枚举
enum O2InformationAPI {
    case allCategoryList
    case categoryDetail(O2InformationCategoryListParameter)
    case categoryHomeDetail(O2InformationHomeParameter)
    case docAttachList(String)
    case docAttachDownload(O2CMSAttachmentInfo)
}

// MARK:- 上下文实现
extension O2InformationAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_cms_assemble_control"
    }
}


// MARK: - 是否需要加入x-token访问头
extension O2InformationAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

extension O2InformationAPI:TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_cms_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .allCategoryList:
            return "/jaxrs/appinfo/list/user/view"
        case .categoryDetail(let parameter):
            return "/jaxrs/document/filter/list/\(parameter.pageParameter?.currentPageNo ?? 0)/next/\(parameter.pageParameter?.countByPage ?? 20)"
        case .categoryHomeDetail(let parameter):
            return "/jaxrs/document/filter/list/\(parameter.pageParameter?.currentPageNo ?? 0)/next/\(parameter.pageParameter?.countByPage ?? 20)"
        case .docAttachList(let documentId):
            return "/jaxrs/fileinfo/list/document/\(documentId)"
        case .docAttachDownload(let attach):
            return "/jaxrs/fileinfo/download/document/\(attach.id!)/stream/APPLICATION_OCTET_STREAM"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .allCategoryList:
            return .get
        case .categoryDetail(_):
            return .put
        case .categoryHomeDetail(_):
            return .put
        case .docAttachList(_):
            return .get
        default:
            return .get
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .allCategoryList:
            return .requestPlain
        case .categoryDetail(let parameter):
            let param = parameter.getParamDict()
            return .requestParameters(parameters: param, encoding: JSONEncoding.default)
        case .categoryHomeDetail(_):
            return .requestPlain
        case .docAttachList(_):
            return .requestPlain
        case .docAttachDownload(_):
            return .downloadDestination(self.getDownDest()!)
        default:
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    func getDocAttachURL() -> NSURL? {
        switch self {
        case .docAttachDownload(let attachmentInfo):
            let fileName = attachmentInfo.name
             let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            let fileURL = documentsURL.appendingPathComponent("O2").appendingPathComponent("infor").appendingPathComponent(fileName!)
            return NSURL(string: fileURL.absoluteString)
        default:
            return nil
        }
    }
    
    func getDownDest() -> DownloadDestination? {
        switch self {
        case .docAttachDownload(let attachmentInfo):
            let myDest:DownloadDestination = { temporaryURL, response in
                let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
                let fileName = attachmentInfo.name
                let fileURL = documentsURL.appendingPathComponent("O2").appendingPathComponent("infor").appendingPathComponent(fileName!)
                return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
                
            }
            return myDest
        default:
            return nil
        }
        
    }
    
}




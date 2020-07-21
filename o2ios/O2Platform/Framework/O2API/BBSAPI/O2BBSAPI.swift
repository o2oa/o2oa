//
//  O2BBSAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/26.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK


//static let bbsContextKey = "x_bbs_assemble_control"
//static let getCategoryAndSectionQuery = "jaxrs/mobile/view/all" //所有分区及所有子板块
//static let getSectionItemQuery = "jaxrs/section/##id##" //板块详细信息列表
//static let sectionTopItemQuery = "jaxrs/subject/top/##id##" //板块内的置顶帖
//static let subjectByIdQuery = "jaxrs/subject/view/##id##" //获得具体帖子
//static let subjectFromSectionByPageQuery = "jaxrs/subject/filter/list/page/##pageNumber##/count/##pageSize##" //板块帖子分页查询
//static let uploadImageQuery = "servlet/upload/subject" //上传图片
//static let imageDisplayQuery = "servlet/download/subjectattachment/##id##/stream" //图片显示地址
//static let itemCreateQuery = "jaxrs/user/subject"//发帖
//static let itemReplyQuery = "jaxrs/user/reply" //回帖
//static let bbsSectionIconQuery = "servlet/section/##id##/icon"

// MARK:- 所有调用的API枚举
enum O2BBSAPI {
    case getCategoryAndSectionQuery
    case getSectionItemQuery(String)
    case sectionTopItemQuery(String)
    case subjectByIdQuery(String)
    case subjectFromSectionByPageQuery(SubjectsParameter)
    case createSubject
    case replySubject
    case getAttachment(String)
    case uploadAttachForSubject(String)
    case downloadAttachForSubject(O2BBSSubjectAttachmentInfo)
    case getSubjectAttachmentList(String)
}

// MARK:- 上下文实现
extension O2BBSAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_bbs_assemble_control"
    }
}


// MARK: - 是否需要加入x-token访问头
extension O2BBSAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}



extension O2BBSAPI:TargetType{
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_bbs_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .getCategoryAndSectionQuery:
            return "/jaxrs/mobile/view/all"
        case .getSectionItemQuery(let id):
            return "/jaxrs/section/\(id.urlEscaped)"
        case .sectionTopItemQuery(let id):
            return "/jaxrs/subject/top/\(id.urlEscaped)"
        case .subjectByIdQuery(let id):
            return "/jaxrs/subject/view/\(id.urlEscaped)"
        case .subjectFromSectionByPageQuery(let parameter):
            return "/jaxrs/subject/filter/list/page/\(parameter.pageParameter!.currentPageNo)/count/\(parameter.pageParameter!.countByPage)"
        case .createSubject:
            return "/jaxrs/user/subject"
        case .replySubject:
            return "/jaxrs/user/reply"
        case .getAttachment(let attId):
            return "/jaxrs/attachment/\(attId)"
        case .uploadAttachForSubject(let subjectId):
            return "/jaxrs/attachment/upload/subject/\(subjectId)"
        case .downloadAttachForSubject(let att):
            return "/jaxrs/attachment/download/\(att.id!)"
        case .getSubjectAttachmentList(let subjectId):
            return "/jaxrs/subjectattach/list/subject/\(subjectId)"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .getCategoryAndSectionQuery, .getSubjectAttachmentList(_), .getAttachment(_):
            return .get
        case .getSectionItemQuery(_):
            return .get
        case .sectionTopItemQuery(_):
            return .get
        case .subjectByIdQuery(_):
            return .get
        case .subjectFromSectionByPageQuery(_):
            return .put
        case .createSubject:
            return .post
        case .replySubject:
            return .post
        case .uploadAttachForSubject(_):
            return .post
        case .downloadAttachForSubject(_):
            return .get
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .downloadAttachForSubject(let attachment):
            let myDest = getDownDest(filename: attachment.fileName!)
            return .downloadDestination(myDest)
        case .uploadAttachForSubject(_):
            return .requestPlain
        case .subjectFromSectionByPageQuery(let parameter):
            let param = ["sectionId":parameter.sectionId!,"withTopSubject":parameter.withTopSubject!] as [String : Any]
            return .requestParameters(parameters: param, encoding:JSONEncoding.default)
        default:
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    func getDownDest(filename: String) -> DownloadDestination {
        let myDest:DownloadDestination = { temporaryURL, response in
            let fileURL = O2.cloudFileLocalFolder().appendingPathComponent(filename)
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
            
        }
        return myDest
    }
    
    
}







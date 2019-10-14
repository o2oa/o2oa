//
//  OOCloudStorageAPI.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/7.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK

// MARK:- 所有调用的API枚举
enum OOCloudStorageAPI {
    // 获取当前人员的顶层文件 - jaxrs/complex/top
    case listTop
    //获取当前人员顶层文件夹 - jaxrs/complex/folder/##id##
    case listFolder(String)
    //jaxrs/share/list
    case listMyShare
    //jaxrs/editor/list
    case listMyEditor
    //jaxrs/attachment/{id}
    case listMyShareByPerson(String)
    
    case listMyEditorByPerson(String)
    
    case getPicItemURL(String)
    
    case getAttachment(String)
    // jaxrs/attachment/{id}/download/stream
    case downloadAttachment(OOAttachment)
    //*
    case deleteAttachement(String)
    
    case renameAttachment(String)
    
    case uploadAttachment(String?)
    
}

// MARK:- 上下文实现
extension OOCloudStorageAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_file_assemble_control"
    }
}


// MARK: - 是否需要加入x-token访问头
extension OOCloudStorageAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

// MARK: - MoyaAPI实现
extension OOCloudStorageAPI:TargetType{
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_file_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .listTop:
            return "/jaxrs/complex/top"
        case .listFolder(let folderId):
            return "/jaxrs/complex/folder/\(folderId)"
        case .listMyShare:
            return "/jaxrs/share/list"
        case .listMyEditor:
            return "/jaxrs/editor/list"
        case .listMyShareByPerson(let personId):
            return "/jaxrs/attachment/list/share/\(personId)"
        case .listMyEditorByPerson(let personId):
            return "/jaxrs/attachment/list/editor/\(personId)"
        case .getAttachment(let attachmentId),.deleteAttachement(let attachmentId),.renameAttachment(let attachmentId):
            return "/jaxrs/attachment/\(attachmentId)"
        case .getPicItemURL(let id):
            return "\(self.baseURL.absoluteString)/jaxrs/file/\(id)/download/stream"
        case .downloadAttachment(let attachment):
            return "/jaxrs/attachment/\(attachment.id!)/download/stream"
        case .uploadAttachment(let folderId):
            if folderId == nil {
                return "jaxrs/attachment/upload"
            }else{
                return "jaxrs/attachment/upload/folder/\(folderId!)"
            }
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .listTop:
           return .get
        case .listFolder(_):
            return .get
        case .listMyShare:
            return .get
        case .listMyEditor:
            return .get
        case .listMyShareByPerson(_):
            return .get
        case .listMyEditorByPerson(_):
            return .get
        case .getAttachment(_):
            return .get
        case .getPicItemURL(_):
            return .get
        case .deleteAttachement(_):
            return .put
        case .renameAttachment(_):
            return .post
        case .downloadAttachment(_):
            return .post
        case .uploadAttachment(_):
            return .post
        }
    }
    
    var sampleData: Data {
        switch self {
        case .listTop:
            if let topFile = Bundle.main.path(forResource: "toplist", ofType: "json") {
                return FileManager.default.contents(atPath: topFile)!
            }else{
                return "".data(using: String.Encoding.utf8)!
            }
        case .listFolder(_):
            if let folderFile = Bundle.main.path(forResource: "folder", ofType: "json") {
                return FileManager.default.contents(atPath: folderFile)!
            }else{
                return "".data(using: String.Encoding.utf8)!
            }
        case .listMyShare:
            if let file = Bundle.main.path(forResource: "myshareEditorList", ofType: "json") {
                return FileManager.default.contents(atPath: file)!
            }else{
                return "".data(using: String.Encoding.utf8)!
            }
        case .listMyEditor:
            if let file = Bundle.main.path(forResource: "myshareEditorList", ofType: "json") {
                return FileManager.default.contents(atPath: file)!
            }else{
                return "".data(using: String.Encoding.utf8)!
            }
        case .listMyShareByPerson(_):
            if let file = Bundle.main.path(forResource: "myshareEditorByPerson", ofType: "json") {
                return FileManager.default.contents(atPath: file)!
            }else{
                return "".data(using: String.Encoding.utf8)!
            }
        case .listMyEditorByPerson(_):
            if let file = Bundle.main.path(forResource: "myshareEditorByPerson", ofType: "json") {
                return FileManager.default.contents(atPath: file)!
            }else{
                return "".data(using: String.Encoding.utf8)!
            }
        case .getAttachment(_):
            return "".data(using: String.Encoding.utf8)!
        case .getPicItemURL(_):
            return "".data(using: String.Encoding.utf8)!
        case .deleteAttachement(_):
            return "".data(using: String.Encoding.utf8)!
        case .renameAttachment(_):
            return "".data(using: String.Encoding.utf8)!
        case .downloadAttachment(_):
            return "".data(using: String.Encoding.utf8)!
        case .uploadAttachment(_):
            return "".data(using: String.Encoding.utf8)!
        }
    }
    
    var task: Task {
        switch self {
        case .listTop,.listFolder(_),.listMyEditorByPerson(_),.listMyShareByPerson(_),
             .listMyEditor,.listMyShare,.getAttachment(_),.deleteAttachement(_),.getPicItemURL(_):
            return .requestPlain
        case .downloadAttachment(let attachment):
//            let myDest:DownloadDestination = { temporaryURL, response in
//                let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
//                let fileName = "\(attachment.name!).\(attachment.`extension`!)"
//                let fileURL = documentsURL.appendingPathComponent("O2").appendingPathComponent(fileName)
//                return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
//            }
            let myDest = getDownDest(attachment)
            return .downloadDestination(myDest)
        case .renameAttachment(_):
            return .requestPlain
        case .uploadAttachment(_):
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    func getDownDest(_ attachment:OOAttachment) -> DownloadDestination {
        let myDest:DownloadDestination = { temporaryURL, response in
            let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            let fileName = "\(attachment.name!).\(attachment.`extension`!)"
            let fileURL = documentsURL.appendingPathComponent("O2").appendingPathComponent("cloud").appendingPathComponent(fileName)
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
            
        }
        return myDest
    }
    
}


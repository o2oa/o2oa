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
    //新版
    case listTop
    case listFolderTop
    case listByFolder(String)
    case listFolderByFolder(String)
    case createFolder(String, String)
    case getFile(String)
    // folderId, fileName , file
    case uploadFile(String, String, Data)
    //fileId, file
    case updateFile(String, OOAttachment)
    //folderId folder
    case updateFolder(String, OOFolder)
    case deleteFolder(String)
    case deleteFile(String)
    //分享
    case share(OOShareForm)
    //分类查询 分页 type: String, page: Int, count: Int
    case listTypeByPage(String, Int, Int)
    case downloadFile(OOAttachment)
    //fileType = attachment | folder
    case shareToMe(String)
    //fileType = attachment | folder
    case myShareList(String)
    case shareFileListWithFolderId(String, String)
    case shareFolderListWithFolderId(String, String)
    case shieldShare(String)
    case deleteMyShare(String)
    
    
    //老版
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
            return "/jaxrs/attachment2/list/top"
        case .listFolderTop:
            return "/jaxrs/folder2/list/top"
        case .listByFolder(let folderId):
            return "/jaxrs/attachment2/list/folder/\(folderId)"
        case .listFolderByFolder(let folderId):
            return "/jaxrs/folder2/list/\(folderId)"
        case .createFolder(_, _):
            return "/jaxrs/folder2"
        case .uploadFile(let folderId, _, _):
            return "/jaxrs/attachment2/upload/folder/\(folderId)"
        case .updateFile(let fileId, _), .deleteFile(let fileId):
            return "/jaxrs/attachment2/\(fileId)"
        case .getFile(let fileId):
            return "jaxrs/attachment2/\(fileId)"
        case .updateFolder(let folderId, _), .deleteFolder(let folderId):
            return "/jaxrs/folder2/\(folderId)"
        case .share(_):
            return "/jaxrs/share"
        case .listTypeByPage(_, let page, let count):
            return "/jaxrs/attachment2/list/type/\(page)/size/\(count)"
        case .downloadFile(let file):
            return "/jaxrs/attachment2/\(file.id!)/download/stream"
        case .shareToMe(let fileType):
            return "/jaxrs/share/list/to/me2/\(fileType)"
        case .myShareList(let fileType):
            return "/jaxrs/share/list/my2/member/\(fileType)"
        case .shareFolderListWithFolderId(let shareId, let folderId):
            return "/jaxrs/share/list/folder/share/\(shareId)/folder/\(folderId)/"
        case .shareFileListWithFolderId(let shareId, let folderId):
            return "/jaxrs/share/list/att/share/\(shareId)/folder/\(folderId)/"
        case .shieldShare(let shareId):
            return "/jaxrs/share/shield/\(shareId)"
        case .deleteMyShare(let shareId):
            return "/jaxrs/share/\(shareId)"
            
            
            
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
        case .listTop, .listFolderTop, .listByFolder(_), .listFolderByFolder(_), .downloadFile(_), .getFile(_):
           return .get
        case .listFolder(_), .shareToMe(_), .myShareList(_), .shareFileListWithFolderId(_, _), .shareFolderListWithFolderId(_, _), .shieldShare(_):
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
        case .deleteAttachement(_), .updateFolder(_, _), .updateFile(_, _):
            return .put
        case .uploadAttachment(_), .downloadAttachment(_),
            .renameAttachment(_), .createFolder(_, _), .uploadFile(_,_,_), .share(_),.listTypeByPage(_, _, _):
            return .post
        case .deleteFolder(_), .deleteFile(_), .deleteMyShare(_):
            return .delete
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .listFolderByFolder(_), .listByFolder(_), .listTop,.listFolderTop,.listFolder(_),.listMyEditorByPerson(_),.listMyShareByPerson(_),
             .listMyEditor,.listMyShare,.getAttachment(_),.deleteAttachement(_),.getPicItemURL(_), .deleteFolder(_), .deleteFile(_),
             .getFile(_), .shareFileListWithFolderId(_, _), .shareFolderListWithFolderId(_, _):
            return .requestPlain
        case .shareToMe(_):
            return .requestPlain
        case .myShareList(_), .deleteMyShare(_), .shieldShare(_):
            return .requestPlain
        case .downloadFile(let attachment):
            let myDest = getDownDest(attachment)
            return .downloadDestination(myDest)
        case .downloadAttachment(let attachment):
            let myDest = getDownDest(attachment)
            return .downloadDestination(myDest)
        case .renameAttachment(_):
            return .requestPlain
        case .uploadAttachment(_):
            return .requestPlain
            
        //新接口
        case .createFolder(let name, let superior):
            return .requestParameters(parameters: ["name":name, "superior": superior], encoding: JSONEncoding.default)
        case .uploadFile(_, let fileName, let data):
            //字符串类型 文件名
            let strData = fileName.data(using: .utf8)
            let fileNameData = MultipartFormData(provider: .data(strData!), name: "fileName")
            //文件类型
            let fileData = MultipartFormData(provider: .data(data), name: "file", fileName: fileName)
            return .uploadMultipart([fileData, fileNameData])
            
        case .updateFolder(_, let folder):
            return .requestParameters(parameters: folder.toJSON()!, encoding: JSONEncoding.default)
        case .updateFile(_, let file):
            return .requestParameters(parameters: file.toJSON()!, encoding: JSONEncoding.default)
        case .share(let form):
            return .requestParameters(parameters: form.toJSON()!, encoding: JSONEncoding.default)
        case .listTypeByPage(let type, _, _):
            return .requestParameters(parameters: ["fileType": type], encoding: JSONEncoding.default)
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    func getDownDest(_ attachment:OOAttachment) -> DownloadDestination {
        let myDest:DownloadDestination = { temporaryURL, response in
            let fileURL = O2CloudFileManager.shared.cloudFileLocalPath(file: attachment)
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
        }
        return myDest
    }
    
}


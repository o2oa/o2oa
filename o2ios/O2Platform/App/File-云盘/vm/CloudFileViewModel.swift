//
//  CloudFileViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/16.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Moya
import Promises
import CocoaLumberjack
import O2OA_Auth_SDK


protocol CloudFileCheckDelegate {
    func checkItem(_ item: DataModel)
}

protocol CloudFileCheckClickDelegate {
    func clickFolder(_ folder: OOFolder)
    func clickFile(_ file: OOAttachment)
}

class CloudFileViewModel: NSObject {
    override init() {
        super.init()
    }
    
    private let cFileAPI = OOMoyaProvider<OOCloudStorageAPI>()
    
    //获取图片地址 根据传入的大小进行比例缩放
    func scaleImageUrl(id: String) -> String {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_file_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        //固定200px
        let width = 200
        let height = 200
        return baseURLString + "/jaxrs/attachment2/\(id)/download/image/width/\(width)/height/\(height)"
    }
    //获取图片地址 原图
    func originImageUrl(id: String) -> String {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_file_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return baseURLString + "/jaxrs/attachment2/\(id)/download"
    }
    
    //分页查询分类列表
    func listTypeByPage(type: CloudFileType, page: Int, count:Int) -> Promise<[OOAttachment]> {
        return Promise { fulfill, reject in
            var typeString: String
            switch type {
            case .image:
                typeString = "image"
                break
            case .office:
                typeString = "office"
                break
            case .movie:
                typeString = "movie"
                break
            case .music:
                typeString = "music"
                break
            case .other:
                typeString = "other"
                break
            }
            self.cFileAPI.request(.listTypeByPage(typeString, page, count), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOAttachment]>>(result)
                if response.isResultSuccess() {
                    if let list = response.model?.data {
                        fulfill(list)
                    }else {
                         fulfill([])
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //移动操作
    func moveToFolder(folderList: [OOFolder], fileList: [OOAttachment], destFolder: OOFolder) -> Promise<Bool> {
        return Promise { fulfill, reject in
            var fileMove : [Promise<Bool>] = []
            folderList.forEach({ (folder) in
                folder.superior = destFolder.id!
                fileMove.append(self.updateFolder(folder: folder))
            })
            fileList.forEach({ (file) in
                file.folder = destFolder.id!
                fileMove.append(self.updateFile(file: file))
            })
            all(fileMove).then({ (results) in
                results.forEach({ (r) in
                    DDLogDebug("移动成功， \(r)")
                })
                fulfill(true)
            }).catch({ (error) in
                reject(error)
            })
        }
    }
    
    //分享操作
    func share(folderList: [OOFolder], fileList: [OOAttachment], users: [O2PersonPickerItem], orgs: [O2UnitPickerItem]) -> Promise<Bool> {
        return Promise { fulfill, reject in
            var fileShare : [Promise<Bool>] = []
            var userIds: [String] = []
            var orgIds: [String] = []
            users.forEach({ (person) in
                userIds.append(person.distinguishedName!)
            })
            orgs.forEach({ (unit) in
                orgIds.append(unit.distinguishedName!)
            })
            folderList.forEach({ (folder) in
                fileShare.append(self.share(id: folder.id!, users: userIds, orgs: orgIds))
            })
            fileList.forEach({ (file) in
                fileShare.append(self.share(id: file.id!, users: userIds, orgs: orgIds))
            })
            all(fileShare).then({ (results) in
                results.forEach({ (r) in
                    DDLogDebug("分享成功， \(r)")
                })
                fulfill(true)
            }).catch({ (error) in
                reject(error)
            })
        }
    }
    
    //删除我分享的
    func deleteShareList(shareList: [String]) -> Promise<Bool> {
        return Promise { fulfill, reject in
            var fileShare : [Promise<Bool>] = []
            shareList.forEach { (id) in
                fileShare.append(self.deleteShare(shareId: id))
            }
            all(fileShare).then { (results) in
                results.forEach({ (r) in
                    DDLogDebug("删除分享， \(r)")
                })
                fulfill(true)
            }.catch { (err) in
                reject(err)
            }
        }
    }
    
    //屏蔽给我的分享
    func shieldShareList(shareList: [String]) -> Promise<Bool> {
         return Promise { fulfill, reject in
                   var fileShare : [Promise<Bool>] = []
                   shareList.forEach { (id) in
                       fileShare.append(self.shieldShare(shareId: id))
                   }
                   all(fileShare).then { (results) in
                       results.forEach({ (r) in
                           DDLogDebug("屏蔽分享， \(r)")
                       })
                    fulfill(true)
                   }.catch { (err) in
                       reject(err)
                   }
               }
    }
    
    //删除选中的数据 包含文件夹和文件
    func deleteCheckedList(folderList: [OOFolder], fileList: [OOAttachment]) -> Promise<Bool> {
        return Promise { fulfill, reject in
            var fileDelete : [Promise<Bool>] = []
            folderList.forEach({ (folder) in
                fileDelete.append(self.deleteFolder(id: folder.id!))
            })
            fileList.forEach({ (file) in
                fileDelete.append(self.deleteFile(id: file.id!))
            })
            all(fileDelete).then({ (results) in
                results.forEach({ (r) in
                    DDLogDebug("删除成功， \(r)")
                })
                fulfill(true)
            }).catch({ (error) in
                reject(error)
            })
        }
    }
    
    //重命名文件夹
    func updateFolder(folder: OOFolder) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.updateFolder(folder.id!, folder), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                     DDLogDebug("重命名文件夹成功：\(id)")
                    }
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //重命名文件
    func updateFile(file: OOAttachment) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.updateFile(file.id!, file), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        DDLogDebug("重命名文件成功：\(id)")
                    }
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //获取分享给我的文件列表 包含文件夹和文件
    func loadShareToMeList(folderParentId: String, shareId: String) -> Promise<[DataModel]> {
        return Promise{ fulfill, reject in
            all(self.shareToMeFolderList(folderId: folderParentId, shareId: shareId), self.shareToMeFileList(folderId: folderParentId, shareId: shareId))
                .then { (result) in
                    var dataList: [DataModel] = []
                    let folderList = result.0
                    for folder in folderList {
                        dataList.append(folder)
                    }
                    let fileList = result.1
                    for file in fileList {
                        dataList.append(file)
                    }
                    fulfill(dataList)
                }.catch { (error) in
                    DDLogError(error.localizedDescription)
                    reject(error)
            }
        }
    }
    
    func loadMyShareList(folderParentId: String, shareId: String) -> Promise<[DataModel]> {
        return Promise{ fulfill, reject in
            all(self.myShareFolderList(folderId: folderParentId, shareId: shareId), self.myShareFileList(folderId: folderParentId, shareId: shareId))
                .then { (result) in
                    var dataList: [DataModel] = []
                    let folderList = result.0
                    for folder in folderList {
                        dataList.append(folder)
                    }
                    let fileList = result.1
                    for file in fileList {
                        dataList.append(file)
                    }
                    fulfill(dataList)
                }.catch { (error) in
                    DDLogError(error.localizedDescription)
                    reject(error)
            }
        }
    }
    
    //获取列表 包含文件夹和文件
    func loadCloudFileList(folderParentId: String) -> Promise<[DataModel]> {
        return Promise{ fulfill, reject in
            all(self.folderList(folderId: folderParentId), self.fileList(folderId: folderParentId))
                .then { (result) in
                    var dataList: [DataModel] = []
                    let folderList = result.0
                    DDLogInfo("文件夹：\(folderList.count)")
                    for folder in folderList {
                        dataList.append(folder)
                    }
                    let fileList = result.1
                    DDLogInfo("文件：\(fileList.count)")
                    for file in fileList {
                        dataList.append(file)
                    }
                    fulfill(dataList)
                }.catch { (error) in
                    DDLogError(error.localizedDescription)
                   reject(error)
            }
        }
    }
    
    //文件列表
    func fileList(folderId: String) -> Promise<[OOAttachment]> {
        if folderId.isBlank {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.listTop, completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOAttachment]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(result.error!)
                    }
                })
            }
        }else {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.listByFolder(folderId), completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOAttachment]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(response.error!)
                    }
                })
            }
        }
        
    }
    
    //文件夹列表
    func folderList(folderId: String) -> Promise<[OOFolder]> {
        if folderId.isBlank {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.listFolderTop, completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOFolder]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(response.error!)
                    }
                })
            }
        } else {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.listFolderByFolder(folderId), completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOFolder]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(response.error!)
                    }
                })
            }
        }
        
    }
    
    //创建文件夹
    func createFolder(name: String, superior: String = "") -> Promise<String> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.createFolder(name, superior), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        fulfill(id.id ?? "")
                    }else {
                        fulfill("")
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //上传文件
    func uploadFile(folderId: String, fileName: String, file: Data) -> Promise<String> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.uploadFile(folderId, fileName, file), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        fulfill(id.id ?? "")
                    }else {
                        fulfill("")
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    
    // MARK: - private func
    
    //分享给我的文件夹列表
    private func shareToMeFolderList(folderId: String, shareId: String ) -> Promise<[OOFolder]> {
        if folderId.isBlank {
            return self.shareToMeTopFolderList()
        } else {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.shareFolderListWithFolderId(shareId, folderId), completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOFolder]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(response.error!)
                    }
                })
            }
        }
    }
    
    //分享给我的顶层文件夹列表
    private func shareToMeTopFolderList() -> Promise<[OOFolder]> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.shareToMe("folder"), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOFolder]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //分享给我的文件列表
    func shareToMeFileList(folderId: String, shareId: String ) -> Promise<[OOAttachment]> {
        if folderId.isBlank {
            return self.shareToMeTopFileList()
        }else {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.shareFileListWithFolderId(shareId, folderId), completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOAttachment]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(response.error!)
                    }
                })
            }
        }
        
    }
    
    //分享给我的顶层文件列表
    private func shareToMeTopFileList() -> Promise<[OOAttachment]> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.shareToMe("attachment"), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOAttachment]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    
    //我分享的顶层文件夹列表
    private func myShareFolderList(folderId: String, shareId: String) -> Promise<[OOFolder]> {
        if folderId.isBlank {
            return self.myShareTopFolderList()
        } else {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.shareFolderListWithFolderId(shareId, folderId), completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOFolder]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(response.error!)
                    }
                })
            }
        }
    }
    
    //我分享的顶层文件夹列表
    private func myShareTopFolderList() -> Promise<[OOFolder]> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.myShareList("folder"), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOFolder]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //分享给我的文件列表
    func myShareFileList(folderId: String, shareId: String) -> Promise<[OOAttachment]> {
        if folderId.isBlank {
            return self.myShareTopFileList()
        }else {
            return Promise { fulfill, reject in
                self.cFileAPI.request(.shareFileListWithFolderId(shareId, folderId), completion: { (result) in
                    let response = OOResult<BaseModelClass<[OOAttachment]>>(result)
                    if response.isResultSuccess() {
                        if let data = response.model?.data {
                            fulfill(data)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    }else {
                        reject(response.error!)
                    }
                })
            }
        }
        
    }
    
    //我分享的顶层文件列表
    private func myShareTopFileList() -> Promise<[OOAttachment]> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.myShareList("attachment"), completion: { (result) in
                let response = OOResult<BaseModelClass<[OOAttachment]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //删除文件
    private func deleteFile(id: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.deleteFile(id), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        DDLogDebug("删除文件成功：\(id)")
                    }
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //删除文件夹
    private func deleteFolder(id: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.deleteFolder(id), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        DDLogDebug("删除文件夹成功：\(id)")
                    }
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //分享
    private func share(id: String, users: [String], orgs: [String]) -> Promise<Bool> {
        return Promise { fulfill, reject in
            let form = OOShareForm()
            form.fileId = id
            form.shareType = "member"
            form.shareUserList = users
            form.shareOrgList = orgs
            self.cFileAPI.request(.share(form), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        DDLogDebug("分享成功：\(id)")
                    }
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //删除分享
    private func deleteShare(shareId: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.deleteMyShare(shareId), completion: { result in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        DDLogDebug("删除分享成功：\(id)")
                    }
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //屏蔽分享给我的文件
    private func shieldShare(shareId: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.cFileAPI.request(.shieldShare(shareId), completion: { result in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    if let id = response.model?.data {
                        DDLogDebug("屏蔽分享给我的文件：\(id)")
                    }
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
}

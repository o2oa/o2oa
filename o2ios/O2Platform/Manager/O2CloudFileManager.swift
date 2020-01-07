//
//  O2CloudFileManager.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/30.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import Moya
import O2OA_Auth_SDK
import Promises
import CocoaLumberjack

class O2CloudFileManager {
    static let shared: O2CloudFileManager = {
       return O2CloudFileManager()
    }()
    
    private init() {
        
    }
    
    private let cloudFileApi = {
       return OOMoyaProvider<OOCloudStorageAPI>()
    }()
    
    
    // MARK: - 工具服务 获取url 本地文件夹路径等等
    //文件存储目录
    func cloudFileLocalFolder() -> URL {
        let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        return documentsURL
            .appendingPathComponent("O2")
            .appendingPathComponent("cloud")
    }
    //本地文件存储路径
    func cloudFileLocalPath(file: OOAttachment) -> URL {
        let fileName = "\(file.name!).\(file.`extension`!)"
        if let id = file.id {
            return self.cloudFileLocalFolder()
                .appendingPathComponent(id)
                .appendingPathComponent(fileName)
        }
        return self.cloudFileLocalFolder()
            .appendingPathComponent(fileName)
        
    }
    
    //获取图片地址 根据传入的大小进行比例缩放
    func scaleImageUrl(id: String, width: Int = 200, height: Int = 200) -> String {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_file_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return baseURLString + "/jaxrs/attachment2/\(id)/download/image/width/\(width)/height/\(height)"
    }
    
    //获取源文件下载地址
    func originFileUrl(id: String) -> String {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_file_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return baseURLString + "/jaxrs/attachment2/\(id)/download"
    }
    
    
    
    // MARK: - 文件获取相关操作
    
    func getFileUrl(fileId: String) -> Promise<URL> {
        return Promise { fulfill, reject in
            self.getFileURLFromDB(id: fileId).then({ (path) in
                fulfill(path)
            }).catch({ (error) in
                DDLogInfo("本地没有这个文件，去服务器获取一次")
                DDLogError(error.localizedDescription)
                self.getFileURLFromDownload(fileId: fileId).then({ (path) in
                    fulfill(path)
                }).catch({ (err) in
                    reject(err)
                })
            })
        }
    }
    
    //获取图片 先从本地文件查找 没找到再从网络下载
    func getImage(imageId: String) -> Promise<UIImage> {
        return Promise {fulfill, reject in
            self.getFileURLFromDB(id: imageId).then({ (path) in
                if let image = UIImage(contentsOfFile: path.path) {
                    fulfill(image)
                }else {
                    DDLogError("没有找到本地文件。。。。\(path.path)")
                    reject(O2DBError.EmptyResultError)
                }
            }).catch({ error in
                DDLogError(error.localizedDescription)
                self.getFileURLFromDownload(fileId: imageId).then({ (path)  in
                    if let image = UIImage(contentsOfFile: path.path) {
                        fulfill(image)
                    }else {
                        DDLogError("没有找到本地文件。。。。\(path.path)")
                        reject(O2DBError.EmptyResultError)
                    }
                }).catch({ (err) in
                    reject(err)
                })
            })
        }
    }
    
    // MARK: - private method
    
    //下载文件 存储本地数据 返回本地文件路径
    private func getFileURLFromDownload(fileId: String)  -> Promise<URL> {
        return Promise {fulfill, reject in
            //本地没有 去网络下载
            self.downdloadFile(id: fileId).then({ (file) -> Promise<O2CloudFileInfo> in
                return self.storageFile2DB(file: file)
            }).then({ (dbFile)  in
                if let filePath = dbFile.filePath, !filePath.isBlank {
                    DDLogDebug("查询到数据 文件路径：\(filePath)")
                    let url = self.cloudFileLocalFolder().appendingPathComponent(filePath)
                    fulfill(url)
                }else {
                    reject(O2DBError.EmptyResultError)
                }
            }).catch({error in
                reject(error)
            })
        }
    }
    
    
    //从数据库获取 本地文件路径
    private func getFileURLFromDB(id: String) -> Promise<URL> {
        return Promise { fulfill, reject in
            DBManager.shared.queryCloudFile(fileId: id).then({ (dbFile) in
                if let filePath = dbFile.filePath, !filePath.isBlank {
                    DDLogDebug("查询到数据 文件路径：\(filePath)")
                    let url = self.cloudFileLocalFolder().appendingPathComponent(filePath)
                    fulfill(url)
                }else {
                    reject(O2DBError.EmptyResultError)
                }
            }).catch({ (error) in
                reject(error)
            })
        }
    }
    
    //存储附件对象到数据库
    private func storageFile2DB(file: OOAttachment) -> Promise<O2CloudFileInfo> {
        return Promise { fulfill, reject in
            let info = O2CloudFileInfo()
            info.fileId = file.id!
            info.fileName = file.name!
            let fileName = "\(file.name!).\(file.`extension`!)"
            let path = "\(file.id!)/\(fileName)"
            DDLogDebug("保存数据库 path:\(path)")
            info.filePath = path
            info.fileExt = file.extension ?? ""
            DBManager.shared.insertCloudFile(info: info).then({ (result) in
                if result {
                    fulfill(info)
                }else {
                    reject(O2DBError.UnkownError)
                }
            }).catch({ (error) in
                DDLogError(error.localizedDescription)
                reject(error)
            })
        }
    }
    
    
    
    //网络下载附件
    private func downdloadFile(id: String) -> Promise<OOAttachment>{
        return Promise { fulfill, reject in
            self.cloudFileApi.request(.getFile(id)) { (result) in
                let response = OOResult<BaseModelClass<OOAttachment>>(result)
                if response.isResultSuccess() {
                    if let file = response.model?.data {
                        self.cloudFileApi.request(.downloadFile(file), completion: { (downloadResult) in
                            switch downloadResult {
                            case .success(_):
                                //下载文件成功 返回附件对象 需要附件的地方根据固定的文件位置去查找
                                fulfill(file)
                                break
                            case .failure(let err):
                                reject(err)
                                break
                            }
                        })
                    }else {
                        reject(O2APIError.o2ResponseError("没有查询到附件对象， id: \(id)"))
                    }
                }else {
                    reject(response.error!)
                }
            }
        }
    }
    
    
    
    
    
}

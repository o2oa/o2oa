//
//  CloudFileBaseVC.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import QuickLook
import CocoaLumberjack

class CloudFileBaseVC: UIViewController {
    
    //预览文件
    private lazy var previewVC: CloudFilePreviewController = {
        return CloudFilePreviewController()
    }()
    
    lazy var cFileVM: CloudFileViewModel = {
        return CloudFileViewModel()
    }()
    
    //选中的文件夹列表
    var checkedFolderList: [OOFolder] = []
    //选中的文件列表
    var checkedFileList: [OOAttachment] = []
    
    //重新加载数据 子类各自实现
    func loadListData() {
        
    }
    
    
    func renameOp() {
        DDLogDebug("重命名")
        let rename = Languager.standardLanguager().string(key: "Rename")
        if self.checkedFolderList.count > 0 {
            let folder = self.checkedFolderList.first!
            let name = folder.name ?? ""
            
            self.showPromptAlert(title: rename, message: "\(rename) \(name)", inputText: name) { (action, result) in
                if result.isBlank {
                    let msg = Languager.standardLanguager().string(key: "Empty name Error Message")
                    self.showError(title: msg)
                }else {
                    folder.name = result
                    self.cFileVM.updateFolder(folder: folder).then({ (result) in
                        self.loadListData()
                    }).catch({ (error) in
                        DDLogError(error.localizedDescription)
                        self.showError(title: error.localizedDescription)
                    })
                }
            }
        }else if self.checkedFileList.count > 0 {
            let file = self.checkedFileList.first!
            let name = file.name ?? ""
            self.showPromptAlert(title: rename, message: "\(rename) \(name)", inputText: name) { (action, result) in
                if result.isBlank {
                    let msg = Languager.standardLanguager().string(key: "Empty name Error Message")
                    self.showError(title: msg)
                }else {
                    file.name = result
                    self.cFileVM.updateFile(file: file).then({ (result) in
                        self.loadListData()
                    }).catch({ (error) in
                        DDLogError(error.localizedDescription)
                        self.showError(title: error.localizedDescription)
                    })
                }
            }
        }
    }
    
    func deleteOp() {
        DDLogDebug("删除")
        let totalCount = self.checkedFileList.count + self.checkedFolderList.count
        if totalCount > 0 {
            let alert = Languager.standardLanguager().string(key: "Alert")
            let msg = Languager.standardLanguager().string(key: "Delete Items Confirm Message")
            self.showDefaultConfirm(title: alert, message: msg) { (action) in
                self.cFileVM.deleteCheckedList(folderList: self.checkedFolderList, fileList: self.checkedFileList)
                    .then({ (result) in
                        if result {
                            self.loadListData()
                        }
                    }).catch({ (error) in
                        DDLogError(error.localizedDescription)
                        self.showError(title: error.localizedDescription)
                    })
            }
        }
    }
    
    func moveOp() {
        DDLogDebug("移动")
        let totalCount = self.checkedFileList.count + self.checkedFolderList.count
        if totalCount > 0 {
            if let vc = CloudFileMoveFolderController.chooseFolderVC({ (folder) in
                self.showLoading()
                self.cFileVM.moveToFolder(folderList: self.checkedFolderList, fileList: self.checkedFileList, destFolder: folder)
                    .then({ (result) in
                        DDLogInfo("移动成功，\(result)")
                        self.hideLoading()
                        if result {
                            self.loadListData()
                        }
                    }).catch { (error) in
                        DDLogError(error.localizedDescription)
                        self.hideLoading()
                }
            }){
                self.pushVC(vc)
            }
        }
    }
    
    func shareOp() {
        DDLogDebug("分享")
        let totalCount = self.checkedFileList.count + self.checkedFolderList.count
        if totalCount > 0 {
            self.showContactPicker(modes: [ContactPickerType.person, ContactPickerType.unit], callback: { result in
                var u: [O2PersonPickerItem] = []
                var d: [O2UnitPickerItem] = []
                if let users = result.users, users.count > 0 {
                    u = users
                }
                if let units = result.departments, units.count > 0 {
                    d = units
                }
                if u.count > 0 || d.count > 0 {
                    self.showLoading()
                    self.cFileVM.share(folderList: self.checkedFolderList, fileList: self.checkedFileList, users: u, orgs: d)
                        .then { result in
                            DDLogInfo("分享成功，\(result)")
                            self.hideLoading()
                            if result {
                                self.loadListData()
                            }
                        }.catch { (error) in
                            DDLogError(error.localizedDescription)
                            self.hideLoading()
                    }
                }
            })
        }
    }
    
    
    
    //点击查看文件
    func clickFile(file: OOAttachment)  {
        if let type = file.type {
            switch type {
            case "image":
                self.previewFile(fileId: file.id!)
                break
            case "office":
                self.previewFile(fileId: file.id!)
                break
            case "music":
                self.showMessage(msg: "音频类型还未支持！")
                break
            case "movie":
                self.showMessage(msg: "视频类型还未支持！")
                break
            case "other":
                self.previewFile(fileId: file.id!)
                break
            default:
                self.previewFile(fileId: file.id!)
                break
            }
        }else {
            self.previewFile(fileId: file.id!)
        }
    }
    
    func previewFile(fileId: String) {
        self.showLoading()
        O2CloudFileManager.shared
            .getFileUrl(fileId: fileId)
            .always {
                self.hideLoading()
            }
            .then { (path) in
                let currentURL = NSURL(fileURLWithPath: path.path)
                DDLogDebug(currentURL.description)
                DDLogDebug(path.path)
                if QLPreviewController.canPreview(currentURL) {
                    self.previewVC.currentFileURLS.removeAll()
                    self.previewVC.currentFileURLS.append(currentURL)
                    self.previewVC.reloadData()
                    self.pushVC(self.previewVC)
                }else {
                    self.showError(title: "当前文件类型不支持预览！")
                }
            }
            .catch { (error) in
                DDLogError(error.localizedDescription)
                self.showError(title: "获取文件异常！")
        }
    }
}

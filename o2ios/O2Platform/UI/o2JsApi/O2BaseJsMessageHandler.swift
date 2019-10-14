//
//  O2BaseJsMessageHandler.swift
//  O2Platform
//
//  Created by FancyLou on 2019/4/26.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import ObjectMapper
import CocoaLumberjack
import BSImagePicker
import Photos
import O2OA_Auth_SDK
import SwiftyJSON

class O2BaseJsMessageHandler: O2WKScriptMessageHandlerImplement {
    
    let viewController: BaseWebViewUIViewController
    
    init(viewController: BaseWebViewUIViewController) {
        self.viewController = viewController
    }
    
    
    func userController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let name = message.name
        switch name {
        case "o2mLog":
            if message.body is NSString {
                let log = message.body as! NSString
                DDLogDebug("console.log: \(log)")
            }else {
                DDLogDebug("console.log: unkown type \(message.body)")
            }
            break
        case "ReplyAction":
            DDLogDebug("回复 帖子 message.body = \(message.body)")
            let pId : String?
            if message.body is NSDictionary {
                let parentId:NSDictionary = message.body as! NSDictionary
                pId = parentId["body"] as? String
            }else if message.body is NSString {
                pId = String(message.body as! NSString)
            }else {
                pId = nil
            }
            self.viewController.performSegue(withIdentifier: "showReplyActionSegue", sender: pId)
            break
        case "openO2Work":
            DDLogDebug("打开工作界面。。。。。")
            let body = message.body
            if body is NSDictionary {
                let dic = body as! NSDictionary
                let work = dic["work"] as? String
                let workCompleted = dic["workCompleted"] as? String
                let title = dic["title"] as? String
                self.openWork(work: (work ?? ""), workCompleted: (workCompleted ?? ""), title: (title ?? ""))
            }else {
                DDLogError("message body 不是一个字典。。。。。。")
            }
            break
        case "openO2WorkSpace":
            DDLogDebug("打开工作列表。。。。。")
            if message.body is NSString {
                let type = message.body as! NSString
                self.openO2WorkSpace(type: String(type))
            }else {
                DDLogError("打开工作列表失败， type不存在！！！！！")
            }
            break
        case "openO2CmsApplication":
            DDLogDebug("打开cms栏目。。。。。")
            if message.body is NSString {
                let appId = message.body as! NSString
                self.openCmsApplication(appId: String(appId))
            }else if message.body is NSDictionary {
                let appBody = message.body as! NSDictionary
                if let appId = appBody["appId"] {
                    self.openCmsApplication(appId: (appId as! String))
                }
            }else {
                DDLogError("打开cms栏目失败， appId不存在！！！！！")
            }
            break
        case "openO2CmsDocument":
            DDLogDebug("打开cms 文档。。。。。")
            if message.body is NSDictionary {
                let appBody = message.body as! NSDictionary
                let docId = appBody["docId"] as? String
                let docTitle = appBody["docTitle"] as? String
                self.openCmsDocument(docId: (docId ?? "" ), docTitle: (docTitle ?? ""))
            }else {
                DDLogError("打开cms文档失败， 参数不存在！！！！！")
            }
            break
        case "openO2Meeting":
            DDLogDebug("打开会议管理。。。。。")
            self.openO2Meeting()
            break
        case "openO2Calendar":
            DDLogDebug("打开日程管理。。。。。")
            self.openO2Calendar()
            break
        case "openScan":
            self.openScan()
            break
        case "openO2Alert":
            if message.body is NSString {
                let msg = message.body as! NSString
                self.openO2Alert(message: String(msg))
            }
            break
        case "closeNativeWindow":
            DDLogDebug("关闭窗口！！！！")
            self.viewController.delegate?.closeUIViewWindow()
            break
        case "openDingtalk":
            self.openDingtalk()
            break
        case "actionBarLoaded":
            self.viewController.delegate?.actionBarLoaded(show: true)
            break
        case "uploadImage2FileStorage":
            DDLogDebug("这里进入了上传图片控件。。。。。。。。。。。。。。。")
            if message.body is NSString {
                let json = message.body as! NSString
                DDLogDebug("上传图片:\(json)")
                if let uploadImage = O2WebViewUploadImage.deserialize(from: String(json)) {
                    self.uploadImage(data: uploadImage)
                }else {
                    DDLogError("解析json失败")
                    self.viewController.showError(title: "参数不正确！")
                }
            }else {
                DDLogError("传入参数类型不正确！")
                self.viewController.showError(title: "参数不正确！")
            }
            break
        default:
            DDLogError("传入js变量名称不正确，name:\(name)")
            if message.body is NSString {
                let json = message.body as! NSString
                DDLogDebug("console.log: \(json)")
            }
            break
        }
        
        
    }
    
    
    private func openO2Alert(message: String) {
        DDLogDebug("O2 alert msg:\(message)")
        self.viewController.showSystemAlert(title: "", message: message) { (action) in
            DDLogDebug("O2 alert ok button clicked! ")
        }
    }
    
    
    private func openWork(work: String, workCompleted: String, title: String) {
        let storyBoard = UIStoryboard(name: "task", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
        let json = """
        {"work":"\(work)", "workCompleted":"\(workCompleted)", "title":"\(title)"}
        """
        DDLogDebug("openWork json: \(json)")
        let todo = TodoTask(JSONString: json)
        destVC.todoTask = todo
        destVC.backFlag = 3 //隐藏就行
        self.viewController.show(destVC, sender: nil)
    }
    
    // task taskCompleted read readCompleted
    private func openO2WorkSpace(type: String) {
        let storyBoard = UIStoryboard(name: "task", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "todoTask")
        let nsType = NSString(string: type).lowercased
        DDLogDebug("打开工作区， type：\(nsType)")
        if "taskcompleted" == nsType {
            AppConfigSettings.shared.taskIndex = 2
        }else if "read" == nsType {
            AppConfigSettings.shared.taskIndex = 1
        }else if "readcompleted" == nsType {
            AppConfigSettings.shared.taskIndex = 3
        }else {
            AppConfigSettings.shared.taskIndex = 0
        }
        self.viewController.show(destVC, sender: nil)
    }
    
    private func openCmsApplication(appId: String) {
        DDLogInfo("打开栏目， appId：\(appId)")
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsCategoryListQuery, parameter: ["##appId##": appId as AnyObject])
        self.viewController.showLoading(title: "Loading...")
        Alamofire.request(url!, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let categroyList = Mapper<CMSCategoryData>().map(JSONObject: val)
                if let count = categroyList?.data?.count {
                    if count > 0 {
                        let storyBoard = UIStoryboard(name: "information", bundle: nil)
                        let destVC = storyBoard.instantiateViewController(withIdentifier: "CMSCategoryListController") as! CMSCategoryListViewController
                        destVC.title = categroyList?.data?.first?.appName ?? ""
                        let d = CMSData(JSONString: "{\"id\":\"\"}")
                        d?.wrapOutCategoryList = categroyList?.data
                        destVC.cmsData = d
                        self.viewController.show(destVC, sender: nil)
                    }
                }
                self.viewController.hideLoading()
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.viewController.hideLoading()
            }
        }
        
    }
    
    private func openCmsDocument(docId: String, docTitle: String) {
        DDLogInfo("打开文档， docId：\(docId) , docTitle:\(docTitle)")
        let storyBoard = UIStoryboard(name: "information", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "CMSSubjectDetailVC") as! CMSItemDetailViewController
        let json = """
        {"title":"\(docTitle)", "id":"\(docId)"}
        """
        destVC.itemData =  CMSCategoryItemData(JSONString: json)
        self.viewController.show(destVC, sender: nil)
    }
    
    private func openO2Meeting() {
        let storyBoard = UIStoryboard(name: "meeting", bundle: nil)
        if let destVC = storyBoard.instantiateInitialViewController() {
            self.viewController.show(destVC, sender: nil)
        }else {
            DDLogError("会议 模块打开失败，没有找到vc")
        }
    }
    
    private func openO2Calendar() {
        let storyBoard = UIStoryboard(name: "calendar", bundle: nil)
        if let destVC = storyBoard.instantiateInitialViewController() {
            self.viewController.show(destVC, sender: nil)
        }else {
            DDLogError("calendar 模块打开失败，没有找到vc")
        }
    }
    
    private func openScan() {
        ScanHelper.openScan(vc: self.viewController)
    }
    
    private func openDingtalk() {
        UIApplication.shared.open(URL(string: "dingtalk://dingtalkclient/")!, options: [:]) { (result) in
            DDLogInfo("打开了钉钉。。。。\(result)")
        }
    }
    
    // 表单图片控件
    private func uploadImage(data: O2WebViewUploadImage) {
        if data.callback == nil || data.callback.isEmpty || data.reference == nil || data.reference.isEmpty
            || data.referencetype == nil || data.referencetype.isEmpty {
            self.viewController.showError(title: "参数传入为空，无法上传图片")
            return
        }
        data.scale = 800
        let chooseImage = FileBSImagePickerViewController()
        self.viewController.bs_presentImagePickerController(chooseImage, animated: true, select: nil, deselect: nil, cancel: nil, finish: { (arr) in
            let count = arr.count
            DDLogDebug("选择了照片数量：\(count)")
            if count > 0 {
                //获取照片
                let asset = arr[0]
                if asset.mediaType == .image {
                    let options = PHImageRequestOptions()
                    options.isSynchronous = true
                    options.deliveryMode = .fastFormat
                    options.resizeMode = .none
                    
                    PHImageManager.default().requestImageData(for: asset, options: options, resultHandler: { (imageData, result, imageOrientation, dict) in
                        DispatchQueue.main.async {
                            self.viewController.showLoading(title: "上传中...")
                        }
                        var newData = imageData
                        //处理图片旋转的问题
                        if imageOrientation != UIImage.Orientation.up && imageData != nil {
                            let newImage = UIImage(data: imageData!)?.fixOrientation()
                            if newImage != nil {
                                newData = newImage?.pngData()
                            }
                        }
                        let fileUploadURL = AppDelegate.o2Collect
                            .generateURLWithAppContextKey(
                                FileContext.fileContextKey,
                                query: FileContext.fileUploadReference,
                                parameter: [
                                    "##referencetype##": data.referencetype as AnyObject,
                                    "##reference##": data.reference as AnyObject,
                                    "##scale##": String(data.scale) as AnyObject
                                ],
                                coverted: true)!
                        DDLogDebug(fileUploadURL)
                        let headers:HTTPHeaders = ["x-token":(O2AuthSDK.shared.myInfo()?.token!)!]
                        let fileURL = dict?["PHImageFileURLKey"] as! URL
                        
                        DispatchQueue.global(qos: .userInitiated).async {
                            Alamofire.upload(multipartFormData: { (mData) in
                                mData.append(newData!, withName: "file", fileName: fileURL.lastPathComponent, mimeType: "image/png")
                            }, to: fileUploadURL, method: .put, headers: headers, encodingCompletion: { (encodingResult) in
                                switch encodingResult {
                                case .success(let upload, _, _):
                                    
                                    upload.responseJSON {
                                        respJSON in
                                        switch respJSON.result {
                                        case .success(let val):
                                            let attachId = JSON(val)["data"]["id"].string!
                                            data.fileId = attachId
                                            let callback = data.callback!
                                            let callbackParameterJson = data.toJSONString()
                                            if callbackParameterJson != nil {
                                                DDLogDebug("json:\(callbackParameterJson!)")
                                                DispatchQueue.main.async {
                                                    let callJS = "\(callback)('\(callbackParameterJson!)')"
                                                    DDLogDebug("执行js：\(callJS)")
                                                    self.viewController.webView.evaluateJavaScript(callJS, completionHandler: { (result, err) in
                                                        self.viewController.showSuccess(title: "上传成功")
                                                    })
                                                }
                                            }
                                            
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                self.viewController.showError(title: "上传图片失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        self.viewController.showError(title: "上传图片失败")
                                    }
                                }
                                
                            })
                        }
                    })
                    
                }else {
                    DDLogError("选择类型不正确，不是照片")
                }
                
            }
        }, completion: nil)
    }


}

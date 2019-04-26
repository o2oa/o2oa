//
//  BaseTaskWebViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/13.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import AlamofireObjectMapper
import SwiftyJSON
import QuickLook
import ObjectMapper
import BSImagePicker
import Photos
import CocoaLumberjack
import JHTAlertController
import O2OA_Auth_SDK

enum TaskAttachmentOperationType {
    case upload(String)
    case download(String,String)
    case replace(String)
}

protocol O2WorkFormLoadedDelegate {
    func workFormLoaded()
}

class BaseTaskWebViewController: UIViewController {
    
    var qlController = TaskAttachmentPreviewController()
    
    //是否是已办
    open var isWorkCompeleted:Bool = false
    
    open var workId:String?
    
    var loadedDelegate: O2WorkFormLoadedDelegate?
    
    var webView:WKWebView!
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        qlController.dataSource = qlController
        qlController.delegate = qlController
        
        // Do any additional setup after loading the view.
    }
    
    deinit {
        
    }
    
    override open func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    open func theWebView(){
        setupWebView()
    }
    
    public func setupWebView() {
        let userContentController = WKUserContentController()
        //cookie脚本
        if let cookies = HTTPCookieStorage.shared.cookies {
            let script = getJSCookiesString(cookies: cookies)
            let cookieScript = WKUserScript(source: script, injectionTime: WKUserScriptInjectionTime.atDocumentStart, forMainFrameOnly: false)
            userContentController.addUserScript(cookieScript)
        }
        let webViewConfig = WKWebViewConfiguration()
        webViewConfig.userContentController = userContentController
        //加入js-app message appFormLoaded
        userContentController.add(self, name: "appFormLoaded")
        userContentController.add(self, name: "uploadAttachment")
        userContentController.add(self, name: "downloadAttachment")
        userContentController.add(self, name: "replaceAttachment")
        userContentController.add(self, name: "openO2Alert")
        userContentController.add(self, name: "openDocument")
        userContentController.add(self, name: "uploadImage2FileStorage")
        userContentController.add(self, name: "o2mNotificationAlert")
        userContentController.add(self, name: "o2mLog")
        userContentController.add(O2JsApiNotification(), name: "o2mNotification")
        self.webView = WKWebView(frame: self.view.frame, configuration: webViewConfig)
        
    }
    
    
    //上传附件
    open  func uploadAttachment(_ site:String){
        //选择附件上传
        let updloadURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskUploadAttachmentQuery, parameter: ["##workId##":workId as AnyObject])
        self.uploadAttachment(site, uploadURL: updloadURL!)
        //回调layout.appForm.uploadedAttachment(site, attachmentId)
    }
    
    
    private func uploadAttachment(_ site:String,uploadURL url:String){
        let vc = FileBSImagePickerViewController()
        bs_presentImagePickerController(vc, animated: true,
                                        select: { (asset: PHAsset) -> Void in
                                            // User selected an asset.
                                            // Do something with it, start upload perhaps?
        }, deselect: { (asset: PHAsset) -> Void in
            // User deselected an assets.
            // Do something, cancel upload?
        }, cancel: { (assets: [PHAsset]) -> Void in
            // User cancelled. And this where the assets currently selected.
        }, finish: { (assets: [PHAsset]) -> Void in
            for asset in assets {
                switch asset.mediaType {
                case .audio:
                    DDLogDebug("Audio")
                case .image:
                    let options = PHImageRequestOptions()
                    options.isSynchronous = true
                    options.deliveryMode = .fastFormat
                    options.resizeMode = .none
                    PHImageManager.default().requestImageData(for: asset, options: options, resultHandler: { (imageData, result, imageOrientation, dict) in
                        //DDLogDebug("result = \(result) imageOrientation = \(imageOrientation) \(dict)")
                        let fileURL = dict?["PHImageFileURLKey"] as! URL
                        DispatchQueue.main.async {
                            self.showMessage(title: "上传中...")
                        }
                        DispatchQueue.global(qos: .userInitiated).async {
                            Alamofire.upload(multipartFormData: { (mData) in
                                //mData.append(fileURL, withName: "file")
                                mData.append(imageData!, withName: "file", fileName: fileURL.lastPathComponent, mimeType: "application/octet-stream")
                                let siteData = site.data(using: String.Encoding.utf8, allowLossyConversion: false)
                                mData.append(siteData!, withName: "site")
                            }, to: url, encodingCompletion: { (encodingResult) in
                                switch encodingResult {
                                case .success(let upload, _, _):
                                    debugPrint(upload)
                                    upload.responseJSON {
                                        respJSON in
                                        switch respJSON.result {
                                        case .success(let val):
                                            let attachId = JSON(val)["data"]["id"].string!
                                            DispatchQueue.main.async {
                                                //ProgressHUD.showSuccess("上传成功")
                                                let callJS = "layout.appForm.uploadedAttachment(\"\(site)\", \"\(attachId)\")"
                                                self.webView.evaluateJavaScript(callJS, completionHandler: { (result, err) in
                                                    self.showSuccess(title: "上传成功")
                                                })
                                            }
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                self.showError(title: "上传失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        self.showError(title: "上传失败")
                                    }
                                }
                                
                            })
                        }
                    })
                case .video:
                    let options = PHVideoRequestOptions()
                    options.deliveryMode = .fastFormat
                    options.isNetworkAccessAllowed = true
                    options.progressHandler = { (progress,err, stop,dict) in
                        DDLogDebug("progress = \(progress) dict  = \(dict)")
                    }
                    PHImageManager.default().requestAVAsset(forVideo: asset, options: options, resultHandler: { (avAsset, avAudioMx, dict) in
                        
                    })
                case .unknown:
                    DDLogDebug("Unknown")
                    
                }
            }
        }, completion: nil)
    }
    
    //下载预览附件
    open func downloadAttachment(_ attachmentId:String){
        //生成两个URL，一个获取附件信息，一个链接正式下载
        var infoURL:String?,downURL:String?
        if isWorkCompeleted {
            infoURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskedContext.taskedContextKey, query: TaskedContext.taskedGetAttachmentInfoQuery, parameter: ["##attachmentId##":attachmentId as AnyObject,"##workcompletedId##":workId as AnyObject])
            downURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskedContext.taskedContextKey, query: TaskedContext.taskedGetAttachmentQuery, parameter:["##attachmentId##":attachmentId as AnyObject,"##workcompletedId##":workId as AnyObject])
        }else{
            infoURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskGetAttachmentInfoQuery, parameter: ["##attachmentId##":attachmentId as AnyObject,"##workId##":workId as AnyObject])
            downURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskGetAttachmentQuery, parameter:["##attachmentId##":attachmentId as AnyObject,"##workId##":workId as AnyObject])
        }
        self.showAttachViewInController(infoURL!, downURL!)
    }
    
    //替换附件
    open func replaceAttachment(_ attachmentId:String, _ site:String){
        //替换结束后回调js名称
        let replaceURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskUpReplaceAttachmentQuery, parameter: ["##attachmentId##":attachmentId as AnyObject,"##workId##":workId as AnyObject])!
        self.replaceAttachment(site, attachmentId, replaceURL: replaceURL)
        //layout.appForm.replacedAttachment(site , attachmentId)
    }
    
    
    /**
     * 下载公文 并阅览
     **/
    open func downloadDocumentAndPreview(_ url: String) {
        DDLogDebug("文档下载地址：\(url)")
        self.showMessage(title: "下载中...")
        // 文件地址
        let localFileDestination: DownloadRequest.DownloadFileDestination = { _, response in
            let documentsURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
            let fileURL = documentsURL.appendingPathComponent(response.suggestedFilename!)
            // 有重名文件就删除重建
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
        }
        Alamofire.download(url, to: localFileDestination).response(completionHandler: { (response) in
            if response.error == nil , let fileurl = response.destinationURL?.path {
                DDLogDebug("文件地址：\(fileurl)")
                //打开文件
                self.dismissProgressHUD()
                self.previewAttachment(fileurl)
            }else{
                let msg = response.error?.localizedDescription ?? ""
                DDLogError("下载文件出错，\(msg)")
                DispatchQueue.main.async {
                    self.showError(title: "预览文件出错")
                }
            }
        })
    }
    
    private func replaceAttachment(_ site:String,_ attachmentId:String,replaceURL url:String){
        let vc = FileBSImagePickerViewController()
        bs_presentImagePickerController(vc, animated: true,
                                        select: { (asset: PHAsset) -> Void in
                                            // User selected an asset.
                                            // Do something with it, start upload perhaps?
        }, deselect: { (asset: PHAsset) -> Void in
            // User deselected an assets.
            // Do something, cancel upload?
        }, cancel: { (assets: [PHAsset]) -> Void in
            // User cancelled. And this where the assets currently selected.
        }, finish: { (assets: [PHAsset]) -> Void in
            for asset in assets {
                switch asset.mediaType {
                case .audio:
                    DDLogDebug("Audio")
                case .image:
                    let options = PHImageRequestOptions()
                    options.isSynchronous = true
                    options.deliveryMode = .fastFormat
                    options.resizeMode = .none
                    PHImageManager.default().requestImageData(for: asset, options: options, resultHandler: { (imageData, result, imageOrientation, dict) in
                        //DDLogDebug("result = \(result) imageOrientation = \(imageOrientation) \(dict)")
                        let fileURL = dict?["PHImageFileURLKey"] as! URL
                        DispatchQueue.main.async {
                            self.showMessage(title: "上传中...")
                        }
                        DispatchQueue.global(qos: .userInitiated).async {
                            Alamofire.upload(multipartFormData: { (mData) in
                                //mData.append(fileURL, withName: "file")
                                mData.append(imageData!, withName: "file", fileName: fileURL.lastPathComponent, mimeType: "application/octet-stream")
                                let siteData = site.data(using: String.Encoding.utf8, allowLossyConversion: false)
                                mData.append(siteData!, withName: "site")
                            }, usingThreshold: SessionManager.multipartFormDataEncodingMemoryThreshold, to: url, method: .put, headers: nil, encodingCompletion: { (encodingResult) in
                                switch encodingResult {
                                case .success(let upload, _, _):
                                    debugPrint(upload)
                                    upload.responseJSON {
                                        respJSON in
                                        switch respJSON.result {
                                        case .success( _):
                                            //let attachId = JSON(val)["data"]["id"].string!
                                            DispatchQueue.main.async {
                                                //ProgressHUD.showSuccess("上传成功")
                                                let callJS = "layout.appForm.replacedAttachment(\"\(site)\", \"\(attachmentId)\")"
                                                self.webView.evaluateJavaScript(callJS, completionHandler: { (result, err) in
                                                    self.showSuccess(title: "替换成功")
                                                })
                                            }
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                self.showError(title: "替换失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        self.showError(title: "替换失败")
                                    }
                                }
                                
                            })
                        }
                    })
                case .video:
                    let options = PHVideoRequestOptions()
                    options.deliveryMode = .fastFormat
                    options.isNetworkAccessAllowed = true
                    options.progressHandler = { (progress,err, stop,dict) in
                        DDLogDebug("progress = \(progress) dict  = \(String(describing: dict))")
                    }
                    PHImageManager.default().requestAVAsset(forVideo: asset, options: options, resultHandler: { (avAsset, avAudioMx, dict) in
                        
                    })
                case .unknown:
                    DDLogDebug("Unknown")
                    
                }
            }
        }, completion: nil)
    }
    
    fileprivate func showAttachViewInController(_ infoURL:String,_ downURL:String){
        self.showMessage(title: "下载中...")
        Alamofire.request(infoURL).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                //DDLogDebug(JSON(val).description)
                let info = Mapper<O2TaskAttachmentInfo>().map(JSONString: JSON(val).description)
                //执行下载
                let destination: DownloadRequest.DownloadFileDestination = { _, _ in
                    let documentsURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
                    
                    let fileURL = documentsURL.appendingPathComponent((info?.data?.name)!)
                    
                    return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
                }
                Alamofire.download(downURL, to: destination).response(completionHandler: { (response) in
                    if response.error == nil , let fileurl = response.destinationURL?.path {
                        //打开文件
                        self.dismissProgressHUD()
                        self.previewAttachment(fileurl)
                    }else{
                        DispatchQueue.main.async {
                            self.showError(title: "预览文件出错")
                        }
                    }
                 })
                break
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    self.showError(title: "预览文件出错")
                }
                break
            }
        }
    }
    
    
    
    private func previewAttachment(_ url:String){
        let currentURL = NSURL(fileURLWithPath: url)
        if QLPreviewController.canPreview(currentURL) {
            qlController.currentFileURLS.removeAll(keepingCapacity: true)
            qlController.currentFileURLS.append(currentURL)
            qlController.reloadData()
            if #available(iOS 10, *) {
                let navVC = ZLNormalNavViewController(rootViewController: qlController)
                qlController.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: qlController, action: #selector(qlController.qlCloseWindow))
                self.presentVC(navVC)
            }else{
                //if #available(iOS 9, *){
                self.pushVC(qlController)
                //}
            }
            
            
        }else{
            self.showError(title: "此文件无法预览，请在PC端查看")
        }

    }
    
    private func openO2Alert(message: String) {
        DDLogDebug("O2 alert msg:\(message)")
        let alertController = JHTAlertController(title: "", message: message, preferredStyle: .alert)
        alertController.titleImage = #imageLiteral(resourceName: "logo80-bai")
        alertController.messageTextColor = UIColor(hex: "#030303")
        alertController.titleViewBackgroundColor = UIColor.hexInt(0xFB4747)
        alertController.alertBackgroundColor =  UIColor(hexString: "#FCFCFC", alpha: 0.9)!
        alertController.setAllButtonBackgroundColors(to: UIColor(hexString: "#FCFCFC", alpha: 0.9)!)
        alertController.setButtonTextColorFor(.default, to: UIColor(hex: "#FB4747"))
        alertController.setButtonTextColorFor(.cancel, to: UIColor(hex: "#FB4747"))
        alertController.hasRoundedCorners = true
        let okAction = JHTAlertAction(title: "确定", style: .default, handler: {action in
            
        })
        alertController.addActions([okAction])
        present(alertController, animated: true, completion: nil)
    }

    
    private func downloadFile(_ attachmentId:String,_ completed:@escaping (_ localURLForFile:String) -> Void){

    }
   
    
    ///Generates script to create given cookies
    public func getJSCookiesString(cookies: [HTTPCookie]) -> String {
        var result = ""
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = NSTimeZone(abbreviation: "UTC") as TimeZone!
        dateFormatter.dateFormat = "EEE, d MMM yyyy HH:mm:ss zzz"
        
        for cookie in cookies {
            result += "document.cookie='\(cookie.name)=\(cookie.value); domain=\(cookie.domain); path=\(cookie.path); "
            if let date = cookie.expiresDate {
                result += "expires=\(dateFormatter.string(from: date)); "
            }
            if (cookie.isSecure) {
                result += "secure; "
            }
            result += "'; "
        }
        return result
    }
    
    
    // 表单图片控件
    private func uploadImage(data: O2WebViewUploadImage) {
        if data.callback == nil || data.callback.isEmpty || data.reference == nil || data.reference.isEmpty
        || data.referencetype == nil || data.referencetype.isEmpty {
            self.showError(title: "参数传入为空，无法上传图片")
            return
        }
        data.scale = 800
        let chooseImage = FileBSImagePickerViewController()
        self.bs_presentImagePickerController(chooseImage, animated: true, select: nil, deselect: nil, cancel: nil, finish: { (arr) in
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
                            self.showMessage(title: "上传中...")
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
                                                    self.webView.evaluateJavaScript(callJS, completionHandler: { (result, err) in
                                                        self.showSuccess(title: "上传成功")
                                                    })
                                                }
                                            }
                                            
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                self.showError(title: "上传图片失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        self.showError(title: "上传图片失败")
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

extension BaseTaskWebViewController:WKScriptMessageHandler{
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let name = message.name
        switch name {
        case "appFormLoaded":
            self.loadedDelegate?.workFormLoaded()
            break
        case "uploadAttachment":
            ZonePermissions.requestImagePickerAuthorization(callback: { (zoneStatus) in
                if zoneStatus == ZoneAuthorizationStatus.zAuthorizationStatusAuthorized {
                    let site = (message.body as! NSDictionary)["site"]
                    self.uploadAttachment(site as! String)
                }else {
                    //显示
                    let alertController = UIAlertController(title: "上传提示", message: "请设置照片允许访问权限", preferredStyle: UIAlertController.Style.alert)
                    alertController.addAction(UIAlertAction(title: "去设置", style: .destructive, handler: { (action) in
                        let setURL = URL(string: UIApplication.openSettingsURLString)!
                        if UIApplication.shared.canOpenURL(setURL) {
                            UIApplication.shared.openURL(setURL)
                        }
                    }))
                    alertController.addAction(UIAlertAction(title: "取消", style: .cancel, handler: { (action) in
                    
                    }))
                    self.presentVC(alertController)
                }
            })
            break
        case "downloadAttachment":
            let attachmentId = (message.body as! NSDictionary)["id"]
            self.downloadAttachment(attachmentId as! String)
            break
        case "replaceAttachment":
            let attachmentId = (message.body as! NSDictionary)["id"] as! String
            let site = (message.body as! NSDictionary)["site"] as? String
            self.replaceAttachment(attachmentId, site ?? "")
            break
        case "openDocument":
            let url = (message.body as! NSString)
            self.downloadDocumentAndPreview(String(url))
            break
        case "openO2Alert":
            if message.body is NSString {
                let msg = message.body as! NSString
                self.openO2Alert(message: String(msg))
            }
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
                    self.showError(title: "参数不正确！")
                }
            }else {
                DDLogError("传入参数类型不正确！")
                self.showError(title: "参数不正确！")
            }
            break
        case "o2mNotificationAlert":
            if message.body is NSString {
                let json = message.body as! NSString
                DDLogDebug("alert:\(json)")
                if let alert = O2NotificationMessage<O2NotificationAlertMessage>.deserialize(from: String(json)) {
                    var buttonName = alert.data?.buttonName ?? ""
                    if buttonName == "" {
                        buttonName = "确定"
                    }
                    let title = alert.data?.title ?? ""
                    let message = alert.data?.message ?? "消息"
                    self.showSystemAlertWithButtonName(title: title, message: message , buttonName: buttonName) { (action) in
                        if alert.callback != nil {
                            let callJs = "\(alert.callback!)()"
                            DDLogDebug(callJs)
                            self.webView.evaluateJavaScript(callJs, completionHandler: { (result, err) in
                                
                            })
                        }
                    }
                }else {
                    DDLogError("解析json失败")
                    self.showError(title: "参数不正确！")
                }
            }
           
            break
        case "o2mLog":
            if message.body is NSString {
                let log = message.body as! NSString
                DDLogDebug("console.log: "+String(log))
            }
            break;
        default:
            DDLogError("未知方法名：\(name)！")
            break
            
        }

    }
}

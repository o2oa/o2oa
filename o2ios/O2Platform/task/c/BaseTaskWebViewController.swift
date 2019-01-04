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
                            ProgressHUD.show("上传中...", interaction: false)
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
                                                    ProgressHUD.showSuccess("上传成功")
                                                })
                                            }
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                ProgressHUD.showError("上传失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        ProgressHUD.showError("上传失败")
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
                            ProgressHUD.show("上传中...", interaction: false)
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
                                                    ProgressHUD.showSuccess("替换成功")
                                                })
                                            }
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                ProgressHUD.showError("替换失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        ProgressHUD.showError("替换失败")
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
        ProgressHUD.show("下载中...", interaction: false)
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
                        ProgressHUD.dismiss()
                        self.previewAttachment(fileurl)
                    }else{
                        DispatchQueue.main.async {
                            ProgressHUD.showError("预览文件出错")
                        }
                    }
                 })
                break
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    ProgressHUD.showError("预览文件出错")
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
            ProgressHUD.showError("此文件无法预览，请在PC端查看")
        }

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
        default: break
            
        }

    }
}

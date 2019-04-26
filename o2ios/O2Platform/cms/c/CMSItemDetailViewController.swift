//
//  CMSItemDetailViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/9.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import QuickLook
import CocoaLumberjack

class CMSItemDetailViewController: BaseWebViewUIViewController {
    
    
    private let qlController = QLPreviewController()
    
    private var zonePickerView:ZonePickerView!
    
    
    fileprivate var currentFileURLS:[NSURL] = []
    
    private var window:UIWindow?
    
    var itemData:CMSCategoryItemData? {
        didSet {
            title = itemData?.title
            itemUrl = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.cmsItemDetailQuery, parameter: ["##documentId##":itemData?.id as AnyObject])!
            attachmentListUrl = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query:CMSContext.cmsAttachmentListQuery, parameter: ["##documentId##":itemData?.id as AnyObject])!
        }
    }
    
    var documentId:String?{
        didSet {
            itemUrl = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.cmsItemDetailQuery, parameter: ["##documentId##":documentId as AnyObject])!
            attachmentListUrl = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query:CMSContext.cmsAttachmentListQuery, parameter: ["##documentId##":documentId as AnyObject])!
        }
    }
    
    var publishItemInfo:CMSPublishInfo! {
        didSet {
            if let datas = publishItemInfo.data  {
                self.window?.isHidden = datas.count > 0 ? false : true
                for infoData in datas {
                    let m = ZonePickerModel()
                    m.id = infoData.id
                    m.name = infoData.name
                    m.sourceObj = infoData
                    attachModels.append(m)
                }
            }
         
        }
    }
    
    var attachModels:[ZonePickerModel] = []
    
    var attachmentListUrl = ""
    
    var itemUrl = ""
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        NotificationCenter.default.addObserver(self, selector: #selector(showAttachViewInController(_:)), name: NSNotification.Name("SHOW_ATTACH_OBJ"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(quitAttachViewInController(_:)), name: NSNotification.Name("QUIT_ATTACH_OBJ"), object: nil)
        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        NotificationCenter.default.removeObserver(self)
        self.window?.isHidden = true
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        createButton()
        loadAttachmentList()
        theWebView()
        qlInit()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func theWebView(){
        super.theWebView()
        //注入回复的回复函数
        webView.navigationDelegate = self
        
        webView.uiDelegate = self
        
        webView.allowsBackForwardNavigationGestures = true
        
        loadItemDetail()
    }
    
    private func qlInit(){
//        qlController.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(qlCloseWindow))
        self.qlController.delegate = self
        self.qlController.dataSource = self
    }
    
    private func loadAttachmentList(){
        Alamofire.request(attachmentListUrl, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                self.publishItemInfo = Mapper<CMSPublishInfo>().map(JSONObject: val)!
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        }
    }
    
    private func loadItemDetail()  {
        webView.load(Alamofire.request(itemUrl).request!)
    }
    
    @objc private func qlCloseWindow(){
        self.dismiss(animated: true, completion: {
            
        })
    }
    
    @objc private func quitAttachViewInController(_ noti:NSNotification){
        self.window?.isHidden = false
    }
    
    @objc private func showAttachViewInController(_ noti:NSNotification){
        if let obj = noti.object {
            let m = obj as! ZonePickerModel
            self.downloadFile(m, { (url) in
                //self.performSegue(withIdentifier: "showInQL", sender: url)
                self.currentFileURLS.removeAll(keepingCapacity: true)
                let currentURL = NSURL(fileURLWithPath: url)
                if QLPreviewController.canPreview(currentURL) {
                    self.currentFileURLS.append(currentURL)
                    self.qlController.reloadData()
                    if #available(iOS 10, *) {
                        let navVC = ZLNormalNavViewController(rootViewController: self.qlController)
                        self.qlController.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(self.qlCloseWindow))
                        self.presentVC(navVC)
                    }else{
                        //if #available(iOS 9, *){
                        self.zonePickerView.hidePickerView()
                        let prController = CMSQLViewController()
                        prController.delegate = self
                        prController.dataSource = self
                        self.pushVC(prController)
                        //}
                    }
                    
                    
                }
            })
        }
    }
    
    private func downloadFile(_ model:ZonePickerModel,_ completed:@escaping (_ localURLForFile:String) -> Void){
        let downURL = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsAttachmentDownloadQuery, parameter: ["##id##":model.id as AnyObject])!
        let destination: DownloadRequest.DownloadFileDestination = { _, _ in
            let documentsURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
            let fileURL = documentsURL.appendingPathComponent(model.name)
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
        }
        self.showMessage(title: "downloading...")
        Alamofire.download(downURL, to: destination).response { response in
            print(response)
            if response.error == nil, let filePath = response.destinationURL?.path {
                DispatchQueue.main.async {
                    self.dismissProgressHUD()
                }
               completed(filePath)
            }else{
                DispatchQueue.main.async {
                    self.showError(title: "文件下载出错")
                }
            }
        }
    }
    

    
    //创建一个显示列表附件的按钮
   private func createButton(){
        let width = SCREEN_WIDTH
        let height = SCREEN_HEIGHT
        let button  = UIButton(frame: CGRect(x: 0,y: 0,width:30,height: 30))
        button.setImage(UIImage(named: "icon_attach"), for:.normal)
        button.addTarget(self, action: #selector(showAttachmentList(_:)), for: .touchUpInside)
        self.window = UIWindow(frame: CGRect(x: (width - 30) / 2, y: height - 60, width: 30, height: 30))
        self.window?.windowLevel = UIWindow.Level.alert + 1
        self.window?.backgroundColor = UIColor.white
        self.window?.layer.cornerRadius = 15
        self.window?.layer.masksToBounds = true
        self.window?.addSubview(button)
        self.window?.makeKeyAndVisible()
    }
    
    @objc func showAttachmentList(_ sender:UIButton){
        print("List AttachList")
        self.window?.isHidden = true
        self.zonePickerView = ZonePickerView()
        zonePickerView.models = attachModels
        zonePickerView.showPickerView()
        
    }
    
}

extension CMSItemDetailViewController:WKNavigationDelegate,WKUIDelegate {
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        DDLogDebug("didFailProvisionalNavigation \(navigation)  error = \(error)")
    }
    
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        DDLogDebug("didStartProvisionalNavigation \(navigation)")
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        DDLogDebug("didCommit")
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        DDLogDebug("didFinish")
        //self.setupData()
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        DDLogDebug("didFail")
        DDLogError(error.localizedDescription)
    }
    
    
}

extension CMSItemDetailViewController:QLPreviewControllerDataSource,QLPreviewControllerDelegate{
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return self.currentFileURLS.count
    }
    
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return self.currentFileURLS[index]
    }
    
    func previewControllerWillDismiss(_ controller: QLPreviewController) {
        guard #available(iOS 10,*) else{
            self.showAttachmentList(UIButton(type: .custom))
            return
        }
    }
}




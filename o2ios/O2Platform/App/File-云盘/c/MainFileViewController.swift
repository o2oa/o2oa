//
//  MainFileViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import BSImagePicker
import Photos
import QuickLook

import CocoaLumberjack

import O2OA_Auth_SDK

private let kLivelyRedColor = base_color

class MainFileViewController: UIViewController {
    
    @IBOutlet weak var tableView: ZLBaseTableView!
    
    var myFiles:[OOFile] = []
    
    var myFileShare:[FileShare] = []
    
    var myFileRecive:[FileShare] = []
    
    //移动选择的文件夹
    var moveSourceFile:OOFile?
    
    var moveTargeFolder:OOFile?
    
    //分享的人员列表
    var sharedPersons:[PersonV2] = []
    
    //分享的文件
    var currentSharedFile:OOFile?
    
    //文件夹深度队列
    var folderQueue:[OOFile] = []
    
    var segmentedControl:SegmentedControl?{
        didSet {
            tabIndex = (segmentedControl?.selectedIndex)!
        }
    }
    
    var tabIndex:Int = 0
    
    //预览
    let quickLookController = QLPreviewController()
    
    var fileURLs = [URL]()
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //self.tableView.registerClass(Class() , forCellReuseIdentifier: "FileShareTableViewCell")
        setupUI()
        self.tableView.contentInset = UIEdgeInsets(top: 40.0, left: 0, bottom: 0, right: 0)
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadDataByTabIndex(tabIndex: self.tabIndex)
        })
        self.loadDataByTabIndex(tabIndex:tabIndex)
        
        quickLookController.dataSource = self
        quickLookController.delegate = self
    }
    
    func loadDataRequestFileCompleted(_ url:String){
        self.showLoading(title: "加载中")
        Alamofire.request(url).responseJSON { response in
            self.myFiles.removeAll()
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    let json = JSON(val)["data"]
                    let files = Mapper<OOFile>().mapArray(JSONString:json["attachmentList"].description)
                    let folders = Mapper<OOFile>().mapArray(JSONString:json["folderList"].description)
                    DispatchQueue.main.async {
                        self.myFiles.append(contentsOf: files!)
                        self.myFiles.append(contentsOf: folders!)
                        self.tableView.reloadData()
                        self.showSuccess(title: "加载完成")
                    }
                }else{
                    DispatchQueue.main.async {
                        DDLogError(JSON(val).description)
                        self.tableView.reloadData()
                        self.showError(title: "加载失败")
                    }
                    
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.tableView.reloadData()
                    self.showError(title: "加载失败")
                }
                
            }
            DispatchQueue.main.async {
                if self.tableView.mj_header.isRefreshing(){
                    self.tableView.mj_header.endRefreshing()
                }
            }
            
        }
        
    }
    
    func loadDataRequestFileShareCompleted(_ url:String){
        self.showLoading(title: "加载中")
        Alamofire.request(url).responseArray(queue: nil, keyPath: "data", context: nil)
        { (response:DataResponse<[FileShare]>) in
            self.myFileShare.removeAll()
            debugPrint(response.result)
            switch response.result {
            case .success(let shares):
                DispatchQueue.main.async {
                    self.myFileShare.append(contentsOf: shares)
                    self.tableView.reloadData()
                    self.showSuccess(title: "加载完成")
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.tableView.reloadData()
                    self.showError(title: "加载失败")
                }
            }
            DispatchQueue.main.async {
                if self.tableView.mj_header.isRefreshing(){
                    self.tableView.mj_header.endRefreshing()
                }
            }
        }
    }
    
    func loadDataRequestFileReciveCompleted(_ url:String) {
        self.showLoading(title: "加载中")
        Alamofire.request(url).responseArray(queue: nil, keyPath: "data", context: nil) {
            (response:DataResponse<[FileShare]>) in
            self.myFileRecive.removeAll()
            switch response.result {
            case .success(let shares):
                DispatchQueue.main.async {
                    self.myFileRecive.append(contentsOf: shares)
                    self.tableView.reloadData()
                    self.showSuccess(title: "加载完成")
                }
                
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.tableView.reloadData()
                    self.showError(title: "加载失败")
                }
            }
            DispatchQueue.main.async {
                if self.tableView.mj_header.isRefreshing(){
                    self.tableView.mj_header.endRefreshing()
                }
            }
            
        }
    }
    
    
    
    func loadDataByTabIndex(tabIndex index:Int){
        //计算URL
        var url = ""
        switch index {
        case 0:
            self.tableView.emptyTitle = "没有文件数据"
            if self.folderQueue.count <= 0 {
                url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileTopListQuery, parameter: nil)!
                self.loadDataRequestFileCompleted(url)
            }else{
                let f = self.folderQueue.last
                //读取
                let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderItemIdQuery, parameter: ["##id##":f!.id! as AnyObject])
                self.loadDataRequestFileCompleted(url!)
            }
        case 1:
            self.tableView.emptyTitle = "没有收到共享的文件"
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileShareQuery, parameter: nil)!
            self.loadDataRequestFileShareCompleted(url)
        case 2:
            self.tableView.emptyTitle = "没有共享文件给其它人"
            url  = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileEditorQuery, parameter: nil)!
            self.loadDataRequestFileReciveCompleted(url)
        default:
            url = ""
        }
        
        
    }
    
    func setupUI(){
        self.initSegmentedControl()
    }
    
    func initSegmentedControl(){
        let titleStrings = ["我的文件","共享文件","收到文件"]
        let titles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: UIColor.black]
            var titles = [NSAttributedString]()
            for titleString in titleStrings {
                let title = NSAttributedString(string: titleString, attributes: attributes)
                titles.append(title)
            }
            return titles
        }()
        let selectedTitles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: kLivelyRedColor]
            var selectedTitles = [NSAttributedString]()
            for titleString in titleStrings {
                let selectedTitle = NSAttributedString(string: titleString, attributes: attributes)
                selectedTitles.append(selectedTitle)
            }
            return selectedTitles
        }()
        self.segmentedControl = SegmentedControl.initWithTitles(titles, selectedTitles: selectedTitles)
        self.segmentedControl!.delegate = self
        self.segmentedControl!.backgroundColor = toolbar_background_color
        self.segmentedControl!.autoresizingMask = [.flexibleRightMargin, .flexibleWidth]
        self.segmentedControl!.selectionIndicatorStyle = .bottom
        self.segmentedControl!.selectionIndicatorColor = kLivelyRedColor
        self.segmentedControl!.selectionIndicatorHeight = 3
        self.segmentedControl!.segmentWidth = SCREEN_WIDTH / 3
        self.segmentedControl!.frame.origin.y = 0
        self.segmentedControl!.frame.size = CGSize(width: UIScreen.main.bounds.width, height: 40)
        view.insertSubview(self.segmentedControl!, belowSubview: navigationController!.navigationBar)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    @IBAction func showMyFile(_ sender: UIBarButtonItem) {
        
        self.performSegue(withIdentifier: "showMyDownloadSegue", sender: nil)
    }
    
    
    
    @IBAction func backToHome(_ sender: UIBarButtonItem) {
        let backType = AppConfigSettings.shared.appBackType
        if backType == 1 {
            self.performSegue(withIdentifier: "backToMain", sender: nil)
        }else if backType == 2 {
            self.performSegue(withIdentifier: "backToApps", sender: nil)
        }
    }
    
    @IBAction func uploadFileMenu(_ sender: UIBarButtonItem) {
        let menuAlertController = UIAlertController(title: "文件操作", message: "文件或文件夹操作", preferredStyle: .actionSheet)
        let fileUploadAlert = UIAlertAction(title: "上传文件", style: .destructive) { (fileUploadAction) in
            self.uploadFile()
        }
        let folderCreateAlert = UIAlertAction(title: "创建文件夹", style: .default) { (createFolderAction) in
            self.createFolder()
        }
        let cancelAlert = UIAlertAction(title: "取消", style: .cancel) { (cancelAction) in
            
        }
        menuAlertController.addAction(fileUploadAlert)
        menuAlertController.addAction(folderCreateAlert)
        menuAlertController.addAction(cancelAlert)
        self.present(menuAlertController, animated: true, completion: nil)
    }
    
    func uploadFile(){
        let vc = FileBSImagePickerViewController()
        var url = ""
        if self.folderQueue.count == 0 {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileUploadTopQuery, parameter: nil,coverted: true)!
        }else{
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileUploadSubQuery, parameter: ["##id##":(self.folderQueue.last?.id!)! as AnyObject],coverted: true)!
        }
        url = url.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed)!
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
                    let headers:HTTPHeaders = ["x-token":(O2AuthSDK.shared.myInfo()?.token!)!]
                    PHImageManager.default().requestImageData(for: asset, options: options, resultHandler: { (imageData, result, imageOrientation, dict) in
                        //debugPrint(imageData,result,imageOrientation,dict)
                        //DDLogDebug("result = \(result) imageOrientation = \(imageOrientation) \(dict)")
                        let fileURL = dict?["PHImageFileURLKey"] as! URL
                        let fileSrcName = fileURL.description
                        let fName = fileSrcName.components(separatedBy: "/").last!
                        let fExtName = fName.components(separatedBy: ".").last!
                        let fPreName = fName.components(separatedBy: ".").first!
                        DispatchQueue.main.async {
                            self.showLoading(title:"上传中...")
                            Alamofire.upload(multipartFormData: { (mData) in
                                let formatter = DateFormatter()
                                formatter.dateFormat = "yyyyMMddHHmmss"
                                let str = formatter.string(from: Date())
                                let fileName = "\(fPreName.lowercased())_\(str).\(fExtName.lowercased())"
                                mData.append(imageData!, withName: "file", fileName: fileName, mimeType: "image/\(fExtName)")
                            }, to: url,headers:headers,encodingCompletion: { (encodingResult) in
                                switch encodingResult {
                                case .success(let upload,_,_):
                                    debugPrint(upload)
                                    upload.responseJSON { response in
                                        debugPrint(response)
                                        DispatchQueue.main.async {
                                            self.showSuccess(title: "上传成功")
                                            Timer.after(0.8, {
                                                self.tableView.mj_header.beginRefreshing()
                                            })

                                        }
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        DispatchQueue.main.async {
                                            self.showError(title: "上传失败")
                                        }
                                    }
                                }
                                
                            })
                        }
                        ////                           Alamofire.upload(.POST, url, headers:nil, multipartFormData: { (mData) in
                        ////
                        ////                            let formatter = DateFormatter()
                        ////                            formatter.dateFormat = "yyyyMMddHHmmss"
                        ////                            let str = formatter.string(from: Date())
                        ////                            let fileName = "\(fPreName.lowercased())_\(str).\(fExtName.lowercased())"
                        ////                            mData.appendBodyPart(data: imageData!, name: "file", fileName: fileName, mimeType: "image/png;*/*")
                        ////
                        ////                            }, encodingMemoryThreshold: SessionManager.MultipartFormDataEncodingMemoryThreshold, encodingCompletion: { (responseResult:SessionManager.MultipartFormDataEncodingResult) in
                        ////                                switch responseResult {
                        ////                                case .success:
                        ////                                    //self.loadDataByTabIndex(tabIndex: 0)
                        ////                                    DispatchQueue.main.async{
                        ////                                        ProgressHUD.showSuccess("上传成功")
                        ////                                    }
                        ////                                case .failure(let errType):
                        ////                                    DDLogError(errType)
                        ////                                    DispatchQueue.main.async{
                        ////                                         ProgressHUD.showError("上传失败")
                        ////                                    }
                        ////
                        ////
                        ////                                }
                        ////                           })
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
    
    func createFolder(){
        let createAlertController = UIAlertController(title: "创建文件夹", message: "", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "确定", style: .destructive) { ok in
            DDLogDebug("ok Click")
            let textInputField = createAlertController.textFields![0]
            if let text = textInputField.text {
                let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderCreateQuery, parameter: nil)
                let s = self.folderQueue.count == 0 ? "":self.folderQueue.last?.id!
                let parameter = ["name":text,"superior":s!]
                Alamofire.request(url!, method:.post, parameters: parameter, encoding: JSONEncoding.default, headers: nil).responseJSON(completionHandler: { (response) in
                    switch response.result {
                    case .success(let val):
                        let json = JSON(val)
                        if json["type"] == "success" {
                            DispatchQueue.main.async {
                                self.showSuccess(title: "创建成功")
                                Timer.after(0.5, {
                                    self.tableView.mj_header.beginRefreshing()
                                })
                            }
                        }else{
                            DispatchQueue.main.async {
                                DDLogError(json.description)
                                self.showError(title: "创建失败")
                            }
                            
                        }
                    case .failure(let err):
                        DispatchQueue.main.async {
                            DDLogError(err.localizedDescription)
                            self.showError(title: "创建失败")
                        }
                    }
                    
                })
                //                Alamofire.request(.POST, url!, parameters: parameter, encoding: .json, headers: nil).responseJSON(completionHandler: { (response) in
                //                    switch response.result {
                //                    case .success(let val):
                //                        let json = JSON(val)
                //                        if json["type"] == "success" {
                //                            ProgressHUD.showSuccess("创建成功")
                //                        }else{
                //                            DDLogError(json)
                //                            ProgressHUD.showError("创建失败")
                //                        }
                //                    case .failure(let err):
                //                        ProgressHUD.showError("创建失败")
                //                        DDLogError(err.localizedDescription)
                //                    }
                //                })
            }
        }
        let cancelAction = UIAlertAction(title: "取消", style: .cancel) { (cancel) in
            DDLogDebug("cancel Click")
        }
        createAlertController.addTextField { (folderInputTextField) in
            
        }
        createAlertController.addAction(okAction)
        createAlertController.addAction(cancelAction)
        self.present(createAlertController, animated: true, completion: nil)
    }
    
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showSharePersonSegue" {
            // TODO: 这里需要修改，----后续。
//            let navVC = segue.destination as! ZLNavigationController
//            let destVC = navVC.topViewController  as! MeetingPersonListViewController
//            destVC.delegate = self
//            destVC.selectPersons = self.sharedPersons
        }else if segue.identifier == "showSelectFolderSegue"{
            let navVC = segue.destination as! ZLNavigationController
            let destVC = navVC.topViewController as! FileFolderSelectViewController
            destVC.delegate = self
        }else if segue.identifier == "showMyShareSegue" {
            //            let navVC = segue.destinationViewController as! ZLNavigationController
            //            let destVC = navVC.topViewController as! FileMyShareListViewController
            let destVC = segue.destination as! FileMyShareListViewController
            destVC.myFileURL = sender as? String
        }
    }
    
    
    
}


//extension MainFileViewController:MeetingPersonListPassValue {
//    func selectPersonPassValue(_ persons: [PersonV2]) {
//        self.sharedPersons.removeAll()
//        self.sharedPersons.append(contentsOf: persons)
//        self.fileShareAction()
//    }
//}

extension MainFileViewController: SegmentedControlDelegate {
    func segmentedControl(_ segmentedControl: SegmentedControl, didSelectIndex selectedIndex: Int) {
        print("Did select index \(selectedIndex)")
        tabIndex = selectedIndex
        self.loadDataByTabIndex(tabIndex: tabIndex)
        switch segmentedControl.style {
        case .text:
            print("The title is “\(segmentedControl.titles[selectedIndex].string)”\n")
        case .image:
            print("The image is “\(segmentedControl.images[selectedIndex])”\n")
        }
    }
    
    func segmentedControl(_ segmentedControl: SegmentedControl, didLongPressIndex longPressIndex: Int) {
        print("Did long press index \(longPressIndex)")
        if UIDevice.current.userInterfaceIdiom == .pad {
            let viewController = UIViewController()
            viewController.modalPresentationStyle = .popover
            viewController.preferredContentSize = CGSize(width: 200, height: 300)
            if let popoverController = viewController.popoverPresentationController {
                popoverController.sourceView = view
                let yOffset: CGFloat = 10
                popoverController.sourceRect = view.convert(CGRect(origin: CGPoint(x: 70 * CGFloat(longPressIndex), y: yOffset), size: CGSize(width: 70, height: 30)), from: navigationItem.titleView)
                popoverController.permittedArrowDirections = .any
                present(viewController, animated: true, completion: nil)
            }
        } else {
            let message = segmentedControl.style == .text ? "Long press title “\(segmentedControl.titles[longPressIndex].string)”" : "Long press image “\(segmentedControl.images[longPressIndex])”"
            let alert = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
            let cancelAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
            alert.addAction(cancelAction)
            present(alert, animated: true, completion: nil)
        }
    }
}

extension MainFileViewController:UITableViewDelegate,UITableViewDataSource{
    
    func numberOfSections(in tableView: UITableView) -> Int {
        //        switch tabIndex {
        //        case 0:
        //            return 1
        //        case 1:
        //            return self.myFileShare.count > 0 ? self.myFileShare.count : 1
        //        case 2:
        //            return self.myFileRecive.count > 0 ? self.myFileRecive.count : 1
        //        default:
        //            return 1
        //        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch tabIndex {
        case 0:
            return self.myFiles.count
        case 1:
            return self.myFileShare.count
        case 2:
            return self.myFileRecive.count
        default:
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //按不同类型生成相应的cell
        switch tabIndex {
        case 0:
            let file = self.myFiles[(indexPath as NSIndexPath).row]
            let cell = tableView.dequeueReusableCell(withIdentifier: "FileTableViewCell", for: indexPath) as! FileTableViewCell
            cell.delegate  = self
            cell.file = file
            return cell
        case 1:
            let fileShare = self.myFileShare[(indexPath as NSIndexPath).row]
            let cell = tableView.dequeueReusableCell(withIdentifier: "FileShareTableViewCell",for: indexPath) as! FileShareTableViewCell
            cell.fileShare = fileShare
            return cell
        case 2:
            let fileRecive = self.myFileRecive[(indexPath as NSIndexPath).row]
            let cell = tableView.dequeueReusableCell(withIdentifier: "FileShareTableViewCell", for: indexPath) as! FileShareTableViewCell
            cell.fileShare = fileRecive
            return cell
        default:
            return UITableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if tabIndex == 0 {
            return 40.0
        }else{
            return 0.0
        }
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if tabIndex == 0 {
            let headerView = FolderHeaderView()
            headerView.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 40)
            headerView.folderQueue = self.folderQueue
            headerView.delegate = self
            return headerView
        }else{
            return UIView()
        }
    }
    
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        DDLogDebug("cell clicked row  = \(indexPath.row)")
        if tabIndex == 0 {
            //我的文件
            let f = self.myFiles[indexPath.row]
            if f.fileType == .folder {
                //文件夹显示文件夹内容
                self.folderQueue.append(f)
                //读取
                let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderItemIdQuery, parameter: ["##id##":f.id! as AnyObject])
                self.loadDataRequestFileCompleted(url!)
                
            }else{
                self.filePreview(f)
            }
        }else if tabIndex == 1 {
            //共享文件
            let fileShare = self.myFileShare[(indexPath as NSIndexPath).row]
            //读取共享文件列表
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileMyShareListQuery, parameter: ["##name##":fileShare.name! as AnyObject])
            self.performSegue(withIdentifier: "showMyShareSegue", sender: url)
            
        }else if tabIndex == 2 {
            let fileRecive = self.myFileRecive[(indexPath as NSIndexPath).row]
            //接收的文件
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileMyEditorListQuery, parameter: ["##name##":fileRecive.name! as AnyObject])
            self.performSegue(withIdentifier: "showMyShareSegue", sender: url)
        }
    }
    
    func filePreview(_ f:OOFile){
        //文件就打开
        let fileURL = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileDownloadItemIdQuery, parameter: ["##id##":f.id! as AnyObject])
        //缓存到本地
        var fileLocalURL:URL?
        
        let destination:DownloadRequest.DownloadFileDestination = { temporaryURL, response in
            let baseURL = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true)[0]
            let folder = URL(fileURLWithPath:baseURL).appendingPathComponent("tmpFile", isDirectory: true)
            //判断文件夹是否存在，不存在则创建
            let exist = FileManager.default.fileExists(atPath: folder.path)
            if !exist {
                try!  FileManager.default.createDirectory(at: folder, withIntermediateDirectories: true,
                                                          attributes: nil)
            }
            //增加随机数
            let preName = f.name?.components(separatedBy: ".").first!
            let extName = f.name?.components(separatedBy: ".").last!
            let timestamp  = Date().timeIntervalSince1970.description
            fileLocalURL = folder.appendingPathComponent("\(preName!)_\(timestamp).\(extName!)")
            return (fileLocalURL!,[.removePreviousFile, .createIntermediateDirectories])
        }
        self.showLoading(title: "下载中...")
        Alamofire.download(fileURL!,to: destination).downloadProgress(closure: { (progress) in
            print("progress.fractionCompleted = \(progress.fractionCompleted)")
            if progress.completedUnitCount == progress.totalUnitCount {
                DispatchQueue.main.async {
                    self.hideLoading()
                }
            }
        }).responseData { resp in
            switch resp.result {
            case .success(_):
                DispatchQueue.main.async {
                    self.hideLoading()
                }
                self.fileURLs.removeAll(keepingCapacity: true)
                if QLPreviewController.canPreview(fileLocalURL! as QLPreviewItem){
                    self.fileURLs.append(fileLocalURL!)
                    self.quickLookController.reloadData()
                    self.quickLookController.currentPreviewItemIndex = 0
                    self.navigationController?.pushViewController(self.quickLookController, animated: true)
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        }
        
        
        
        //            .response { (request, response, data, error) in
        //            if let err = error {
        //                DDLogError(err.localizedDescription)
        //            }else{
        //                ProgressHUD.dismiss()
        //                self.fileURLs.removeAll()
        //                if QLPreviewController.canPreview(fileLocalURL!){
        //                    self.fileURLs.append(fileLocalURL!)
        //                    self.quickLookController.reloadData()
        //                    self.quickLookController.currentPreviewItemIndex = 0
        //                    self.navigationController?.pushViewController(self.quickLookController, animated: true)
        //                }
        //            }
        //        }
        
        
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        DDLogDebug("touch")
        for touch in touches {
            DDLogDebug("touch view  = \(touch.view)")
        }
    }
    
}
private let FileMenu = ["重命名","删除","移动","下载","分享"]

private let FileFolderMenu = ["重命名","删除","移动"]

private let FileMenuColor = [UIColor.white,
                             UIColor.purple,
                             UIColor.cyan,
                             UIColor.yellow,
                             UIColor.green]

private let FileFolderMenuColor = [UIColor.white,
                                   UIColor.purple,
                                   UIColor.cyan]


extension MainFileViewController:FileTableViewCellDelegate{
    func cellDidClicked(_ cell: FileTableViewCell, file: OOFile) {
        DDLogDebug("tableView contentOffset = \(self.tableView.contentOffset)")
        DDLogDebug("file clicked id = \(file.id!)")
        let startX = SCREEN_WIDTH - 10
        //let startY = self.tableView.contentOffset.y > 0 ? cell.center.y -  self.tableView.contentOffset.y : cell.center.y
        let startY = cell.center.y -  self.tableView.contentOffset.y
        DDLogDebug("startx = \(startX),starty = \(startY)")
        let startPoint = CGPoint(x: startX, y: startY)
        if file.fileType == .folder {
            //显示文件夹上下文菜单
            AZPopMenu.show(self.view, startPoint: startPoint, items: FileFolderMenu, colors: FileFolderMenuColor, selected: { (itemSelected) in
                DDLogDebug("\(itemSelected)")
                switch itemSelected {
                case 0:
                    self.fileRename(file)
                case 1:
                    self.fileDelete(file)
                case 2:
                    self.fileMove(file)
                default:
                    DDLogDebug("no action")
                }
                
            })
            
        }else if file.fileType  == .file {
            //显示文件上下文菜单
            AZPopMenu.show(self.view, startPoint: startPoint, items: FileMenu, colors: FileMenuColor, selected: { (itemSelected) in
                DDLogDebug("\(itemSelected)")
                switch itemSelected {
                case 0:
                    self.fileRename(file)
                case 1:
                    self.fileDelete(file)
                case 2:
                    self.fileMove(file)
                case 3:
                    self.fileDownload(file)
                case 4:
                    self.fileShare(file)
                default:
                    DDLogDebug("no action")
                }
            })
        }
    }
    
    fileprivate func renameFileAction(sourceFile f:OOFile,newFileName name:String){
        var url = ""
        var parameter:Dictionary<String,String> = [:]
        if f.fileType == .file {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileRenameQuery, parameter: ["##id##":f.id! as AnyObject])!
            parameter["name"] = name
            parameter["folder"] = f.folder!
        }else if f.fileType == .folder {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderActionIdQuery,parameter: ["##id##":f.id! as AnyObject])!
            parameter["name"] = name
            parameter["superior"] = f.superior!
        }
        self.showLoading(title: "更新中")
        Alamofire.request(url, method: .put, parameters: parameter, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let json = JSON(val)
                if json["type"] == "success" {
                    self.myFiles.forEachEnumerated({ (index, file) in
                        if file.id == f.id {
                            file.name = name
                        }
                    })
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                        self.showSuccess(title: "重命名完成")
                    }
                    
                }else{
                    
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.showError(title: "重命名失败")
                }
            }
        }
        
    }
    
    //重命名
    func fileRename(_ f:OOFile){
        let renameViewController = UIAlertController(title: "重命名", message: "原名称:\(f.name!)", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "确定", style: .destructive) { (okAction) in
            let textField = renameViewController.textFields![0]
            if let text = textField.text  {
                if text.isEmpty {
                    DDLogDebug("empty name  = \(text)")
                }else{
                    DDLogDebug("new name  = \(text)")
                    self.renameFileAction(sourceFile: f, newFileName: text)
                }
            }
        }
        
        let cancelAction = UIAlertAction(title: "取消", style: .cancel) { (cancelAction) in
            
        }
        renameViewController.addTextField { (textField) in
            textField.placeholder = "请输入新名称"
            textField.textColor = UIColor.red
        }
        renameViewController.addAction(okAction)
        renameViewController.addAction(cancelAction)
        
        self.present(renameViewController, animated: true, completion: nil)
        
    }
    
    fileprivate func deleteFileAction(_ f:OOFile){
        var url = ""
        if f.fileType == .file {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileDeleteQuery, parameter: ["##id##":f.id! as AnyObject])!
        }else if f.fileType == .folder {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderActionIdQuery, parameter: ["##id##":f.id! as AnyObject])!
        }
        self.showLoading(title: "删除中")
        Alamofire.request(url,method:.delete, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let json = JSON(val)
                if json["type"] == "success" {
                    self.myFiles.remove(at: self.myFiles.index(where: { (file) -> Bool in
                        if f.id == file.id {
                            return true
                        }else{
                            return false
                        }
                    })!)
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                        self.showSuccess(title: "删除成功")
                    }
                    
                }else{
                    DispatchQueue.main.async {
                        DDLogError("删除失败:\(json)")
                        self.showError(title: "删除失败")
                    }
                    
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError("删除失败:\(err)")
                    self.showError(title: "删除失败")
                }
                
            }
        }
    }
    
    //删除
    func fileDelete(_ f:OOFile){
        let deleViewController = UIAlertController(title: "删除", message: "删除文件:\(f.name!)", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "确定", style: .destructive) { (okAction) in
            //执行删除
            self.deleteFileAction(f)
        }
        let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
        deleViewController.addAction(okAction)
        deleViewController.addAction(cancelAction)
        self.present(deleViewController,animated: true,completion: nil)
    }
    
    func fileMoveAction(){
        //移动
        var url = ""
        var parameter:[String:AnyObject] = [:]
        if moveSourceFile?.fileType == .file {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileRenameQuery, parameter: ["##id##":(moveSourceFile?.id)! as AnyObject])!
            parameter["name"] = moveSourceFile?.name as AnyObject?
            parameter["folder"] = moveTargeFolder?.id as AnyObject?
        }else if moveSourceFile?.fileType == .folder {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderActionIdQuery, parameter: ["##id##":(moveSourceFile?.id)! as AnyObject])!
            parameter["name"] = moveSourceFile?.name as AnyObject?
            parameter["superior"] = moveTargeFolder?.id as AnyObject?
        }
        Alamofire.request(url,method:.put, parameters: parameter, encoding: JSONEncoding.default , headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let json = JSON(val)
                if json["type"] == "success" {
                    self.myFiles.remove(at: self.myFiles.index(where: { (file) -> Bool in
                        if self.moveSourceFile?.id == file.id {
                            return true
                        }else{
                            return false
                        }
                    })!)
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                        self.showSuccess(title: "移动完成")
                    }
                    
                }else{
                    DispatchQueue.main.async {
                        DDLogError(json.description)
                        self.showError(title: "移动失败")
                    }
                    
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.showError(title: "移动失败")
                }
            }
        }
        
    }
    
    //移动
    func fileMove(_ f:OOFile){
        self.moveSourceFile = f
        self.performSegue(withIdentifier: "showSelectFolderSegue", sender: nil)
    }
    
    func getRect() -> CGRect {
        return CGRect(
            x: view.centerX - 100 / 2,
            y: view.centerY - 100 / 2 - 33,
            width: 100,
            height: 100)
    }
    
    //下载
    func fileDownload(_ f:OOFile){
        //存储到私有目录
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileDownloadItemIdQuery, parameter: ["##id##":f.id! as AnyObject])
        
        let destination:DownloadRequest.DownloadFileDestination = { temporaryURL, response in
            let baseURL = NSSearchPathForDirectoriesInDomains(.downloadsDirectory, .userDomainMask, true)[0]
            let folder = URL(fileURLWithPath:baseURL).appendingPathComponent("file", isDirectory: true)
            //判断文件夹是否存在，不存在则创建
            let exist = FileManager.default.fileExists(atPath: folder.path)
            if !exist {
                try!  FileManager.default.createDirectory(at: folder, withIntermediateDirectories: true,
                                                          attributes: nil)
            }
            return (folder.appendingPathComponent(f.name!), [.removePreviousFile, .createIntermediateDirectories])
        }
        
        
        self.showLoading(title: "下载中...")
        let utilityQueue = DispatchQueue.global(qos: .utility)
        Alamofire.download(url!, to: destination).downloadProgress(queue: utilityQueue) { (progress) in
            print("progress.fractionCompleted = \(progress.fractionCompleted)")
//            DispatchQueue.main.async {
//                self.showSuccess(title: "<#T##String#>")
//            }
//
            }.responseData { (resp) in
                DispatchQueue.main.async {
                    self.hideLoading()
                }
                switch resp.result {
                case .success( _):
                    DispatchQueue.main.async {
                        self.showSuccess(title: "下载完成")
                    }
                case .failure(let err):
                    DispatchQueue.main.async {
                        DDLogError(err.localizedDescription)
                        self.showError(title: "下载失败")
                    }
                }
        }
    }
    
    //分享
    func fileShare(_ f:OOFile){
        currentSharedFile = f
        self.performSegue(withIdentifier: "showSharePersonSegue", sender: nil)
    }
    
    func fileShareAction(){
        var names:[String] = []
        sharedPersons.forEach { (p) in
            names.append(p.name!)
        }
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileShareActionQuery, parameter: ["##id##":(currentSharedFile?.id)! as AnyObject])
        self.showLoading(title: "分享中...")
        Alamofire.request(url!, method:.put , parameters: ["shareList":names], encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let json = JSON(val)
                if json["type"] == "success" {
                    DispatchQueue.main.async {
                        self.showSuccess(title: "分享成功")
                    }
                    
                }else{
                    DispatchQueue.main.async {
                        self.showError(title: "分享失败")
                    }
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.showError(title: "分享失败")
                }
            }
        }
        
    }
}

extension MainFileViewController:FileFolderPassValueDelegate{
    func selectedFolder(_ f: OOFile) {
        self.moveTargeFolder = f
        self.fileMoveAction()
    }
}



extension MainFileViewController:FolderHeaderViewDelegate{
    func headerClickSelected(currentFile f: OOFile, folderQueue fQueue: [OOFile]) {
        self.folderQueue = fQueue
        var url = ""
        if f.id == "0" {
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileTopListQuery, parameter: nil)!
        }else{
            url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderItemIdQuery, parameter: ["##id##":f.id! as AnyObject])!
        }
        self.loadDataRequestFileCompleted(url)
    }
}

//quick look
extension MainFileViewController:QLPreviewControllerDataSource,QLPreviewControllerDelegate{
    
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return fileURLs.count
    }
    
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return fileURLs[index] as QLPreviewItem
    }
    
}


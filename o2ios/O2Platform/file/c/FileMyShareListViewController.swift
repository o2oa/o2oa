//
//  FileMyShareListViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/27.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import QuickLook
import CocoaLumberjack

class FileMyShareListViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    var myFileURL:String?{
        didSet {
            fileURL = myFileURL
        }
    }
    
    var fileURL:String?
    
    var myFiles = [OOFile]()
    
    //预览
    let quickLookController = QLPreviewController()
    
    var localFileURL = [URL]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadMyFiles()
        })
        self.loadMyFiles()
        quickLookController.delegate = self
        quickLookController.dataSource = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func loadMyFiles(){
        self.myFiles.removeAll()
       self.showMessage(title: "加载中...")
        Alamofire.request(fileURL!).responseArray(queue: nil, keyPath: "data", context: nil) { (resp:DataResponse<[OOFile]>) in
            switch resp.result {
            case .success(let files):
                self.myFiles.append(contentsOf: files)
                self.tableView.reloadData()
                self.showSuccess(title: "加载完成")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.tableView.reloadData()
                self.showError(title: "加载失败")
            }
            if self.tableView.mj_header.isRefreshing(){
                self.tableView.mj_header.endRefreshing()
            }
        }
    }
    

}


extension FileMyShareListViewController:UITableViewDelegate,UITableViewDataSource{
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return myFiles.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "FileTableViewCell", for: indexPath) as! FileTableViewCell
        let file = self.myFiles[(indexPath as NSIndexPath).row]
        cell.file = file
        return cell
        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let file = self.myFiles[(indexPath as NSIndexPath).row]
        self.downloadSelectFile(file)
    }
    
    func downloadSelectFile(_ f:OOFile){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileDownloadItemIdQuery, parameter: ["##id##":f.id! as AnyObject])
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
        self.showMessage(title: "下载中..")
        Alamofire.download(url!, to: destination).downloadProgress { (progress) in
                if progress.completedUnitCount == progress.totalUnitCount {
                    self.dismissProgressHUD()
                }
        }.responseData { resp in
            switch resp.result{
            case .success(_):
                self.localFileURL.removeAll(keepingCapacity: true)
                if QLPreviewController.canPreview(fileLocalURL! as QLPreviewItem){
                    self.localFileURL.append(fileLocalURL!)
                    self.quickLookController.reloadData()
                    self.quickLookController.currentPreviewItemIndex = 0
                    self.navigationController?.pushViewController(self.quickLookController, animated: true)
                }
 
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        }
    }
    
//        { (request, response, data, error) in
//                if let err = error {
//                    DDLogError(err.localizedDescription)
//                }else{
//                    ProgressHUD.dismiss()
//                    self.localFileURL.removeAll()
//                    if QLPreviewController.canPreview(fileLocalURL!){
//                        self.localFileURL.append(fileLocalURL!)
//                        self.quickLookController.reloadData()
//                        self.quickLookController.currentPreviewItemIndex = 0
//                        self.navigationController?.pushViewController(self.quickLookController, animated: true)
//                    }
//                }
//        }
//
//        
//    }
}

extension FileMyShareListViewController:QLPreviewControllerDataSource,QLPreviewControllerDelegate{
    
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return self.localFileURL.count
    }
    
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return self.localFileURL[index] as QLPreviewItem
    }
}

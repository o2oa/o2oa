//
//  FileMyDownloadViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/20.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import QuickLook
import EmptyDataSet_Swift
import CocoaLumberjack

class FileMyDownloadViewController: UIViewController {
    
    @IBOutlet weak var tableView: ZLBaseTableView!
    
    var files:[String] = []
    
    var fileURLs:[URL] = []
    
    let quickLookController = QLPreviewController()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.dataSource = self
        self.tableView.delegate = self
        self.tableView.emptyTitle = "您没有下载文件"
        self.tableView.emptyDesc = "您没有下载文件到本地，可以从前面的列表中选择文件并下载"
        self.loadMyFileFromDirectory()
        quickLookController.delegate = self
        quickLookController.dataSource = self
    }
    
    func loadMyFileFromDirectory(){
        let directoryURL = FileManager.default.urls(for: .downloadsDirectory,
                                                        in: .userDomainMask)[0]
        let folder = directoryURL.appendingPathComponent("file", isDirectory: true)
        do {
        let subs = try FileManager.default.contentsOfDirectory(at: folder, includingPropertiesForKeys: nil, options: FileManager.DirectoryEnumerationOptions.skipsSubdirectoryDescendants)
        fileURLs.append(contentsOf: subs)
        getFileNameFromURLs()
        } catch {
            DDLogError("文件列表错误")
        }
    }
    
    func getFileNameFromURLs(){
        for fileURL in fileURLs {
            let fileName = fileURL.lastPathComponent
            files.append(fileName)
        }
        self.tableView.reloadData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func tableviewEditAction(_ sender: UIBarButtonItem) {
        tableView.setEditing(!tableView.isEditing, animated: true)
        if tableView.isEditing {
            sender.title = "取消"
        }else{
            sender.title = "编辑"
        }
    }
    
    //DataSource titleForEmptyDataSet
    

}

//extension FileMyDownloadViewController:DZNEmptyDataSetSource,DZNEmptyDataSetDelegate{
//    
//    func title(forEmptyDataSet scrollView: UIScrollView!) -> NSAttributedString! {
//        
//        let text  = "没有下载的文件"
//        let attributes = [NSFontAttributeName:UIFont(name: "PingFangSC-Regular", size: 20.0)!,NSForegroundColorAttributeName:RGB(108, g: 108, b: 108)]
//        return NSAttributedString(string: text, attributes: attributes)
//    }
//    
//    func backgroundColor(forEmptyDataSet scrollView: UIScrollView!) -> UIColor! {
//        return RGB(247, g: 247, b: 247)
//    }
//    
//    func emptyDataSetShouldDisplay(_ scrollView: UIScrollView!) -> Bool {
//        return true
//    }
//  
//}

extension FileMyDownloadViewController:UITableViewDelegate,UITableViewDataSource{
    //是否可以编辑
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    //设定编辑样式
    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .delete
    }
    
    //修改编辑文字
    func tableView(_ tableView: UITableView, titleForDeleteConfirmationButtonForRowAt indexPath: IndexPath) -> String? {
        return "删除"
    }
    
    //编辑提交
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        switch editingStyle {
        case .none:
            break
        case .delete:
            //删除数据源
            let fileURL = self.fileURLs[(indexPath as NSIndexPath).row]
            do {
                try FileManager.default.removeItem(at: fileURL)
            } catch let error as NSError {
                DDLogError(error.debugDescription)
            }
            self.files.remove(at: (indexPath as NSIndexPath).row)
            //删除对应的行
            tableView.deleteRows(at: [indexPath], with: .left)
        case .insert:
            break

        }
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.files.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MyFileItemCell", for: indexPath) as! MyFileItemCell
        let fileName = self.files[(indexPath as NSIndexPath).row]
        cell.fileName = fileName
        return cell
        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let fileURL = self.fileURLs[(indexPath as NSIndexPath).row]
        if QLPreviewController.canPreview(fileURL as QLPreviewItem){
            quickLookController.currentPreviewItemIndex = (indexPath as NSIndexPath).row
            navigationController?.pushViewController(quickLookController, animated: true)
        }
    }
}

extension FileMyDownloadViewController:QLPreviewControllerDelegate,QLPreviewControllerDataSource{
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return fileURLs.count
    }
    
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return fileURLs[index] as QLPreviewItem
    }
}



//
//  FileFolderSelectViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/19.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import SwiftyJSON
import CocoaLumberjack

protocol FileFolderPassValueDelegate {
    func selectedFolder(_ f:OOFile)
}

class FileFolderSelectViewController: UIViewController {
    
    
    @IBOutlet weak var tableView: ZLBaseTableView!
    
    var folders:[OOFile] = []
    
    var sFolder:OOFile?
    
    //文件夹层次队列
    var folderQueue:[OOFile] = []
    
    var delegate:FileFolderPassValueDelegate?
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.emptyTitle = "没有可供选择的文件夹"
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileTopListQuery, parameter: nil)!
        self.loadDataRequestFileCompleted(url)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func btnSure(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true) { 
            if self.sFolder != nil {
                self.delegate?.selectedFolder(self.sFolder!)
            }
        }
    }
    
    
    @IBAction func btnCancel(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    
    
}

extension FileFolderSelectViewController:FileFolderCellPassValueDelegate{
    //cellDelegate
    func selectedCellPassValue(_ cell: FileFolderItemCell, f: OOFile) {
         self.sFolder = f
        self.tableView.beginUpdates()
        let cells = self.tableView.visibleCells
        for c in cells {
            let itemCell = c as! FileFolderItemCell
            if itemCell != cell {
                itemCell.actionButton.isSelected = false
            }
        }
        self.tableView.endUpdates()
    }
}

extension FileFolderSelectViewController:FolderHeaderViewDelegate{
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

extension FileFolderSelectViewController:UITableViewDelegate,UITableViewDataSource{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.folders.count
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 40.0

    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {

            let headerView = FolderHeaderView()
            headerView.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 40)
            headerView.folderQueue = self.folderQueue
            headerView.delegate = self
            return headerView
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "FileFolderItemCell", for: indexPath) as! FileFolderItemCell
        let file = self.folders[(indexPath as NSIndexPath).row]
        cell.file = file
        cell.delegate = self
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let f = self.folders[(indexPath as NSIndexPath).row]
        self.folderQueue.append(f)
            //读取
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileFolderItemIdQuery, parameter: ["##id##":f.id! as AnyObject])
        self.loadDataRequestFileCompleted(url!)

    }
    
    func loadDataRequestFileCompleted(_ url:String){
        self.showLoading(title: "加载中")
        Alamofire.request(url).responseJSON { response in
            self.folders.removeAll()
            switch response.result {
            case .success(let val):
                let json = JSON(val)["data"]
                let folders = Mapper<OOFile>().mapArray(JSONString:json["folderList"].description)
                self.folders.append(contentsOf: folders!)
                self.tableView.reloadData()
                self.showSuccess(title: "加载完成")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.tableView.reloadData()
                self.showError(title: "加载失败")
            }
        }
        
    }

    
    
}

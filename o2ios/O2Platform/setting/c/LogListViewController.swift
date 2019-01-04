//
//  LogListViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/6/2.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import QuickLook

class LogListViewController: UIViewController {
    
    fileprivate let qlController = QLPreviewController()
    
    fileprivate var currentFileURLS:[NSURL] = []
    
    @IBOutlet weak var tableView: UITableView!
    
    var logFiles:[O2LogFileInfo] = O2Logger.getLogFiles()

    override func viewDidLoad() {
        super.viewDidLoad()
        qlController.delegate = self
        qlController.dataSource = self
        self.tableView.dataSource = self
        self.tableView.delegate = self
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc fileprivate func qlCloseWindow(){
        self.dismiss(animated: true, completion: {
            
        })
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension LogListViewController:QLPreviewControllerDataSource,QLPreviewControllerDelegate{
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return self.currentFileURLS.count
    }
    
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return self.currentFileURLS[index]
    }
    
    func previewControllerWillDismiss(_ controller: QLPreviewController) {
    }
}

extension LogListViewController:UITableViewDelegate,UITableViewDataSource {
    
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return logFiles.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let fileCell = tableView.dequeueReusableCell(withIdentifier: "LogFileCell", for: indexPath) as! LogFileTableViewCell
        let logFile = logFiles[indexPath.row]
        fileCell.fileNameLabel.text = logFile.friendFileName
        fileCell.fileSizeLabel.text = String(logFile.fileSize)
        return fileCell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let logFile = logFiles[indexPath.row]
        let currentURL = NSURL(fileURLWithPath: logFile.filePath)
        self.currentFileURLS.removeAll(keepingCapacity: true)
        if QLPreviewController.canPreview(currentURL) {
            self.currentFileURLS.append(currentURL)
            self.qlController.reloadData()
            if #available(iOS 10, *) {
                let navVC = ZLNormalNavViewController(rootViewController: self.qlController)
                self.qlController.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(self.qlCloseWindow))
                self.presentVC(navVC)
            }else{
                //if #available(iOS 9, *){
                let prController = CMSQLViewController()
                prController.delegate = self
                prController.dataSource = self
                self.pushVC(prController)
                //}
            }
            
            
        }

    }
    
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
        
    }
    
    func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        let action1 = UITableViewRowAction(style: .destructive, title: "删除") { (action, indexPath) in
            self._delete(indexPath)
        }
        return [action1]
    }
    
    private func _delete(_ indexPath:IndexPath){
        let logFile = logFiles[indexPath.row]
        let currentURL = URL(fileURLWithPath: logFile.filePath)
        do {
            try FileManager.default.removeItem(at: currentURL)
        }catch(let error){
            O2Logger.error(error.localizedDescription)
        }
        logFiles.remove(at: indexPath.row)
        self.tableView.reloadData()
        
    }
}

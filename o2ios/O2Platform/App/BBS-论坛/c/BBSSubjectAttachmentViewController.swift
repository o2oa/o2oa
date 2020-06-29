//
//  BBSSubjectAttachmentViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/28.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import QuickLook
import CocoaLumberjack

class BBSSubjectAttachmentViewController: UITableViewController {
    
    
    var attachmentList:[O2BBSSubjectAttachmentInfo] = []
    
    //预览文件
    private lazy var previewVC: CloudFilePreviewController = {
        return CloudFilePreviewController()
    }()
    
    private lazy var viewModel: BBSViewModel = {
       return BBSViewModel()
   }()
    
    @IBAction func closeAction(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.tableFooterView = UIView()
        
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return self.attachmentList.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "subjectAttachmentCell", for: indexPath) as? BBSSubjectAttachmentViewCell {
            cell.setAttachment(file: self.attachmentList[indexPath.row])
            return cell
        }
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        self.showLoading()
        self.viewModel
            .downloadAttachment(att: self.attachmentList[indexPath.row])
            .always {
                self.hideLoading()
        }.then({ (url)  in
            let currentURL = NSURL(fileURLWithPath: url.path)
            if QLPreviewController.canPreview(currentURL) {
                self.previewVC.currentFileURLS.removeAll()
                self.previewVC.currentFileURLS.append(currentURL)
                self.previewVC.reloadData()
                self.pushVC(self.previewVC)
            }else {
                self.showError(title: "当前文件类型不支持预览！")
            }
        }).catch { (err) in
            DDLogError(err.localizedDescription)
            self.showError(title: "下载文件失败！")
        }
    }

     

}

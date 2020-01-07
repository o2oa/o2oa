//
//  CloudFileTypeListController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/28.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class CloudFileTypeListController: UITableViewController {

    private var fileList: [OOAttachment] = []
    private lazy var cFileVM: CloudFileViewModel = {
        return CloudFileViewModel()
    }()
    var fileType: CloudFileType?
    var baseVC: CloudFileListBaseController?
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if self.parent is CloudFileListBaseController {
            self.baseVC = self.parent as? CloudFileListBaseController
        }
        if fileType == nil {
            self.fileType = .other
        }
        self.tableView.register(UINib.init(nibName: "CFFileTableViewCell", bundle: nil), forCellReuseIdentifier: "CFFileTableViewCell")
        
        self.loadFileList()
    }
    
    private func loadFileList() {
        self.showLoading()
        self.cFileVM.listTypeByPage(type: fileType!, page: 1, count: 100).then { (result) in
            self.fileList = result
            self.hideLoading()
            self.tableView.reloadData()
            }.catch { (error) in
                DDLogError("图片加载失败, \(error.localizedDescription)")
                self.hideLoading()
        }
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.fileList.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "CFFileTableViewCell", for: indexPath) as? CFFileTableViewCell {
            cell.clickdelegate = self.baseVC
            let file = self.fileList[indexPath.row]
            let checked = self.baseVC?.isFileChecked(file) ?? false
            cell.setData(file: file, checked: checked)
            return cell
        }else {
            return UITableViewCell()
        }
    }
    
    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return UIView(frame: CGRect.zero)
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //点击处理
        let item = self.fileList[indexPath.row]
        self.baseVC?.clickFile(file: item)
        tableView.deselectRow(at: indexPath, animated: true)
    }
    

}

extension CloudFileTypeListController: CloudFileListBaseChildDelegate {
    func reloadDataAndUI() {
        self.loadFileList()
        
    }
    
    func reloadUI() {
        self.tableView.reloadData()
    }
    
    
}


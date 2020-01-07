//
//  CloudFileViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import Promises

class CloudFileViewController: CloudFileBaseVC {

    private let tableBottomContraint = "CFtableViewBottomConstraint"
    
    private var dataList: [DataModel] = []
    //上级文件夹 空就是顶层
    private var superior: String = ""
    private var superiorTitle = ""
    
    
    
    //底部工具栏
    var toolbarView: UIToolbar!



    @IBAction func clickCloseAction(_ sender: UIBarButtonItem) {
        print("点击了关闭按钮。。。。。。。。。。。")
        self.dismissVC(completion: nil)
    }
    @IBOutlet weak var listTitleLabel: UILabel!
    
    @IBOutlet weak var tableView: UITableView!
    
    @IBOutlet weak var imageBtn: UIStackView!
    
    
    @IBOutlet weak var documentBtn: UIStackView!
    @IBOutlet weak var musicBtn: UIStackView!
    @IBOutlet weak var videoBtn: UIStackView!
    @IBOutlet weak var otherBtn: UIStackView!
    @IBOutlet weak var shareBtn: UIStackView!


    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = Languager.standardLanguager().string(key: "Cloud Files")
        self.navigationItem.rightBarButtonItems = [UIBarButtonItem(image: UIImage(named: "add"), style: .plain, target: self, action: #selector(addEvent))]
        
        self.superiorTitle = Languager.standardLanguager().string(key: "All File")
        self.setUI()
        //toolbar 初始化底部工具栏 先放在屏幕下面
        self.toolbarView = UIToolbar(frame: CGRect(x: 0, y: self.view.height - 44, width: self.view.width, height: 44))
        //初始化tableView
        self.tableView.dataSource = self
        self.tableView.delegate = self
        self.tableView.register(UINib.init(nibName: "CFFileTableViewCell", bundle: nil), forCellReuseIdentifier: "CFFileTableViewCell")
        self.tableView.register(UINib.init(nibName: "CFFolderTableViewCell", bundle: nil), forCellReuseIdentifier: "CFFolderTableViewCell")
    
        let header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadListData()
        })
        header?.beginRefreshing()
        self.tableView.mj_header = header
    }
    

   
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showListVC" {
            if let vc = segue.destination as? CloudFileListController {
                let folder = sender as? OOFolder
                if let f = folder {
                    vc.breadcrumbList = [f]
                }
            }
        }else if segue.identifier == "showTypeListView" {
            if let typeVC = segue.destination as? CloudFileListBaseController {
                if let type = sender as? CloudFileType
                {
                    typeVC.fileType = type
                }
            }
        }
    }


    // MARK: - private func
    
    private func setUI() {
        self.listTitleLabel.text = self.superiorTitle
        self.imageBtn.addTapGesture { (tap) in
            print("图片按钮。。。。。。。")
            self.performSegue(withIdentifier: "showTypeListView", sender: CloudFileType.image)
        }
        self.documentBtn.addTapGesture { (tap) in
            print("文档按钮。。。。。。。")
            self.performSegue(withIdentifier: "showTypeListView", sender: CloudFileType.office)
        }
        self.musicBtn.addTapGesture { (tap) in
            print("音频按钮。。。。。。。")
            self.performSegue(withIdentifier: "showTypeListView", sender: CloudFileType.music)
        }
        self.videoBtn.addTapGesture { (tap) in
            print("视频按钮。。。。。。。")
            self.performSegue(withIdentifier: "showTypeListView", sender: CloudFileType.movie)
        }
        self.otherBtn.addTapGesture { (tap) in
            print("其他按钮。。。。。。。")
            self.performSegue(withIdentifier: "showTypeListView", sender: CloudFileType.other)
        }
        self.shareBtn.addTapGesture { (tap) in
            print("分享按钮。。。。。。。")
            if let shareToMeVC = self.storyboard?.instantiateViewController(withIdentifier: "cloudFileListMultiModeVC") as? CloudFileListController {
                shareToMeVC.showMode = .shareToMe
                shareToMeVC.breadcrumbList = []
                self.pushVC(shareToMeVC)
            }
        }
    }
    

    //加载数据
    override func loadListData() {
        self.showLoading()
        self.dataList = []
        self.checkedFileList = []
        self.checkedFolderList = []
        all(self.cFileVM.folderList(folderId: self.superior), self.cFileVM.fileList(folderId: self.superior)).then { (result) in
            let folderList = result.0
            DDLogInfo("文件夹：\(folderList.count)")
            for folder in folderList {
                self.dataList.append(folder)
            }
            let fileList = result.1
            DDLogInfo("文件：\(fileList.count)")
            for file in fileList {
                self.dataList.append(file)
            }
            self.hideLoading()
            self.reloadUI()
            self.tableView.mj_header.endRefreshing()
        }.catch { (error) in
            DDLogError(error.localizedDescription)
            self.hideLoading()
            self.reloadUI()
            self.tableView.mj_header.endRefreshing()
        }
    }

    
    @objc private func moreMenuAction() {
        let startX = SCREEN_WIDTH - 10
        let startY = CGFloat(0.0)
        DDLogDebug("startx = \(startX),starty = \(startY)")
        let startPoint = CGPoint(x: startX, y: startY)
        let menu = [
        Languager.standardLanguager().string(key: "Rename"),
        Languager.standardLanguager().string(key: "Move"),
        Languager.standardLanguager().string(key: "Delete"),
        Languager.standardLanguager().string(key: "Share")
        ]
        let color = [
            UIColor.gray,
            UIColor.gray,
            base_color,
            UIColor.gray
        ]
        AZPopMenu.show(self.view, startPoint: startPoint, items: menu, colors: color) { (index) in
            DDLogInfo("點擊了菜單： \(menu[index])")
        }
    }
    //点击新建按钮
    @objc private func addEvent() {
        var actions: [UIAlertAction] = []
        let uploadFile = Languager.standardLanguager().string(key: "Upload File")
        let newFile = UIAlertAction(title: uploadFile, style: .default) { (action) in
            DDLogInfo("选择文件 上传")
            self.choosePhotoAndUpload()
        }
        let newFolderTitle = Languager.standardLanguager().string(key: "New Folder")
        let newFolder = UIAlertAction(title: newFolderTitle, style: .default) { (action) in
            DDLogInfo("新建文件夹")
            self.createFolder()
        }
        actions.append(newFile)
        actions.append(newFolder)
        let newMsg = Languager.standardLanguager().string(key: "New")
        self.showSheetAction(title: "", message: newMsg, actions: actions)
    }

    //新建文件夹
    private func createFolder() {
        let newFolderTitle = Languager.standardLanguager().string(key: "New Folder")
        self.showPromptAlert(title: newFolderTitle, message: "", inputText: "") { (ok, result) in
            if result != "" {
                self.cFileVM.createFolder(name: result).then({ (id) in
                    self.loadListData()
                }).catch({ (error) in
                    DDLogError("创建文件失败,\(error.localizedDescription)")
                    let errTitle = Languager.standardLanguager().string(key: "Create Folder Error Message")
                    self.showError(title: errTitle)
                })
            }
        }
    }

    private func choosePhotoAndUpload() {
        self.choosePhotoWithImagePicker { (fileName, imageData) in
            self.showLoading()
            self.cFileVM.uploadFile(folderId: O2.O2_First_ID, fileName: fileName, file: imageData)
                .then { result in
                    DDLogInfo("上传成功，\(result)")
                    self.hideLoading()
                    self.loadListData()
                }.catch { (error) in
                    DDLogError(error.localizedDescription)
                    self.hideLoading()
            }
        }
    }

    private func isFolderChecked(_ folder: OOFolder) -> Bool {
        return self.checkedFolderList.contains(folder)
    }
    
    private func isFileChecked(_ file: OOAttachment) -> Bool {
        return self.checkedFileList.contains(file)
    }

    
    //底部工具栏中的单个按钮生成
    fileprivate func generateBottomButton(_ items: inout [UIBarButtonItem], name: String, tapCall: @escaping ()->Void) {
        let spaceItem = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: self, action: nil)
        let btn = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
        btn.setTitle(name, for: .normal)
        btn.setTitleColor(base_color, for: .normal)
        btn.addTapGesture { (tap) in
            tapCall()
        }
        let item = UIBarButtonItem(customView: btn)
        items.append(spaceItem)
        items.append(item)
        items.append(spaceItem)
    }
    
    //重新生成刷新底部工具栏和按钮
    private func refreshBottomToolBar() {
        let totalCount = self.checkedFileList.count + self.checkedFolderList.count
        if totalCount > 0 {
            var items: [UIBarButtonItem] = []
            if totalCount == 1 {
                let reName = Languager.standardLanguager().string(key: "Rename")
                generateBottomButton(&items, name: reName, tapCall: {
                    self.renameOp()
                })
            }
            //其他按钮 删除 移动 分享
            let deleteName = Languager.standardLanguager().string(key: "Delete")
            self.generateBottomButton(&items, name: deleteName) {
                self.deleteOp()
            }
            let moveName = Languager.standardLanguager().string(key: "Move")
            self.generateBottomButton(&items, name: moveName) {
                self.moveOp()
            }
            let shareName = Languager.standardLanguager().string(key: "Share")
            self.generateBottomButton(&items, name: shareName) {
                self.shareOp()
            }
            self.layoutBottomBar(items: items)
        }else {
            var c = false
            self.view.constraints.forEach { (constraint) in
                if constraint.identifier == self.tableBottomContraint {
                    c = true
                }
            }
            if !c {
                let bottom = NSLayoutConstraint(item: self.view as Any, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.tableView, attribute: NSLayoutConstraint.Attribute.bottom, multiplier: 1, constant: 0)
                bottom.identifier = self.tableBottomContraint
                self.toolbarView.removeConstraints(self.toolbarView.constraints)
                self.toolbarView.removeFromSuperview()
                self.view.addConstraint(bottom)
                self.view.layoutIfNeeded()
            }
        }
    }
    
    //布局底部工具栏
    private func layoutBottomBar(items: [UIBarButtonItem]) {
        DDLogDebug("layout bottom \(items.count)")
        let toolBarHeight = CGFloat(44.0)
        var bottomSpace = CGFloat(0)
        if iPhoneX {
            bottomSpace = CGFloat(-34.0)
        }
        if items.count > 0 {
            self.toolbarView.items = items
            self.view.addSubview(self.toolbarView)
            self.toolbarView.translatesAutoresizingMaskIntoConstraints = false
            //高度约束
            let heightC = NSLayoutConstraint(item: self.toolbarView as Any, attribute: NSLayoutConstraint.Attribute.height, relatedBy: NSLayoutConstraint.Relation.equal, toItem: nil, attribute: NSLayoutConstraint.Attribute.notAnAttribute, multiplier: 0.0, constant: toolBarHeight)
            self.toolbarView.addConstraint(heightC)
            //底部约束
            let bottom = NSLayoutConstraint(item: self.toolbarView as Any, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.view, attribute: NSLayoutConstraint.Attribute.bottom, multiplier: 1, constant: bottomSpace)
            //右边约束
            let trailing = NSLayoutConstraint(item: self.toolbarView as Any, attribute: NSLayoutConstraint.Attribute.trailing, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.view, attribute: NSLayoutConstraint.Attribute.trailing, multiplier: 1, constant: 0)
            //左边约束
            let leading = NSLayoutConstraint(item: self.toolbarView as Any, attribute: NSLayoutConstraint.Attribute.leading, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.view, attribute: NSLayoutConstraint.Attribute.leading, multiplier: 1, constant: 0)
            self.view.addConstraints([bottom, leading, trailing])
            
            self.view.constraints.forEach { (constraint) in
                //删除原来tableView的底部约束
                
                if constraint.identifier == self.tableBottomContraint {
                    self.view.removeConstraint(constraint)
                }
            }
            //添加tableView和底部工具栏的约束
            let webcTop = NSLayoutConstraint(item: self.tableView as Any, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.toolbarView as Any, attribute: NSLayoutConstraint.Attribute.top, multiplier: 1, constant: 0)
            self.view.addConstraint(webcTop)
            self.view.layoutIfNeeded()
        }
    }
    
    
    
    private func reloadUI() {
        self.refreshBottomToolBar()
        self.tableView.reloadData()
    }
}




// MARK: - UITableView
extension CloudFileViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.dataList.count
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return UIView(frame: CGRect.zero)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = self.dataList[indexPath.row]
        if item is OOFolder {
            let folder = item as! OOFolder
            if let cell = tableView.dequeueReusableCell(withIdentifier: "CFFolderTableViewCell", for: indexPath) as? CFFolderTableViewCell {
                cell.clickdelegate = self
                cell.setData(folder: folder, checked: self.isFolderChecked(folder))
                return cell
            } else {
                return UITableViewCell()
            }
        } else if item is OOAttachment {
            let file = item as! OOAttachment
            if let cell = tableView.dequeueReusableCell(withIdentifier: "CFFileTableViewCell", for: indexPath) as? CFFileTableViewCell {
                cell.clickdelegate = self
                cell.setData(file: file, checked: self.isFileChecked(file))
                return cell
            } else {
                return UITableViewCell()
            }
        }else {
            return UITableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //点击处理
        let item = self.dataList[indexPath.row]
        if item is OOFolder { //点击文件夹进入下一层
            let folder = item as! OOFolder
            self.performSegue(withIdentifier: "showListVC", sender: folder)
        } else if item is OOAttachment { //点击文件
            let file = item as! OOAttachment
            self.clickFile(file: file)
        }
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
}




extension CloudFileViewController: CloudFileCheckClickDelegate {
    func clickFolder(_ folder: OOFolder) {
        if self.checkedFolderList.contains(folder) {
            self.checkedFolderList.removeFirst(folder)
        }else {
            self.checkedFolderList.append(folder)
        }
        self.reloadUI()
    }
    
    func clickFile(_ file: OOAttachment) {
        if self.checkedFileList.contains(file) {
            self.checkedFileList.removeFirst(file)
        }else {
            self.checkedFileList.append(file)
        }
        self.reloadUI()
    }
    
    

}

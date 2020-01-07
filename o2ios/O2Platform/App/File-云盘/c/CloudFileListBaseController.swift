//
//  CloudFileListBaseController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/28.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

enum CloudFileType {
    case image
    case office
    case movie
    case music
    case other
}
protocol CloudFileListBaseChildDelegate {
    func reloadUI()
    func reloadDataAndUI()
}

class CloudFileListBaseController: CloudFileBaseVC {

    @IBOutlet weak var containerView: UIView!
    
    
    //底部工具栏
    private var toolbarView: UIToolbar!
    //底部toolbar和containerView之间的约束名称
    private let containerBottomContraintName = "containerBottomContraint"
    
    var childDelegate: CloudFileListBaseChildDelegate?
    var fileType: CloudFileType = .image
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        var title = Languager.standardLanguager().string(key: "Cloud File Type Image")
        switch self.fileType {
        case .image:
            title = Languager.standardLanguager().string(key: "Cloud File Type Image")
            break
        case .office:
            title = Languager.standardLanguager().string(key: "Cloud File Type Document")
            break
        case .movie:
            title = Languager.standardLanguager().string(key: "Cloud File Type Video")
            break
        case .music:
            title = Languager.standardLanguager().string(key: "Cloud File Type Music")
            break
        case .other:
            title = Languager.standardLanguager().string(key: "Cloud File Type Other")
            break
        }
        self.title = title
        
        //toolbar 初始化底部工具栏 先放在屏幕下面
        self.toolbarView = UIToolbar(frame: CGRect(x: 0, y: self.view.height - 44, width: self.view.width, height: 44))
        
        switch self.fileType {
        case .image:
            if let imageVC = self.storyboard?.instantiateViewController(withIdentifier: "cloudFileImageVC") as? CloudFileImageCollectionController {
                self.addChild(imageVC)
                imageVC.view.frame = self.containerView.bounds
                self.childDelegate = imageVC
                self.containerView.addSubview(imageVC.view)
            }
        default:
            if let otherVC = self.storyboard?.instantiateViewController(withIdentifier: "cloudFileTypeListVC") as? CloudFileTypeListController {
                otherVC.fileType = self.fileType
                self.addChild(otherVC)
                otherVC.view.frame = self.containerView.bounds
                self.childDelegate = otherVC
                self.containerView.addSubview(otherVC.view)
            }
        }
        
        
    }
    
    
    
    
    // MARK: - private method
    
    private func reloadUI() {
        self.refreshBottomToolBar()
        self.childDelegate?.reloadUI()
    }
    
    override func loadListData() {
        self.checkedFileList.removeAll()
        self.refreshBottomToolBar()
        self.childDelegate?.reloadDataAndUI()
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
        let totalCount = self.checkedFileList.count
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
                if constraint.identifier == self.containerBottomContraintName {
                    c = true
                }
            }
            if !c {
                let bottom = NSLayoutConstraint(item: self.view as Any, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.containerView, attribute: NSLayoutConstraint.Attribute.bottom, multiplier: 1, constant: 0)
                bottom.identifier = self.containerBottomContraintName
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
        //适配iPhoneX之后的版本手机
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
                if constraint.identifier == self.containerBottomContraintName {
                    self.view.removeConstraint(constraint)
                }
            }
            //添加tableView和底部工具栏的约束
            let webcTop = NSLayoutConstraint(item: self.containerView as Any, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.toolbarView as Any, attribute: NSLayoutConstraint.Attribute.top, multiplier: 1, constant: 0)
            self.view.addConstraint(webcTop)
            self.view.layoutIfNeeded()
        }
    }
   
    func isFileChecked(_ file: OOAttachment) -> Bool {
        let c = self.checkedFileList.contains { (item) -> Bool in
            return item.id == file.id
        }
        return c
    }

}

extension CloudFileListBaseController: CloudFileCheckClickDelegate {
    func clickFolder(_ folder: OOFolder) {
        //
    }
    
    func clickFile(_ file: OOAttachment) {
        DDLogDebug("base list click file")
        if self.checkedFileList.contains(file) {
            self.checkedFileList.removeFirst(file)
        }else {
            self.checkedFileList.append(file)
        }
        self.reloadUI()
    }
}


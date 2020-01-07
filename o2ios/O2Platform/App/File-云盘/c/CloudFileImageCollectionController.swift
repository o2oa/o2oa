//
//  CloudFileImageCollectionController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/28.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import ImageSlideshow
import QuickLook

private let reuseIdentifier = "CFImageViewCell"

class CloudFileImageCollectionController: UICollectionViewController {
 
    
    private let horzationGap = 1.toCGFloat
    private var fileList: [OOAttachment] = []
    private lazy var cFileVM: CloudFileViewModel = {
        return CloudFileViewModel()
    }()
    //预览文件
    private lazy var previewVC: CloudFilePreviewController = {
        return CloudFilePreviewController()
    }()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.collectionView.register(UINib.init(nibName: reuseIdentifier, bundle: nil), forCellWithReuseIdentifier: reuseIdentifier)
        
       self.loadImageList()
    }
    
    private func loadImageList() {
        self.showLoading()
        self.cFileVM.listTypeByPage(type: .image, page: 1, count: 100).then { (result) in
            self.fileList = result
            self.hideLoading()
            self.collectionView.reloadData()
            }.catch { (error) in
                DDLogError("图片加载失败, \(error.localizedDescription)")
                self.hideLoading()
        }
    }
    
    private func previewFile(fileId: String) {
        self.showLoading()
        O2CloudFileManager.shared
            .getFileUrl(fileId: fileId)
            .always {
                self.hideLoading()
            }
            .then { (path) in
                let currentURL = NSURL(fileURLWithPath: path.path)
                DDLogDebug(currentURL.description)
                DDLogDebug(path.path)
                if QLPreviewController.canPreview(currentURL) {
                    self.previewVC.currentFileURLS.removeAll()
                    self.previewVC.currentFileURLS.append(currentURL)
                    self.previewVC.reloadData()
                    self.pushVC(self.previewVC)
                }else {
                    self.showError(title: "当前文件类型不支持预览！")
                }
            }
            .catch { (error) in
                DDLogError(error.localizedDescription)
                self.showError(title: "获取文件异常！")
        }
    }


    // MARK: UICollectionViewDataSource

    override func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }


    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.fileList.count
    }

    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {

        if let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as? CFImageViewCell
        {
            let item = self.fileList[indexPath.row]
            let url = self.cFileVM.scaleImageUrl(id: item.id!)
            DDLogDebug("url: \(url)")
            cell.setData(urlString: url)
            return cell
        }
        return UICollectionViewCell()
    }
    
    override func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        DDLogInfo("点了 row:\(indexPath.row)")
        let image = self.fileList[indexPath.row]
        self.previewFile(fileId: image.id!)
    }
    
    
     
    
}

extension CloudFileImageCollectionController: UICollectionViewDelegateFlowLayout {
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let width = CGFloat((SCREEN_WIDTH - self.horzationGap * 3.toCGFloat) / 4)
        let height = width
        return CGSize(width: width, height: height)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 3.0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 1.0
    }
}


extension CloudFileImageCollectionController: CloudFileListBaseChildDelegate {
    func reloadDataAndUI() {
        self.loadImageList()
    }
    
    func reloadUI() {
        self.collectionView.reloadData()
    }
    
    
}

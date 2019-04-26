//
//  TaskFlowCategoryViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import CocoaLumberjack

private let reuseHeaderIdentifier = "AppCategoryHeaderView"
private let reuseCellIdentifier = "AppCategoryCell"

class TaskFlowCategoryViewController: UICollectionViewController {
    
    
    var apps:[Application] = []
    
    fileprivate static var ItemLineCount:Int {
        if UIDevice.deviceModelReadable() == "iPhone 5S" || UIDevice.deviceModelReadable() == "iPhone 5" || UIDevice.deviceModelReadable() == "iPhone SE" {
            return 3
        }else{
            return 4
        }
    }
    
    fileprivate let ItemSize = Double(SCREEN_WIDTH)/Double(ItemLineCount)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.loadAppList()
    }
    
    func loadAppList(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ApplicationContext.applicationContextKey, query: ApplicationContext.applicationListQuery, parameter: nil)
        self.showMessage(title: "应用加载中...")
        self.apps.removeAll()
        Alamofire.request(url!).responseArray(queue: nil, keyPath: "data", context: nil, completionHandler: { (response:DataResponse<[Application]>) in
            switch response.result {
            case .success(let apps):
                self.apps.append(contentsOf: apps)
                self.collectionView?.reloadData()
                self.showSuccess(title: "加载完成")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "加载失败")
            }
            
        })
    }


    // MARK: UICollectionViewDataSource

    override func numberOfSections(in collectionView: UICollectionView) -> Int {
        return apps.count
    }


    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return apps[section].processList!.count
    }

    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseCellIdentifier, for: indexPath) as! AppCategoryCell
        let process = apps[(indexPath as NSIndexPath).section].processList![(indexPath as NSIndexPath).row]
        cell.appProcess = process
        return cell
    }
    
    override func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        if kind == UICollectionView.elementKindSectionHeader {
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: reuseHeaderIdentifier, for: indexPath)  as! AppCategoryHeaderView
            let app = apps[(indexPath as NSIndexPath).section]
            headerView.app = app
            headerView.backgroundColor = UIColor.lightGray
            headerView.alpha = 0.7
            return headerView
        }else{
            return UICollectionReusableView(frame: CGRect(x: 0,y: 0,width: SCREEN_WIDTH,height: 1))
        }
    }
    
    override func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let process = apps[(indexPath as NSIndexPath).section].processList![(indexPath as NSIndexPath).row]
        DDLogDebug("\(String(describing: process.toJSONString()))")
        self.performSegue(withIdentifier: "showStartFlowSegue", sender: process)
        
        
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showStartFlowSegue" {
            let destVc = segue.destination as! TaskCreateViewController
            destVc.process = sender as? AppProcess
        }
    }
    
    
    
    

}

extension TaskFlowCategoryViewController:UICollectionViewDelegateFlowLayout{
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: CGFloat(ItemSize-1),height: CGFloat(ItemSize-1))
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 1, left: 1, bottom: 1, right: 1)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        return CGSize(width: SCREEN_WIDTH,height: 40)
    }
    
    func collectionView(_ collectionView:UICollectionView,layout collectionViewLayout:UICollectionViewLayout,referenceSizeForFooterInSection section: Int) -> CGSize {
        return CGSize(width: 0,height: 0)
    }
    
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0.0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0.0
    }
    
}



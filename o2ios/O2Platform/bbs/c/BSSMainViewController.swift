//
//  BSSMainViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/3.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack

private let reuseHeaderIdentifier = "BBSHeaderCollectionReusableView"
private let reuseCellIdentifier = "BBSForumCell"

class BSSMainViewController: UIViewController {

    @IBOutlet weak var collectionView: UICollectionView!
    
    //分区及板块数据集
    var bbsForums:[BBSForumListData] = []
    
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
        self.collectionView.dataSource = self
        self.collectionView.delegate = self
        self.loadForumDataAndSectionData()
    }
    
    func loadForumDataAndSectionData(){
        self.bbsForums.removeAll()
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(BBSContext.bbsContextKey, query: BBSContext.getCategoryAndSectionQuery, parameter: nil)
        Alamofire.request(url!, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    let forumsJSON:[BBSForumListData] = Mapper<BBSForumListData>().mapArray(JSONString: JSON(val)["data"].description)!
                    self.bbsForums.append(contentsOf: forumsJSON)
                }else{
                    DDLogError(JSON(val).description)
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
            self.collectionView.reloadData()
        }
    }
    
    @IBAction func backToSuper(_ sender: UIBarButtonItem) {
        let backType = AppConfigSettings.shared.appBackType
        if backType == 1 {
            self.performSegue(withIdentifier: "backToMain", sender: nil)
        }else if backType == 2 {
            self.performSegue(withIdentifier: "backToApps", sender: nil)
        }
    }
    
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showSectionSubjectSegue" {
            let destVC = segue.destination as! BBSSubjectListViewController
            destVC.sectionData = sender as? BBSectionListData
        }
    }
    


}

extension BSSMainViewController:UICollectionViewDataSource{
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return bbsForums.count
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return bbsForums[section].sectionInfoList?.count ?? 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseCellIdentifier, for: indexPath) as! BBSForumCell
        let sectionData = bbsForums[(indexPath as NSIndexPath).section].sectionInfoList![(indexPath as NSIndexPath).row]
        cell.bbsSectionData = sectionData
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        if kind == UICollectionView.elementKindSectionHeader {
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: reuseHeaderIdentifier, for: indexPath)  as! BBSHeaderCollectionReusableView
            let forumData = bbsForums[(indexPath as NSIndexPath).section]
            headerView.bbsForumData = forumData
            headerView.backgroundColor = UIColor.white
            headerView.alpha = 0.7
            return headerView
        }else{
            return UICollectionReusableView(frame: CGRect(x: 0,y: 0,width: SCREEN_WIDTH,height: 1))
        }
    }
    

}


extension BSSMainViewController:UICollectionViewDelegateFlowLayout{
    
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
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        DDLogDebug("cell clicked")
        let sectionData = bbsForums[(indexPath as NSIndexPath).section].sectionInfoList![(indexPath as NSIndexPath).row]
        self.performSegue(withIdentifier: "showSectionSubjectSegue", sender: sectionData)
    }

}

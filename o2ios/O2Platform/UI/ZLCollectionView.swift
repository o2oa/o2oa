//
//  ZLCollectionView.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/18.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack

protocol ZLCollectionViewDelegate {
    func clickWithApp(_ app:O2App)
}

class ZLCollectionView: NSObject {
    
    fileprivate let itemNumberWithSection = 5
    
    var apps:[[O2App]] = [[],[]]
    
    var delegate:ZLCollectionViewDelegate?

    
}

extension ZLCollectionView:UICollectionViewDataSource{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 2
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return apps[section].count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "itemCell", for: indexPath) as!  O2CollectionViewCell
        let app = self.apps[indexPath.section][indexPath.row]
        cell.setAppData(app: app)
        return cell
    }
}

extension ZLCollectionView:UICollectionViewDelegateFlowLayout{
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width:SCREEN_WIDTH/CGFloat(itemNumberWithSection),height:80)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
    
    
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0.0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0.0
    }
    
}


extension ZLCollectionView:UICollectionViewDelegate{
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
            let app = self.apps[indexPath.section][indexPath.row]
            DDLogDebug("app \(app.title!) be clicked")
            self.delegate?.clickWithApp(app)
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        var reusableView:UICollectionReusableView = UICollectionReusableView(frame: .zero)
        if kind == UICollectionView.elementKindSectionHeader {
            reusableView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "OOAppMainheaderView", for: indexPath)
            let headerView = reusableView as! OOAppMainCollectionReusableView
            if indexPath.section == 0 {
                headerView.titleLabel.text = "主页应用"
            } else {
                headerView.titleLabel.text = "所有应用"
            }
        }else if kind == UICollectionView.elementKindSectionFooter {
            reusableView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "OOAppMainCollectionFooterView", for: indexPath)
        }
        return reusableView
    }
}

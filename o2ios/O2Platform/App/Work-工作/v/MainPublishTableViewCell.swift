//
//  MainPublishTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/7.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class MainPublishTableViewCell: UITableViewCell {
    
    var publishInfos:[CMS_PublishInfo] = [] {
        didSet {
            self.publishCollectionView.reloadData()
        }
    }
    
    @IBOutlet weak var publishCollectionView: UICollectionView!
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.contentView.backgroundColor = UIColor.green
        self.publishCollectionView.frame = CGRect(origin:CGPoint(x:0,y:0), size: self.bounds.size)
        //self.publishCollectionView.backgroundColor = UIColor.blue
        self.publishCollectionView.delegate = self
        self.publishCollectionView.dataSource = self
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

extension MainPublishTableViewCell:UICollectionViewDelegateFlowLayout,UICollectionViewDataSource{
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        
        if kind == UICollectionView.elementKindSectionHeader {
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "MainPublishHeaderView", for: indexPath)
            return headerView
        }else if kind == UICollectionView.elementKindSectionFooter{
            let footerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "MainPublishFooterView", for: indexPath)
            return footerView
        }
        return UICollectionReusableView()
        
    }
    
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "MainPublishContentCell", for: indexPath) as! MainPublishContentCell
        cell.publishInfos.removeAll(keepingCapacity: true)
        cell.publishInfos.append(contentsOf: self.publishInfos)
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width:SCREEN_WIDTH - 125.0, height: 100.0)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 1, left: 1, bottom: 1, right: 1)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        return CGSize(width: 65.0,height: 100.0)
    }
    
    func collectionView(_ collectionView:UICollectionView,layout collectionViewLayout:UICollectionViewLayout,referenceSizeForFooterInSection section: Int) -> CGSize {
        return CGSize(width: 60.0,height: 100.0)
    }
    
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0.5
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0.5
    }
    

    
}

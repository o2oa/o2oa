//
//  MainAppActionCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/27.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

protocol MainAppActionCellDelegate {
    func clickWithApp(_ app:O2App)
}

class MainAppActionCell: UITableViewCell{
    
    
    @IBOutlet weak var appCollectionView: UICollectionView!
    
    fileprivate let collectionViewDelegate = ZLCollectionView()
    
    var delegate:MainAppActionCellDelegate?
    
    
    var cellHeight:CGFloat {
        get {
            return 200.0
        }
    }
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        //DDLogDebug("itemSize = \(self.collectionViewDelegate.ItemWithSize)")
        self.collectionViewDelegate.delegate = self
        self.appCollectionView.dataSource = self.collectionViewDelegate
        self.appCollectionView.delegate = self.collectionViewDelegate
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

}

extension MainAppActionCell:ZLCollectionViewDelegate{
    func clickWithApp(_ app: O2App) {
        self.delegate?.clickWithApp(app)
    }
}



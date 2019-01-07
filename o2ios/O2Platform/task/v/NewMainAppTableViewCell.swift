//
//  NewMainAppTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/12.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import EmptyDataSet_Swift

protocol NewMainAppTableViewCellDelegate {
    func NewMainAppTableViewCellWithApp(_ app:O2App)
    func emptyTapClick()
}

class NewMainAppTableViewCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var appCollectionView: UICollectionView!
    
    private var collectionViewDelegate = NewMainCollectionView()
    
    var delegate:NewMainAppTableViewCellDelegate!
    
    var apps:[O2App] = [] {
        didSet {
            collectionViewDelegate.apps.removeAll()
            collectionViewDelegate.apps.append(contentsOf: apps)
            self.appCollectionView.reloadData()
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        //实现点击反向代理
        self.collectionViewDelegate.delegate  = self
        //空数据集显示
        self.appCollectionView.emptyDataSetSource = collectionViewDelegate
        self.appCollectionView.emptyDataSetDelegate = collectionViewDelegate
        
//        //实现数据展现代理
        self.appCollectionView.dataSource = collectionViewDelegate
        self.appCollectionView.delegate = collectionViewDelegate
        
        //self.appCollectionView.reloadData()
        
    }
    
    func config(withItem item: Any?) {
        guard let myApps = item as? [O2App] else {
            return
        }
        apps.removeAll()
        apps.append(contentsOf: myApps)
        //self.appCollectionView.reloadData()
    }
    

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

extension NewMainAppTableViewCell:NewMainCollectionViewDelegate{
    func NewMainCollectionViewItemClickWithApp(_ app: O2App) {
        if let d = delegate {
            d.NewMainAppTableViewCellWithApp(app)
        }
    }
    
    func emptyTapClick() {
        if let d = delegate {
            d.emptyTapClick()
        }
    }
}

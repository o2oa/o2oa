//
//  OOSelectPersonTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/4.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOSelectPersonTableViewCell: UITableViewCell,Configurable {
    
    
    @IBOutlet weak var personCollectionView: UICollectionView!
    
    private var models:[OOPersonModel] = []
    
    override func awakeFromNib() {
        super.awakeFromNib()
        personCollectionView.register(UINib.init(nibName: "OOPersonCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "personItemCell")
        personCollectionView.dataSource  = self
        personCollectionView.delegate = self
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let items =  item as? [OOPersonModel] else {
            return
        }
        models = items
        personCollectionView.reloadData()
    }
    
}

extension OOSelectPersonTableViewCell:UICollectionViewDelegate,UICollectionViewDataSource {
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return models.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell =  collectionView.dequeueReusableCell(withReuseIdentifier: "personItemCell", for: indexPath) as! (OOPersonCollectionViewCell & Configurable)
        cell.config(withItem: models[indexPath.row])
        return cell
        
    }
    
    
}

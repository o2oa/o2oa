//
//  OOPersonCollectionViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/4.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOPersonCollectionViewCell: UICollectionViewCell,Configurable {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    private lazy var viewModel:OOPersonListViewModel =  {
        return OOPersonListViewModel()
    }()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func config(withItem item: Any?) {
        guard let model = item as? OOPersonModel else {
            return
        }
        nameLabel.text = model.name
        
        viewModel.getIconOfPerson(model, compeletionBlock: { (iconImage, errMsg) in
            
            if errMsg == nil {
                self.iconImageView.image = iconImage
            }else{
                print(errMsg!)
            }
            
        })
        
        
    }

}

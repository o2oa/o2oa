//
//  OOAttandanceWorkPlaceCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/21.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOAttandanceWorkPlaceCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var placeName: UILabel!
    
    @IBOutlet weak var placeAlias: UILabel!
    
    @IBOutlet weak var creator: UILabel!
    
    @IBOutlet weak var longitude: UILabel!
    
    @IBOutlet weak var latitude: UILabel!
    
    @IBOutlet weak var errorRange: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let model = item as? OOAttandanceWorkPlace else {
            return
        }
        placeName.text = model.placeName
        placeAlias.text = model.placeAlias
        creator.text = model.creator
        longitude.text = model.longitude
        latitude.text = model.latitude
        errorRange.text = String(model.errorRange ?? 0)
    }
    
}

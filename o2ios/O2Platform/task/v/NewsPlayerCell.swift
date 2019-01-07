//
//  NewsPlayerCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/27.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

protocol NewsPlayerCellDelegate {
    func clickItemIndex(_ imageIndex:Int)
}


class NewsPlayerCell: UITableViewCell {
    
    
    var delegate:NewsPlayerCellDelegate?
    
    var playImages:[String]?
    
    var playerHeight:CGFloat = CGFloat(162.0)
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func configPlayer(_ playImages:[String]){
        let imageScrollView = ZLImageScrollView(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: self.playerHeight), withImages: playImages)
        imageScrollView?.addTapEventForImage { imageIndex in
            self.delegate?.clickItemIndex(imageIndex)
        }
        self.contentView.addSubview(imageScrollView!)
    }
    
    

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

//
//  ImageSlidesShowTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/5.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import ImageSlideshow
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper

protocol ImageSlidesShowTableViewCellDelegate {
    func  ImageSlidesShowClick(taskImageshowEntity:TaskImageshowEntity)
}

class ImageSlidesShowTableViewCell: UITableViewCell {
    
    @IBOutlet weak var imageSlideshow: ImageSlideshow!
    
    var delegate:ImageSlidesShowTableViewCellDelegate?
    
    var imageshowEntitys:[TaskImageshowEntity] = [] {
        didSet {
            self.imageURLS.removeAll(keepingCapacity: true)
            imageshowEntitys.forEach { (taskImageshowEntity) in
                let afurl = O2AlamofireSource(urlString: taskImageshowEntity.url!)
                self.imageURLS.append(afurl!)
            }
//            imageshowEntitys.forEachEnumerated { (index,taskImageshowEntity)  in
//                let afurl = O2AlamofireSource(urlString: taskImageshowEntity.url!)
//                self.imageURLS.append(afurl!)
//            }
            imageSlideshow.setImageInputs(imageURLS)
            
        }
    }
    
    fileprivate var imageURLS:[O2AlamofireSource] = []
    
    

    override func awakeFromNib() {
        super.awakeFromNib()
        imageSlideshow.backgroundColor = UIColor.white
        imageSlideshow.slideshowInterval = 15.0
        imageSlideshow.preload = .all
        imageSlideshow.pageControlPosition = PageControlPosition.insideScrollView
        imageSlideshow.pageControl.currentPageIndicatorTintColor = UIColor.lightGray;
        imageSlideshow.pageControl.pageIndicatorTintColor = UIColor.black;
        imageSlideshow.contentScaleMode = UIView.ContentMode.scaleToFill
        imageSlideshow.addTapGesture(target: self, action: #selector(imageSlideshowClick(sender:)))
        
    }
    
    public func imageSlideshowClick(sender:ImageSlideshow?){
      //print(self.imageSlideshow.currentPage)
        if delegate != nil{
            let entity = self.imageshowEntitys[self.imageSlideshow.currentPage]
           self.delegate?.ImageSlidesShowClick(taskImageshowEntity: entity)
        }
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    
    

}
